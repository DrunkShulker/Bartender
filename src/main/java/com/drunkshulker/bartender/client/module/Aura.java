package com.drunkshulker.bartender.client.module;

import java.util.ArrayList;
import java.util.Random;

import com.drunkshulker.bartender.client.gui.clickgui.ClickGuiSetting;
import com.drunkshulker.bartender.client.social.PlayerGroup;
import com.drunkshulker.bartender.util.kami.EntityUtils;
import com.drunkshulker.bartender.util.kami.EntityUtils.EntityPriority;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;


public class Aura {
	
	enum DelayType{
		NONE,
		TICKS,
		SECONDS
	}
	
	public static boolean enabled = false; 

	private static DelayType delayType = DelayType.TICKS;
	public static boolean attackPassiveMobs = false;
	public static EntityPriority entityPriority = EntityPriority.DISTANCE;
	public static int tickDelay = 500, rTickDelay = 0;
	public static long millisDelay = 1000, rMillisDelay = 0;
	public static float reach = 5.5f;
	public static int lastHitTicks = 0;
	public static long lastHitMillis = 0, rLastHitMillis = 0;
	private static boolean hitCrystals = false;
	static Random random = new Random();
	private static boolean forceOff = false;
	private static boolean useAutoWeapon = false;
	public static boolean autoWeaponBySharpness = false;

	@SubscribeEvent
	public void playerTick(TickEvent.PlayerTickEvent event){
		if(forceOff) return;
		if(SafeTotemSwap.taskInProgress) return;
		if(AutoEat.enabled&&AutoEat.eating) return;
		

		
		lastHitTicks++; if(lastHitTicks>=tickDelay) lastHitTicks=tickDelay;
		if(cancelDueDelay()) return;
		
		lastHitTicks = 0;
        lastHitMillis = System.currentTimeMillis();
        
		if(rMillisDelay>0)rLastHitMillis = random.nextInt((int)rMillisDelay);
        
        Entity target = null;
        
		
		if(Bodyguard.enabled||!Bodyguard.currentEnemies.isEmpty()){
			target = Bodyguard.getAuraTarget();
		}
		
		if(target==null&&hitCrystals){
			target = getCrystalToHit();
		}

		
		if(target==null&&enabled) {
			target = getTarget();
		}
		
		if(target == null) return;
		
		
		Minecraft mc = Minecraft.getMinecraft();
        if (mc.player == null || mc.player.isDead) return;

        
		if(Bodyguard.enabled&&EntityUtils.isPlayer(target)){
			Bodyguard.addEnemy(((EntityPlayer)target).getDisplayNameString(), true);
		}

        
    	if(!(target instanceof EntityEnderCrystal)&&useAutoWeapon){
    		AutoWeapon.equipBestWeapon();
		}
		
		mc.playerController.attackEntity(mc.player, target);
		mc.player.swingArm(EnumHand.MAIN_HAND);
		
	}

	private Entity getCrystalToHit() {
		ArrayList<Entity> crystals = EntityRadar.nearbyCrystals();
		Entity player = Minecraft.getMinecraft().player;
		if(player==null) return null;
		
		
		double closestDistance = 999;
		Entity closest = null;
		for (Entity e:crystals) {
			if(e==null) continue;
			if(closest==null||closestDistance>e.getDistance(player)){
				closestDistance = e.getDistance(player);
				closest = e;
			}
		}
		if(closest==null) return null;
		if(closest.getDistance(player)<=4){
			return closest;
		}

		return null;
	}

	public static Entity getTarget() {
	 
	
	
		boolean[] player = new boolean[] {true, false, true};
		boolean[] mob = new boolean[] {true, attackPassiveMobs, attackPassiveMobs, true};
		
		ArrayList<Entity> checkInRange = EntityUtils.getTargetList(player, mob, true,  true, reach);

		return EntityUtils.getPrioritizedTarget(checkInRange, entityPriority);
	}

	private boolean cancelDueDelay() {
		switch (delayType) {
		case TICKS:
			if(lastHitTicks<tickDelay) return true;
			break;
		case SECONDS:
			if(System.currentTimeMillis()-lastHitMillis<millisDelay+rLastHitMillis) return true;
			break;
		default:
			break;
		}
		return false;
	}

	public static void applyPreferences(ClickGuiSetting[] contents) {
		for (ClickGuiSetting setting : contents) {
			switch (setting.title) {
			case "state":

				if(setting.value==0){
					enabled = false;
					forceOff = false;
				}else if(setting.value==1){
					enabled = true;
					forceOff = false;
				}else { 
					enabled = false;
					forceOff = true;
				}
				break;
			case "hit crystals":
				hitCrystals = setting.value == 1;
				break;
			case "D type":
				if(setting.value == 0) delayType = DelayType.NONE;
				else if(setting.value == 1) delayType = DelayType.TICKS;
				else if(setting.value == 2) delayType = DelayType.SECONDS;
				break;
			case "D millis":
				millisDelay = Integer.parseInt(setting.values.get(setting.value).getAsString());
				break;
			case "RD millis":
				rMillisDelay = Integer.parseInt(setting.values.get(setting.value).getAsString());
				break;
			case "prio":
				if(setting.value==1) entityPriority = EntityPriority.HEALTH;
				else entityPriority = EntityPriority.DISTANCE;
				break;
			case "mobs":
				attackPassiveMobs = setting.value == 0;
				break;
			case "auto weapon":
				useAutoWeapon = setting.value != 0;
				autoWeaponBySharpness = setting.value == 2;
				break;
			case "D ticks":
				tickDelay = Integer.parseInt(setting.values.get(setting.value).getAsString());
				break;
			case "reach":
				reach = (float) Double.parseDouble(setting.values.get(setting.value).getAsString());
				break;
			default:
				break;
			}
		}	
	}

}
