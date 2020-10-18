package com.drunkshulker.bartender.client.module;

import com.drunkshulker.bartender.client.gui.clickgui.ClickGuiSetting;
import com.drunkshulker.bartender.client.social.PlayerGroup;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AutoFlight {

	public static boolean useBartenderFlight = false;

	enum FlyTask{
		HOVER,
		FLIGHT,
		LANDING,
		FIND_PROTECTED
	}
	
	public static boolean enabled = false;
	public static int followDistance = 5;
	public static FlyTask currentFlyTask = FlyTask.HOVER;
	public static HoldLanding holdLanding = HoldLanding.OFF;
	public static boolean hoverOrbitEnabled = false;
	private static Vec3d followedPosition;
	public static int takeOffDelay = 400, takeOffMaxTime = 6000;
	public static boolean hoverOrbitClockwise = false;

	public enum HoldLanding{
		OFF,
		RAGE,
		FREEZE
	}

	
	private static final Vec3d[] orbitOrder = new Vec3d[]{
			new Vec3d(0,0,3),
			new Vec3d(1.35,0,2.68),
			new Vec3d(2.12,0,2.12),
			new Vec3d(2.77,0,1.17),
			new Vec3d(3,0,0),
			new Vec3d(2.79,0,-1.11),
			new Vec3d(2.12,0,-2.12),
			new Vec3d(1.2,0,-2.7),
			new Vec3d(0,0,-3),
			new Vec3d(-1.2,0,-2.7),
			new Vec3d(-2.12,0,-2.12),
			new Vec3d(-2.81,0,1.09),
			new Vec3d(-3,0,0),
			new Vec3d(-2.76,0,1.17),
			new Vec3d(-2.12,0,2.12),
			new Vec3d(-1.35,0,2.68),
	};

	@SubscribeEvent
	public void playerTick(InputUpdateEvent event)
	{
		if(!enabled ||BaseFinder.enabled) {
			enabled = false;
			return;
		}
		Minecraft mc = Minecraft.getMinecraft();
		EntityPlayerSP player = mc.player;
		if(player==null) {
			return;
		}
		if(!player.isElytraFlying()) return;
		EntityPlayer followed = EntityRadar.getEntityPlayer(Bodyguard.currentFollowTarget); 

		if(currentFlyTask == FlyTask.FIND_PROTECTED&&Bodyguard.enabled){
			
			if(EntityRadar.getEntityPlayer(PlayerGroup.mainAccount)!=null){
				setTask(FlyTask.FLIGHT);
				return;
			}
			
			else if(BaseFinder.distance2D(player.getPosition(), Bodyguard.lastKnownProtectedLocation)<3){
				if(Bodyguard.protectedDimension!=player.dimension){
					
					setTask(FlyTask.HOVER);
					player.sendMessage(new TextComponentString("<Bartender> Main acc's last location reached but no main acc is seen."));
				}
				else if(!PlayerGroup.isPlayerOnline(PlayerGroup.mainAccount)){
					
					setTask(FlyTask.HOVER);
				}
				
				else {
					Bodyguard.askCoordsFromProtected();
				}
			}

			
			if(mc.player.dimension!=-1 
			&&mc.player.dimension==Bodyguard.protectedDimension){ 
				
				if(mc.player.getPosition().y<260){
					
					if(ElytraFlight.mode== ElytraFlight.Mode.BOOST)ElytraFlight.setMode(ElytraFlight.Mode.CONTROL);
					BaseFinder.lookAt(player.getPosition().getX(), player.getPosition().getY()+99, player.getPosition().getZ(), player, false);
					event.getMovementInput().moveForward = 1f;
				}else{
					
					if(ElytraFlight.mode== ElytraFlight.Mode.CONTROL)ElytraFlight.setMode(ElytraFlight.Mode.BOOST);
					BaseFinder.lookAt(Bodyguard.lastKnownProtectedLocation.getX(), 277, Bodyguard.lastKnownProtectedLocation.getZ(), player, true);
					event.getMovementInput().moveForward = 1f;
				}
			}
			
			else{
				
				Bodyguard.eatChorus(false);
				player.sendMessage(new TextComponentString("<Bartender> Main dimension was nether or did not match. Landing for tp."));
			}

			return;
		}

		
		if(currentFlyTask == FlyTask.HOVER&&followedPosition!=null) {
			
			if(hoverOrbitEnabled&&Bodyguard.currentFollowTarget.equals(PlayerGroup.mainAccount)&&followed!=null){
				
				double closestDistance = 99;
				int closestIndex = -1;
				for (int i = 0; i < orbitOrder.length; i++) {
					double dist = BaseFinder.distance2Ddouble(player.getPositionVector(),followedPosition.add(orbitOrder[i]));
					if(closestIndex==-1||dist<closestDistance) {
						closestDistance = dist;
						closestIndex=i;
					}
				}
				
				int targetIndex;
				if(hoverOrbitClockwise){
					if(closestIndex+1>=orbitOrder.length-1){targetIndex=0;}
					else {targetIndex=closestIndex+1;}
				}else{
					if(closestIndex<=0){targetIndex=orbitOrder.length-1;}
					else {targetIndex=closestIndex-1;}
				}

				Vec3d orbitPoint = followedPosition.add(orbitOrder[targetIndex]);

				BaseFinder.lookAt(orbitPoint.x, orbitPoint.y, orbitPoint.z, player, false);
				event.getMovementInput().moveForward = 1f;
			}
			
			else if(player.getPosition().getY()<followedPosition.y) {
				
				BaseFinder.lookAt(player.getPosition().getX(), player.getPosition().getY()+99, player.getPosition().getZ(), player, false);
				event.getMovementInput().moveForward = 1f;
			}
		}
		
		if(followed!=null) {
			followedPosition = followed.getPositionVector();
			
			EntityPlayer main = EntityRadar.getEntityPlayer(PlayerGroup.mainAccount);
			if(main!=null
					&&main.isElytraFlying()
					&&!followed.isElytraFlying()
					&&holdLanding!=HoldLanding.OFF
					&&!Bodyguard.currentFollowTarget.equals(PlayerGroup.mainAccount)){
				
				followedPosition=followedPosition.add(0,4,0);
				float distance = followed.getDistance(player);

				
				double yLevel = followedPosition.y;
				if(yLevel<6) {
					yLevel = 6;
				}
				
				BaseFinder.lookAt(followedPosition.x, yLevel, followedPosition.z+1, player, false);
				if(holdLanding==HoldLanding.RAGE) {
					event.getMovementInput().moveForward = 1f;
				}else if(holdLanding==HoldLanding.FREEZE) {
					if(distance>4) {
						event.getMovementInput().moveForward = 1f;
					}
				}
			}
			
			else if(!followed.isElytraFlying()) {
				if(currentFlyTask!=FlyTask.LANDING)setTask(FlyTask.LANDING);
				BaseFinder.lookAt(followedPosition.x, followedPosition.y, followedPosition.z, player, false);
				event.getMovementInput().moveForward = 1f;
				event.getMovementInput().sneak = true;
			}
			
			else {
				float distance = followed.getDistance(player);
				if(distance<followDistance&&currentFlyTask!=FlyTask.HOVER) setTask(FlyTask.HOVER);
				else if(distance>=followDistance&&currentFlyTask!=FlyTask.FLIGHT) setTask(FlyTask.FLIGHT);
				
				if(currentFlyTask==FlyTask.FLIGHT) {
					BaseFinder.lookAt(followedPosition.x, followedPosition.y, followedPosition.z+1, player, false);
					event.getMovementInput().moveForward = 1f;
				}
			}
		} else {
			
			if(!PlayerGroup.isPlayerOnline(PlayerGroup.mainAccount)) {
				
				setTask(FlyTask.HOVER);
				
			}else{
				
				
				if(Bodyguard.lastKnownProtectedLocation!=null){
					BaseFinder.lookAt(Bodyguard.lastKnownProtectedLocation.getX(), Bodyguard.lastKnownProtectedLocation.getY(), Bodyguard.lastKnownProtectedLocation.getZ()+1, player, false);
					event.getMovementInput().moveForward = 1f;
				}
			}
		}
	}
	
	static void setTask(FlyTask task) {
		if(task==FlyTask.FIND_PROTECTED) ElytraFlight.setMode(ElytraFlight.Mode.BOOST);
		else ElytraFlight.setMode(ElytraFlight.Mode.CONTROL);
		currentFlyTask = task;
	}

	public static void applyPreferences(ClickGuiSetting[] contents) {
		for (ClickGuiSetting setting : contents) {
			switch (setting.title) {
			case "hold landing":
				if(setting.value == 0) holdLanding = HoldLanding.OFF;
				if(setting.value == 1) holdLanding = HoldLanding.RAGE;
				if(setting.value == 2) holdLanding = HoldLanding.FREEZE;
				break;
				case "orbit":
					hoverOrbitEnabled = setting.value==1;
					break;
				case "auto takeoff":
					Bodyguard.autoTakeOff = setting.value==0;
					break;
				case "use elytra+":
					useBartenderFlight = setting.value==1;
					break;
				case "clockwise":
					hoverOrbitClockwise = setting.value==0;
					break;
				case "takeoff timer":
					takeOffDelay = Integer.parseInt(setting.values.get(setting.value).getAsString());
					break;
				case "takeoff limit":
					takeOffMaxTime = Integer.parseInt(setting.values.get(setting.value).getAsString());
					break;
			default:
				break;
			}
		}	
	}

}
