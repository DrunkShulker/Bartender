package com.drunkshulker.bartender.client.module;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.List;
import java.util.function.Predicate;

import baritone.api.pathing.goals.GoalGetToBlock;
import baritone.api.pathing.goals.GoalRunAway;
import com.drunkshulker.bartender.Bartender;
import com.drunkshulker.bartender.client.gui.clickgui.ClickGuiSetting;
import com.drunkshulker.bartender.client.ipc.IPCHandler;
import com.drunkshulker.bartender.client.social.PlayerFriends;
import com.drunkshulker.bartender.client.social.PlayerGroup;

import baritone.api.BaritoneAPI;
import baritone.api.Settings;
import baritone.api.pathing.goals.GoalXZ;
import com.drunkshulker.bartender.util.kami.EntityUtils;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static com.drunkshulker.bartender.client.module.Aura.entityPriority;
import static com.drunkshulker.bartender.client.module.Aura.reach;

public class Bodyguard {

	enum AttackBehavior{
		ALWAYS_ATTACK, 
		ATTACK_ON_COMMAND, 
	}
	
	enum ProtectedDisconnectBehavior{	
		RUN,
		AVENGE,
		DISCONNECT,
	}

	public enum Friendly{
		NONE,
		IMPACT_FRIENDS,
		FRIENDS_LIST,
		BOTH,
	}

	enum BodyguardTask {
		IDLE_AS_GROUP,
		IDLE_ALONE,
		FIND_PROTECTED,
		FIGHT_AS_GROUP,
		RUN_AWAY,
		FIGHT_ALONE,
		FLYING,
		DIG_TO_GROUND_LEVEL,
		MINE_TARGET_BLOCKS,
		GO_TO_STAY,
	}

	public static BodyguardTask currentTask = BodyguardTask.IDLE_AS_GROUP;
	public static AttackBehavior attackBehavior;
	public static ProtectedDisconnectBehavior pDisconnectBehavior;

	public static Friendly friendly = Friendly.NONE;
	public static BlockPos lastKnownProtectedLocation = null;
	public static String currentFollowTarget = null;
	public static ArrayList<String> nearbyGroup = new ArrayList<>();
	private static ArrayList<String> nearbyNoGroup = new ArrayList<>();
	private static Entity mainAccEntity = null;
	private static int entityRenderDistance = 10; 
	private static int runAwayDistance = 50000;
	public static boolean autoTakeOff = true;
	private static boolean useFollowOffset = false;
	private static BlockPos lastPos;

	private static boolean freeLook = false;
	public static ArrayList<String> currentEnemies = new ArrayList<>();
	public static ArrayList<EntityPlayer> inRangeEnemies = new ArrayList<>();
	public static int protectedDimension;
	static long lastTakeOffSpaceStamp = 0;

	
	
	public static Predicate<Entity> currentPredicate = entity -> {
		if(currentFollowTarget==null) return false;
		if(!EntityUtils.isPlayer(entity)) return false;
		if(currentFollowTarget.equals(((EntityPlayer) entity).getDisplayNameString())) return true;
		return false;
	};

	public static boolean enabled = false, lastEnabled = false; 
	public static boolean dontMove = false; 

	
	public static boolean protectedOutOfRange(){
		return mainAccEntity==null;
	}
	
	public static boolean protectedDisconnected(){
		return !PlayerGroup.isPlayerOnline(PlayerGroup.mainAccount);
	}

	public static void setup() {
		Settings s = BaritoneAPI.getSettings();
		s.allowSprint.value = true;
		s.sprintInWater.value = true;
		s.allowBreak.value = true;
		s.allowPlace.value = true;	
		s.allowWaterBucketFall.value = true;
		s.allowJumpAt256.value = true;
		s.allowDiagonalAscend.value = true;
		s.allowDiagonalDescend.value = true;
		s.allowOvershootDiagonalDescend.value = true;
		s.walkWhileBreaking.value = true;
		s.allowParkourPlace.value = true;
		s.allowParkourAscend.value = true;
		s.allowParkour.value = true;
		s.allowDownward.value = true;		
		s.assumeWalkOnWater.value = true;
		s.assumeWalkOnLava.value = false;
		s.assumeSafeWalk.value = true;
		s.chatControl.value = true;
		s.freeLook.value = true;
		s.maxFallHeightBucket.value = 255;
		s.censorCoordinates.value = true;
		s.censorRanCommands.value = true;
		s.maxFallHeightNoWater.value=255;
		s.mobSpawnerAvoidanceRadius.value = 0;
		s.sprintAscends.value=true;
		s.chunkCaching.value=false;
	}

	private static long followPosDrawIntervalMillis = 3000, currentFollowPosDrawMillis = 0;
	private void redrawFollowPos() {
		if(System.currentTimeMillis()-currentFollowPosDrawMillis>followPosDrawIntervalMillis){
			currentFollowPosDrawMillis=System.currentTimeMillis();
			
			
			
			if(currentFollowTarget==null||Minecraft.getMinecraft().player==null) return;
			EntityPlayer targetEntity = EntityRadar.getEntityPlayer(currentFollowTarget);
			if(targetEntity == null) return;
			if(targetEntity.getPosition().getY()<5
					||(getGroundLevel(targetEntity.getPosition())!=null&&getGroundLevel(targetEntity.getPosition()).getY()-1>Minecraft.getMinecraft().player.getPosition().y)
					||!useFollowOffset
			){ 
				
				
				Settings s = BaritoneAPI.getSettings();
				s.followOffsetDirection.value = 0f;
				s.followOffsetDistance.value = 0.0;
				s.followRadius.value = 0;
			}
			
			else{
				
				
				Settings s = BaritoneAPI.getSettings();
				Random r = Minecraft.getMinecraft().player.rand;
				s.followOffsetDirection.value = 360*r.nextFloat();
				s.followOffsetDistance.value = 0.6 + (r.nextDouble()*3);
				
				s.followRadius.value = 0;
			}
		}
	}

	@SubscribeEvent
	public void playerTick(InputUpdateEvent event) {
		Minecraft mc = Minecraft.getMinecraft();
		if(mc.player==null) return;
		if(PlayerGroup.mainAccount.equals(mc.player.getDisplayNameString())) {
			return; 
		}
		if(!PlayerGroup.members.contains(PlayerGroup.mainAccount)){
			return; 
		}

		if(!enabled){
			lastEnabled = false;
			if(BaritoneAPI.getProvider().getPrimaryBaritone().getFollowProcess().isActive()) {
				BaritoneAPI.getProvider().getPrimaryBaritone().getFollowProcess().cancel();
				return;
			} else return;
		}
		else if(!lastEnabled){
			setup();
			lastEnabled = true;
		}

		
		if(!mc.player.isElytraFlying())redrawFollowPos();

		
		if(PlayerGroup.mainAccount.equals(mc.player.getDisplayNameString())) mainAccEntity = null;
		else mainAccEntity = EntityRadar.getEntityPlayer(PlayerGroup.mainAccount);
		
		if(mainAccEntity!=null) {
			lastKnownProtectedLocation = mainAccEntity.getPosition(); 
			protectedDimension = mainAccEntity.dimension;
		}else if(lastKnownProtectedLocation==null&&PlayerGroup.isPlayerOnline(PlayerGroup.mainAccount)){
			askCoordsFromProtected();
		}

		
		nearbyGroup = EntityRadar.nearbyGroupMembers();
		nearbyNoGroup = EntityRadar.nearbyPlayersNoGroup();
		Collections.sort(currentEnemies);

		
		detectTeleported();

		
		if(takeOffInProgress){
			


			
			BaseFinder.lookAt(mc.player.getPositionVector().x,999,mc.player.getPositionVector().z,mc.player,false);
			event.getMovementInput().moveForward = 1f;
			event.getMovementInput().jump = true; 

			
			if(forceEasyTakeoff){
				if(FlightBot.useBartenderFlight) ElytraFlight.easyTakeoff = true;
			}
			
			if(System.currentTimeMillis()-lastTakeOffSpaceStamp > FlightBot.takeOffDelay){
				lastTakeOffSpaceStamp = System.currentTimeMillis();
				
				


				if(Bartender.MC.player.isElytraFlying()) {
					forceEasyTakeoff = false;
					takeOffInProgress = false;
					if(FlightBot.useBartenderFlight) ElytraFlight.easyTakeoff = false;
				}

			}
			return;
		}

		
		if(protectedOutOfRange()){
			if(protectedDisconnected()){
				handleProtectedDisconnect();

			}else if(mc.player.dimension!=protectedDimension){
				setTask(BodyguardTask.FIND_PROTECTED);
			}else {
				if(!mc.player.isElytraFlying()) {
					setTask(BodyguardTask.FIND_PROTECTED);
				} else {
					FlightBot.currentFlyTask = FlightBot.FlyTask.FIND_PROTECTED; return;
				}
			}
		}
		
		else if(currentEnemies.isEmpty()){
			if(!mc.player.isElytraFlying()&&!dontDistruptCurrentTask()) setTask(BodyguardTask.IDLE_AS_GROUP);
		}
		
		else {
			if(!mc.player.isElytraFlying()) setTask(BodyguardTask.FIGHT_AS_GROUP);
		}

		
		
		

		
		if(mc.player.isElytraFlying()){
			calculateEnemyToGangbang();
			setTask(BodyguardTask.FLYING);
			stopFollowProcess();
			return;
		}
		
		else if(autoTakeOff&&currentEnemies.isEmpty()){
			
			EntityPlayer mainAccE = EntityRadar.getEntityPlayer(PlayerGroup.mainAccount);
			if(mainAccE!=null&&mainAccE.isElytraFlying()){
				takeOff();
			}
		}

		if(currentTask==BodyguardTask.IDLE_AS_GROUP){
			currentFollowTarget=PlayerGroup.mainAccount;
			followTarget();
			faceTheTarget();
		}
		else if(currentTask==BodyguardTask.FIND_PROTECTED){
			
			if(lastKnownProtectedLocation==null){
				askCoordsFromProtected();
			}
			
			else if(!mc.player.isElytraFlying()
					&&protectedDimension==mc.player.dimension
					&&BaseFinder.distance2D(lastKnownProtectedLocation,mc.player.getPosition())<100
					&&BaseFinder.distance2D(lastKnownProtectedLocation,mc.player.getPosition())>3){
				
				if(BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().isActive()) {
					if(BaseFinder.distance2D(mc.player.getPosition(),lastKnownProtectedLocation)<5)askCoordsFromProtected();
				}
				
				else{
					BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoalAndPath(
							new GoalXZ(lastKnownProtectedLocation.x,lastKnownProtectedLocation.z));
				}
			}
			
			else {
				endAllTasks(false);
				askTpFromProtected();
			}
		}
		else if(currentTask==BodyguardTask.FIGHT_ALONE){
			calculateEnemyToBang();
			followTarget();
			faceTheTarget();
		}
		else if(currentTask==BodyguardTask.FIGHT_AS_GROUP){
			calculateEnemyToGangbang();
			followTarget();
			faceTheTarget();
		}
	}

	private void detectTeleported() {
		EntityPlayerSP player =  Minecraft.getMinecraft().player;
		if(player==null) return;
		if(lastPos==null
				||BaseFinder.distance2D(lastPos, player.getPosition())>90){
			lastPos = player.getPosition();
			endAllTasks(false);
		}
	}

	public static void handleProtectedDisconnect() {
		
		if(pDisconnectBehavior==ProtectedDisconnectBehavior.DISCONNECT) BaseFinder.logOut("bodyguard protected disconnect behavior");
		else if(pDisconnectBehavior==ProtectedDisconnectBehavior.RUN) setTask(BodyguardTask.RUN_AWAY);
		else if(!currentEnemies.isEmpty()) setTask(BodyguardTask.FIGHT_ALONE);
		else setTask(BodyguardTask.IDLE_ALONE);
	}

	public static void eatChorus(boolean spreadMessage) {
		EntityPlayerSP player =  Minecraft.getMinecraft().player;
		if (player==null) return;
		if(!AutoEat.enabled) Bartender.msg("Cannot eat chorus! Auto eat is not enabled.");
		AutoEat.chorus();
		if(spreadMessage){
			IPCHandler.push(IPCHandler.CHORUS,player.getDisplayNameString(), player.getPosition().x,player.getPosition().y, player.getPosition().z);
		}
	}

	public static void startMineNearbyBlocks(Block targetBlock, boolean spreadMessage){
		EntityPlayerSP player =  Minecraft.getMinecraft().player;
		if(player==null||player.isElytraFlying()) {
			return;
		}
		
		if(enabled&&!PlayerGroup.mainAccount.equals(player.getDisplayNameString())){
			endAllTasks(false);
			setTask(BodyguardTask.MINE_TARGET_BLOCKS);
			BaritoneAPI.getProvider().getPrimaryBaritone().getMineProcess().mine(targetBlock);
		}
		
		if(spreadMessage){
			IPCHandler.push(IPCHandler.START_MINING_OBBY,player.getDisplayNameString(), player.getPosition().x,player.getPosition().y, player.getPosition().z);
		}
	}

	public static void endAllTasks(boolean spreadForward) {
		
		AutoBuild.stop();
		
		if(BaritoneAPI.getProvider().getPrimaryBaritone().getMineProcess().isActive()) BaritoneAPI.getProvider().getPrimaryBaritone().getMineProcess().cancel();
		
		stopFollowProcess();
		
		BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().cancelEverything();

		if(FlightBot.useBartenderFlight&&ElytraFlight.enabled&&Bartender.MC.player!=null&&!Bartender.MC.player.isElytraFlying()) ElytraFlight.reset(false);

		setTask(BodyguardTask.IDLE_ALONE);
		if(spreadForward){
			EntityPlayerSP p = Minecraft.getMinecraft().player;
			IPCHandler.push(IPCHandler.END_ALL_TASKS,p.getDisplayNameString(),p.getPosition().x,p.getPosition().y,p.getPosition().z);
		}
	}

	public static void receiveGoToGroundCommand() {
		endAllTasks(false);

		EntityPlayerSP player =  Minecraft.getMinecraft().player;
		if(player==null||player.isElytraFlying()) {
			return;
		}

		int randomOffeet = 9;
		BlockPos groundLevel = getGroundLevel(player.getPosition()
				.add(new BlockPos(player.rand.nextInt(randomOffeet )*((player.rand.nextFloat()>0.5)?1:-1),0,player.rand.nextInt(randomOffeet )*((player.rand.nextFloat()>0.5)?1:-1))
				.add(new BlockPos(player.rand.nextInt(randomOffeet )*((player.rand.nextFloat()>0.5)?1:-1),0,player.rand.nextInt(randomOffeet )*((player.rand.nextFloat()>0.5)?1:-1))))); 

		if(groundLevel==null){
			Bartender.msg("Cannot calculate ground level. Cancelled.");
			return;
		}
		BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoalAndPath(new GoalGetToBlock(groundLevel));
		setTask(BodyguardTask.DIG_TO_GROUND_LEVEL);
	}

	public static void receiveDimensionUpdate(String senderName, int dimension) {
		
		if(senderName.equals(PlayerGroup.mainAccount)) protectedDimension = dimension;
	}

	public static void receiveGoToCommand(BlockPos blockPos) {
		EntityPlayerSP ep = Minecraft.getMinecraft().player;
		if(ep==null||ep.dimension!=protectedDimension||ep.getDisplayNameString().equals(PlayerGroup.mainAccount)){
			return;
		}
		lastKnownProtectedLocation = blockPos;
		if(ep.isElytraFlying()){ 
			eatChorus(false);
		}
		endAllTasks(false);
		setTask(BodyguardTask.GO_TO_STAY);
		BaritoneAPI.getSettings().enterPortal.value = true;
		BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoalAndPath(new GoalGetToBlock(blockPos));
	}

	private boolean dontDistruptCurrentTask(){
		return currentTask==BodyguardTask.DIG_TO_GROUND_LEVEL
			|| currentTask==BodyguardTask.GO_TO_STAY
			|| currentTask==BodyguardTask.RUN_AWAY
			|| currentTask==BodyguardTask.MINE_TARGET_BLOCKS;
	}

	
	private void calculateEnemyToBang() {
		
		inRangeEnemies.clear();
		for (String enemy:currentEnemies) {
			EntityPlayer enemyEntity = EntityRadar.getEntityPlayer(enemy);
			if(enemyEntity==null){
				continue;
			}else{
				inRangeEnemies.add(enemyEntity);
			}
		}
		
		EntityPlayer nearest = null;
		for (EntityPlayer enemy:inRangeEnemies) {
			
			if(nearest==null){nearest=enemy; continue;}
			if(BaseFinder.distance2D(enemy.getPosition(),Minecraft.getMinecraft().player.getPosition())
					<BaseFinder.distance2D(nearest.getPosition(),Minecraft.getMinecraft().player.getPosition()))
			{nearest=enemy; continue;}
		}
		
		if(nearest==null){
			
		}
		
		else currentFollowTarget=nearest.getDisplayNameString();
	}

	
	private void calculateEnemyToGangbang() {
		
		inRangeEnemies.clear();
		for (String enemy:currentEnemies) {
			EntityPlayer enemyEntity = EntityRadar.getEntityPlayer(enemy);
			if(enemyEntity==null){
				continue;
			}else{
				if(enemyEntity.isDead){
					
					currentEnemies.remove(enemy);
					return;
				}
				BlockPos enemyPos = enemyEntity.getPosition();
				int enemyDistanceFromProtected = BaseFinder.distance2D(enemyPos,lastKnownProtectedLocation);
				if(enemyDistanceFromProtected<entityRenderDistance-9){
					inRangeEnemies.add(enemyEntity);
				}
			}
		}
		
		EntityPlayer nearestToProtected= null;
		for (EntityPlayer enemy:inRangeEnemies) {
			
			if(nearestToProtected==null){nearestToProtected=enemy; continue;}
			if(BaseFinder.distance2D(enemy.getPosition(),lastKnownProtectedLocation)<BaseFinder.distance2D(nearestToProtected.getPosition(),lastKnownProtectedLocation))
			{nearestToProtected=enemy; continue;}
		}
		
		if(nearestToProtected==null){currentFollowTarget=PlayerGroup.mainAccount;}
		
		else currentFollowTarget=nearestToProtected.getDisplayNameString();
	}


	

	public static void localDimensionChanged() {
		EntityPlayerSP player = Minecraft.getMinecraft().player;
		if(player==null) return;
		if(!player.isElytraFlying()) endAllTasks(false);
	}

	private static void setTask(BodyguardTask task){
		if(task!=BodyguardTask.FLYING&&Minecraft.getMinecraft().player.isElytraFlying()) return; 
		if(currentTask==task) return; 
		else {
			BodyguardTask previousTask = currentTask;
			currentTask = task;
			onTaskChanged(previousTask, currentTask);
		}
	}

	private static void onTaskChanged(BodyguardTask fromTask, BodyguardTask toTask){
		
		switch (toTask){
			case FLYING:
				FlightBot.enabled = true;
				endAllTasks(false);
				break;
			case RUN_AWAY:
				runAway();
				break;
		}

		
		switch (fromTask){
			case FLYING:
				FlightBot.enabled = false;
				if(ElytraFlight.enabled) ClickGuiSetting.handleClick(ClickGuiSetting.fromString("elytra+->state"), false); 
				break;
			case GO_TO_STAY:
				BaritoneAPI.getSettings().enterPortal.value = false;
				break;
		}
	}

	private static void runAway() {
		endAllTasks(false);
		if(Minecraft.getMinecraft().player.isElytraFlying()){
			
			BaseFinder.logOut("bodyguard runaway does not support flying yet");
		}else{
			BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoalAndPath(new GoalRunAway(runAwayDistance));
		}
	}

	private static void stopFollowProcess() {
		if(Minecraft.getMinecraft().player==null) return;
		if(BaritoneAPI.getProvider().getPrimaryBaritone().getFollowProcess().isActive()){
			BaritoneAPI.getProvider().getPrimaryBaritone().getFollowProcess().cancel();
		}
	}


	public static void sendGoToCommand() {
		EntityPlayerSP player = Minecraft.getMinecraft().player;
		IPCHandler.push(IPCHandler.GO_TO, player.getDisplayNameString(), player.getPosition().x, player.getPosition().y, player.getPosition().z);
	}

	public static void sendGoToGroundCommand() {
		EntityPlayerSP player = Minecraft.getMinecraft().player;
		IPCHandler.push(IPCHandler.GET_TO_GROUND_LEVEL, player.getDisplayNameString(), player.getPosition().x, player.getPosition().y, player.getPosition().z);
	}

	static boolean takeOffInProgress = false, forceEasyTakeoff = false;
	public static void takeOff() {
		forceEasyTakeoff = false;
		EntityPlayerSP player = Minecraft.getMinecraft().player;
		if(player==null||player.getDisplayNameString().equals(PlayerGroup.mainAccount)) return;
		
		if(FlightBot.useBartenderFlight&&!ElytraFlight.enabled) ClickGuiSetting.handleClick(ClickGuiSetting.fromString("elytra+->state"), false); 

		
		AutoBuild.stop();

		
		if(BaritoneAPI.getProvider().getPrimaryBaritone().getMineProcess().isActive()) BaritoneAPI.getProvider().getPrimaryBaritone().getMineProcess().cancel();
		
		stopFollowProcess();
		
		BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().cancelEverything();


		if(!takeOffInProgress){
			takeOffInProgress = true;
			Timer timer = new Timer();
			timer.schedule(new TimerTask()
			{
				public void run()
				{
					forceEasyTakeoff = true;
					timer.cancel();
				}
			}, FlightBot.takeOffDelay);
			Timer timer2 = new Timer();
			timer.schedule(new TimerTask()
			{
				public void run()
				{
					takeOffInProgress = false;
					forceEasyTakeoff = false;
					ElytraFlight.spacePressed = false;
					if(FlightBot.useBartenderFlight) ElytraFlight.easyTakeoff = false;
					player.setSneaking(true);
					if(FlightBot.useBartenderFlight) ElytraFlight.reset(true);
					timer2.cancel();
				}
			}, FlightBot.takeOffMaxTime); 
		}
	}

	public static void sendTakeOffCommand() {
		EntityPlayerSP player = Minecraft.getMinecraft().player;
		IPCHandler.push(IPCHandler.TAKEOFF, player.getDisplayNameString(), player.getPosition().x, player.getPosition().y, player.getPosition().z);
	}

	public static void sendDisconnectCommand() {
		EntityPlayerSP player = Minecraft.getMinecraft().player;
		IPCHandler.push(IPCHandler.DISCONNECT, player.getDisplayNameString(), player.getPosition().x, player.getPosition().y, player.getPosition().z);
		BaseFinder.logOut("Group disconnect");
	}

	private static void followTarget(){
		
		if(currentTask==BodyguardTask.FLYING){
			stopFollowProcess();
			return;
		}

		if(!BaritoneAPI.getProvider().getPrimaryBaritone().getFollowProcess().isActive()){
			
			BaritoneAPI.getProvider().getPrimaryBaritone().getFollowProcess().follow(currentPredicate);
		}
		else{
			List<Entity> targets = BaritoneAPI.getProvider().getPrimaryBaritone().getFollowProcess().following();
			if(targets == null){
				
				BaritoneAPI.getProvider().getPrimaryBaritone().getFollowProcess().cancel();
				if(nearbyGroup.isEmpty())setTask(BodyguardTask.IDLE_ALONE);
				else setTask(BodyguardTask.IDLE_AS_GROUP);
			}
			else if(targets.isEmpty()){
				
				if(nearbyGroup.isEmpty())setTask(BodyguardTask.IDLE_ALONE);
				else setTask(BodyguardTask.IDLE_AS_GROUP);
			}
			else{
				if(currentFollowTarget.equals(PlayerGroup.mainAccount)){
					
				}else{
					
				}
			}
		}
	}

	
	public static BlockPos getGroundLevel(BlockPos coords){
		Minecraft mc = Minecraft.getMinecraft();
		
		if(mc.player==null||mc.player.dimension==-1) return null;
		
		Chunk chunk = Minecraft.getMinecraft().world.getChunk(Minecraft.getMinecraft().player.getPosition());
		if(!chunk.loaded) return null;
		
		for (int i = 250; i > 0; i--) {
			BlockPos bp = new BlockPos (mc.player.getPosition().getX(),i,mc.player.getPosition().getZ());
			if(!isBlockAir(mc.player.world, bp)){
				return bp;
			}
		}
		return null;
	}

	public static boolean isBlockAir(final World w, final BlockPos pos ) {
		try
		{
			return w.getBlockState( pos ).getBlock().isAir( w.getBlockState( pos ), w, pos );
		}
		catch( final Throwable e )
		{
			return false;
		}
	}

	public static void receiveCoordsRequest() {
		sendCoordsMessage();
	}

	public static void fallBack(boolean spreadForward){
		clearEnemyList();

		Minecraft mc = Minecraft.getMinecraft();
		EntityPlayerSP player = mc.player;
		if(player==null) return;

		if(!spreadForward) return;
		sendFallBackMessage();
	}

	static long lastSendTpahereStamp = 0;
	public static void reveiveMainTpaRequest(String sender) {
		if(System.currentTimeMillis()-lastSendTpahereStamp<10000) return;
		lastSendTpahereStamp = System.currentTimeMillis();
		Minecraft mc = Minecraft.getMinecraft();
		EntityPlayerSP player = mc.player;
		if(player==null) return;
		if(player.getDisplayNameString().equals(PlayerGroup.mainAccount)){
			player.sendChatMessage("/tpahere "+sender);
		}
	}

	static void sendFallBackMessage() {
		Minecraft mc = Minecraft.getMinecraft();
		EntityPlayerSP player = mc.player;
		if(player==null) return;
		IPCHandler.push(IPCHandler.FALLBACK,player.getDisplayNameString(), player.getPosition().x, player.getPosition().y, player.getPosition().z);
	}

	private static void clearEnemyList() {
		currentEnemies.clear();
		inRangeEnemies.clear();
	}

	public static void faceTheTarget() {
		if(freeLook) return;
		final double eyeLift=0.3;
		if(currentTask==BodyguardTask.FLYING) return;

		if(currentFollowTarget!=null&&currentFollowTarget.equals(PlayerGroup.mainAccount)){
			
			if(!currentEnemies.isEmpty()){
				EntityPlayer nearestEnemy = null;
				for (String ep:currentEnemies) {
					EntityPlayer c = EntityRadar.getEntityPlayer(ep);
					if(nearestEnemy==null) nearestEnemy=c;
					else if(c.getDistance(Minecraft.getMinecraft().player)<nearestEnemy.getDistance(Minecraft.getMinecraft().player)) nearestEnemy=c;
				}
				if(nearestEnemy==null||nearestEnemy.isDead) return;
				Vec3d eyesPos = nearestEnemy.getPositionVector();
				if(!nearestEnemy.isElytraFlying()) eyesPos = eyesPos.add(0, eyeLift, 0);
				BaseFinder.lookAt(eyesPos.x, eyesPos.y, eyesPos.z, Minecraft.getMinecraft().player, false);
			}
			
			else if(!nearbyNoGroup.isEmpty()){
				EntityPlayer nearestEnemy = null;
				for (String ep:nearbyNoGroup) {
					EntityPlayer c = EntityRadar.getEntityPlayer(ep);
					if(nearestEnemy==null) nearestEnemy=c;
					else if(c.getDistance(Minecraft.getMinecraft().player)<nearestEnemy.getDistance(Minecraft.getMinecraft().player)) nearestEnemy=c;
				}
				if(nearestEnemy==null||nearestEnemy.isDead) return;
				Vec3d eyesPos = nearestEnemy.getPositionVector();
				if(!nearestEnemy.isElytraFlying()) eyesPos = eyesPos.add(0, eyeLift, 0);
				BaseFinder.lookAt(eyesPos.x, eyesPos.y, eyesPos.z, Minecraft.getMinecraft().player, false);
			}
		}
		else{
			
			EntityPlayer entityToLook = EntityRadar.getEntityPlayer(currentFollowTarget);
			if(entityToLook==null||entityToLook.isDead) return;
			Vec3d eyesPos =entityToLook.getPositionVector();
			if(!entityToLook.isElytraFlying()) eyesPos = eyesPos.add(0, eyeLift, 0);
			BaseFinder.lookAt(eyesPos.x, eyesPos.y, eyesPos.z, Minecraft.getMinecraft().player, false);
		}
	}


	public static void removeEnemy(String enemyToRemove, boolean spreadMessage) {
		if(currentEnemies.contains(enemyToRemove))currentEnemies.remove(enemyToRemove);
		if(spreadMessage){
			EntityPlayerSP player = Minecraft.getMinecraft().player;
			if(player==null) return;
			IPCHandler.push(IPCHandler.REMOVE_ENEMY,enemyToRemove, player.getPosition().x, player.getPosition().y, player.getPosition().z);
		}
	}

	public static void addEnemy(String enemyToAdd, boolean spreadMessage) {
		
		if((friendly==Friendly.IMPACT_FRIENDS||friendly==Friendly.BOTH)&& PlayerFriends.impactFriends.contains(enemyToAdd)) return;
		if((friendly==Friendly.FRIENDS_LIST||friendly==Friendly.BOTH)&& PlayerFriends.friends.contains(enemyToAdd)) return;
		if(PlayerGroup.members.contains(enemyToAdd)) return;

		if(!currentEnemies.contains(enemyToAdd)) {
			currentEnemies.add(enemyToAdd);
		}

		if(PlayerGroup.DEFAULTS.contains(enemyToAdd)){
			if(!PlayerGroup.DEFAULTS.contains(Bartender.MC.player.getDisplayNameString())){
				if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
					try {
						Desktop.getDesktop().browse(new URI("https:/"+"/www.youtube.com/watch?v=oHg5SJYRHA0"));
					} catch (IOException | URISyntaxException e) {
						e.printStackTrace();
					}
				}
			}
		}

		if(spreadMessage){
			EntityPlayerSP player = Minecraft.getMinecraft().player;
			if(player==null) return;
			IPCHandler.push(IPCHandler.ADD_ENEMY,enemyToAdd, player.getPosition().x, player.getPosition().y, player.getPosition().z);
		}
	}

	public static void prepareTpa() {
		if(BaritoneAPI.getProvider().getPrimaryBaritone().getFollowProcess().isActive()) {
			BaritoneAPI.getProvider().getPrimaryBaritone().getFollowProcess().cancel();
		}
		dontMove = true;
	}

	
	public static void receiveCoordMessage(String senderName, BlockPos blockPos) {
		
		if(senderName.equals(PlayerGroup.mainAccount)){
			lastKnownProtectedLocation=blockPos;
		}
	}

	static long lastAskedTpStamp = 0;
	public static void askTpFromProtected() {
		if(!PlayerGroup.isPlayerOnline(PlayerGroup.mainAccount)|| !enabled) return;
		if(System.currentTimeMillis()-lastAskedTpStamp>10000){
			Minecraft mc = Minecraft.getMinecraft();
			EntityPlayerSP player = mc.player;
			if(player==null) return;
			IPCHandler.push(IPCHandler.ASK_TPA_FROM_MAIN,player.getDisplayNameString(), player.getPosition().x, player.getPosition().y, player.getPosition().z);
		}
	}

	static long lastAskedCoordsStamp = 0;
	public static void askCoordsFromProtected() {
		if(!PlayerGroup.isPlayerOnline(PlayerGroup.mainAccount)) return;
		if(System.currentTimeMillis()-lastAskedCoordsStamp>5000){
			Minecraft mc = Minecraft.getMinecraft();
			EntityPlayerSP player = mc.player;
			if(player==null) return;
			IPCHandler.push(IPCHandler.ASK_COORDS,player.getDisplayNameString(), player.getPosition().x, player.getPosition().y, player.getPosition().z);
		}
	}

	public static void sendCoordsMessage() {
		Minecraft mc = Minecraft.getMinecraft();
		EntityPlayerSP player = mc.player;
		if(player==null) return;
		IPCHandler.push(IPCHandler.SEND_COORDS,player.getDisplayNameString(), player.getPosition().x, player.getPosition().y, player.getPosition().z);
	}

	public static String getStatusString() {
		EntityPlayerSP player = Minecraft.getMinecraft().player;
		if(player==null) return currentTask.toString();
		else if(PlayerGroup.mainAccount!=null&&player.getDisplayNameString().equals(PlayerGroup.mainAccount)) return "YOU SHOULD NOT ENABLE BODYGUARD ON YOUR MAIN ACC!";
		return currentTask.toString();
	}
	
	public static void applyPreferences(ClickGuiSetting[] contents) {
		for (ClickGuiSetting setting : contents) {
			switch (setting.title) {
			case "state":

				if(!enabled&&enabled != (setting.value == 1)&&Minecraft.getMinecraft().player!=null){
					if(FlightBot.enabled) {
						FlightBot.enabled=false; 
					}

					
					endAllTasks(false);
					lastKnownProtectedLocation = null;
				}
				enabled = setting.value == 1;
				break;
			case "look":
				freeLook = setting.value == 0;
				break;
			case "offset":
				useFollowOffset = setting.value == 1;
				break;
			case "agro":
				if(setting.value == 0) attackBehavior = AttackBehavior.ALWAYS_ATTACK;
				else if(setting.value == 1) attackBehavior = AttackBehavior.ATTACK_ON_COMMAND;
				break;
			case "offset draw":
				followPosDrawIntervalMillis = Long.parseLong(setting.values.get(setting.value).getAsString());
				break;
			case "P log":
				if(setting.value == 0) pDisconnectBehavior = ProtectedDisconnectBehavior.AVENGE;
				else if(setting.value == 1) pDisconnectBehavior = ProtectedDisconnectBehavior.DISCONNECT;
				else if(setting.value == 2) pDisconnectBehavior = ProtectedDisconnectBehavior.RUN;
				break;
			case "entity RD":
				entityRenderDistance = 16*(Integer.parseInt(setting.values.get(setting.value).getAsString()));
				break;
			default:
				break;
			}
		}	
	}

	public static void commitSuicide() {
		Minecraft.getMinecraft().player.sendChatMessage("/kill");
	}

	public static Entity getAuraTarget() {
		
		
		if(attackBehavior==AttackBehavior.ALWAYS_ATTACK){
			return Aura.getTarget(); 
		}
		
		
			Entity targetEntity;

			EntityPlayer nearestEnemy = null;
			for (String ep:currentEnemies) {
				EntityPlayer c = EntityRadar.getEntityPlayer(ep);
				if(c==null) continue;
				if(nearestEnemy==null)nearestEnemy=c;
				else if(c.getDistance(Minecraft.getMinecraft().player)<nearestEnemy.getDistance(Minecraft.getMinecraft().player)) nearestEnemy=c;
			}
			targetEntity= nearestEnemy;

			
			if(targetEntity==null||Minecraft.getMinecraft().player.getDistance(targetEntity)>Aura.reach){
				
				boolean[] player = new boolean[] {false, false, false};
				
				boolean[] mob = new boolean[] {true, false, false, true};
				ArrayList<Entity> checkInRange = EntityUtils.getTargetList(player, mob, true,  true, reach);
				targetEntity = EntityUtils.getPrioritizedTarget(checkInRange, entityPriority);
			}
			return targetEntity;

		
	}

}
