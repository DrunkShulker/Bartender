package com.drunkshulker.bartender.client.module;

import java.util.ArrayList;

import com.drunkshulker.bartender.Bartender;
import com.drunkshulker.bartender.client.gui.clickgui.ClickGuiSetting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.GameType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class SafeTotemSwap {
	
    static int swapOn = 8;
    static int stackSize = 64;
    static int nearDeathCount = 8;
	public static boolean enabled = true, pauseReminder = false;
	public static int totalCount;
	public static boolean runningLowOnStacks = false;
	public static boolean totemsReadyToSwitch = false;
	final static int ALLOWED_MISS_CALC_IN_STACK = 9;
	final public static int FIRST_HOTBAR_SLOT = 0;
	final static int OFFHAND_SLOT = 45;
	public static boolean taskInProgress = false;
	static int operationIntervalMillis = 1500;
	static long lastSwapStamp = operationIntervalMillis;
	public static NearDeathBehavior nearDeathBehavior;
	public static int totalUselessCount=0;

	final static int totID = Item.getIdFromItem(Items.TOTEM_OF_UNDYING);

	enum NearDeathBehavior{	
		NONE,
		DISCONNECT,
		SLASH_KILL
	}
	
	
	@SubscribeEvent
	public void onTick(TickEvent.ClientTickEvent event) {
		if(Minecraft.getMinecraft().player==null||Minecraft.getMinecraft().player.isDead) return;
		if(!enabled
				||Minecraft.getMinecraft().playerController.getCurrentGameType()==GameType.CREATIVE
				||Minecraft.getMinecraft().playerController.getCurrentGameType()==GameType.SPECTATOR
				||Minecraft.getMinecraft().isSingleplayer()) return;

		
		taskInProgress = Bartender.INVENTORY_UTILS.inProgress||System.currentTimeMillis()-lastSwapStamp<operationIntervalMillis;

		try {		
			EntityLivingBase playerLB  = (EntityLivingBase) Minecraft.getMinecraft().getRenderViewEntity();
			EntityPlayerSP playerSP = Minecraft.getMinecraft().player;
			if(playerLB==null) return;
			
			
			totalCount = playerSP.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.TOTEM_OF_UNDYING).mapToInt(ItemStack::getCount).sum();
			if(Item.getIdFromItem(playerLB.getHeldItemOffhand().getItem())==Item.getIdFromItem(Items.TOTEM_OF_UNDYING)) {totalCount += playerLB.getHeldItemOffhand().getCount();}	
			
			updateUselessTotCount();

			
			
			if(totalCount-totalUselessCount<=nearDeathCount&&totalCount>0) {
				
				if(Minecraft.getMinecraft().player.getHealth()==20) {
					
					Bartender.msg("Prevented safe totem NDB on full HP!");
				}else {
					
					Bartender.msg("ND behavior!");
					if(nearDeathBehavior==NearDeathBehavior.DISCONNECT) {
						BaseFinder.logOut("safe totem near death");
					}else if(nearDeathBehavior==NearDeathBehavior.SLASH_KILL) {
						Bodyguard.commitSuicide();
					}
				}
			}
			
			
			totemsReadyToSwitch = checkHotbarTots();
						
			if(playerLB.getHeldItemOffhand().isEmpty()
					||(playerLB.getHeldItemOffhand().getCount()<=swapOn&& !runningLowOnStacks)
					||playerLB.getHeldItemOffhand().getItem()!=Items.TOTEM_OF_UNDYING) {
				if(taskInProgress)return;
				lastSwapStamp = System.currentTimeMillis();
				
				if(!totemsReadyToSwitch) prepareSwap();
				else swap();
			}else if(!totemsReadyToSwitch){
				if(taskInProgress)return;
				lastSwapStamp = System.currentTimeMillis();			
				prepareSwap();
			}
		} catch (Exception e) {} 
	}

	private void updateUselessTotCount() {
		
		ArrayList<Integer> slotsWithTots = Bartender.INVENTORY_UTILS.getSlots(9, 45, totID);
		int uselessStackCout = 0;
		for (Integer slot : slotsWithTots) {
			if(slot==FIRST_HOTBAR_SLOT) continue;
			if(slot==OFFHAND_SLOT) continue;
			int stackCount = Bartender.INVENTORY_UTILS.countItem(slot, slot, totID);
			if(stackCount<=swapOn) {
				uselessStackCout +=stackCount;
			}
		}
		totalUselessCount = uselessStackCout;
	}

	private void prepareSwap() {
		
		ArrayList<Integer> slotsWithTots = Bartender.INVENTORY_UTILS.getSlots(9, 45, totID);
		int bringToHotbar = -1;
		for (Integer slot : slotsWithTots) {
			if(slot==FIRST_HOTBAR_SLOT) continue;
			if(slot==OFFHAND_SLOT) continue;
			
			if(Bartender.INVENTORY_UTILS.countItem(slot, slot, totID)>=stackSize-ALLOWED_MISS_CALC_IN_STACK) {
				bringToHotbar = slot;
				break;
			}
		}
		
		if(bringToHotbar==-1) {
			runningLowOnStacks = true;
			Bartender.msg("Running low on tots!");
			int slotWithMostTots = -1;
			int uselessStackCout = 0;
			for (Integer slot : slotsWithTots) {
				if(slot==FIRST_HOTBAR_SLOT) continue;
				if(slot==OFFHAND_SLOT) continue;
				int stackCount = Bartender.INVENTORY_UTILS.countItem(slot, slot, totID);
				if(stackCount<=swapOn) {
					uselessStackCout +=stackCount;
				}
				if(stackCount>slotWithMostTots) {
					slotWithMostTots = slot;
				}
			}
			bringToHotbar = slotWithMostTots;
			totalUselessCount = uselessStackCout;
		}
		else runningLowOnStacks = false;
		
		if(bringToHotbar==-1) {
			Bartender.msg("Bring to hotbar not found!");
			return;
		}
		
		Bartender.INVENTORY_UTILS.quickTotem(bringToHotbar, operationIntervalMillis/2);
	}

	public static void swap() {
		equipItem(FIRST_HOTBAR_SLOT);
		KeyBinding.onTick(Minecraft.getMinecraft().gameSettings.keyBindSwapHands.getKeyCode());
	}

	
	public static boolean checkHotbarTots() {
		if(Bartender.INVENTORY_UTILS.countItem(FIRST_HOTBAR_SLOT, FIRST_HOTBAR_SLOT, Item.getIdFromItem(Items.TOTEM_OF_UNDYING))>swapOn) {
			return true;
		}
		return false;
	}
	
	public static void equipItem(int slot) {
		Minecraft.getMinecraft().player.inventory.currentItem = slot;
		
	}

	public static void applyPreferences(ClickGuiSetting[] contents) {
		for (ClickGuiSetting setting : contents) {
			switch (setting.title) {
			case "state":
				enabled = setting.value == 0;
				break;
			case "stacksize":
				stackSize = Integer.parseInt(setting.values.get(setting.value).getAsString());
				break;
			case "reminder":
				pauseReminder = setting.value == 1;
				break;
			case "interval":
				operationIntervalMillis = Integer.parseInt(setting.values.get(setting.value).getAsString());
				break;
			case "swap at":
				swapOn = setting.value+1;
				break;
			case "NDB":
				if(setting.value==0) nearDeathBehavior = NearDeathBehavior.NONE;
				else if(setting.value==1) nearDeathBehavior = NearDeathBehavior.DISCONNECT;
				else if(setting.value==2) nearDeathBehavior = NearDeathBehavior.SLASH_KILL;
				break;
			case "NDB count":
				nearDeathCount = setting.value+1;
				break;
			default:
				break;
			}
		}	
	}
	
}
