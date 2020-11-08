package com.drunkshulker.bartender.util.kami;

import java.util.ArrayList;

import com.drunkshulker.bartender.client.module.SafeTotemSwap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class InventoryUtils {
    private static Minecraft mc = Minecraft.getMinecraft();

    public static void moveToSlot(int slotFrom, int slotTo) {
        moveToSlot(0, slotFrom, slotTo);
    }

    public static ArrayList<Integer> getSlots(int min, int max, int itemID) {
        ArrayList<Integer> slots = new ArrayList<>();
        for(int i = min; i <= max; i++) {
            if (Item.getIdFromItem(mc.player.inventory.getStackInSlot(i).getItem()) == itemID) {
                slots.add(i);
            }
        }
        
        if(slots.isEmpty()==false) return slots; 
        else return null;
    }


    public ArrayList<Integer> getSlotsHotbar(int itemId) {
        return getSlots(0, 8, itemId);
    }


    public static ArrayList<Integer> getSlotsNoHotbar(int itemId) {
        return getSlots(9, 35, itemId);
    }


    public static ArrayList<Integer> getSlotsFullInv(int min, int max, int itemId) {
    	ArrayList<Integer> slots = new ArrayList<>();
    	for(int i = min; i < max; i++) {
            if (Item.getIdFromItem(mc.player.inventoryContainer.getInventory().get(i).getItem()) == itemId) {
                slots.add(i);
            }
        }
    	if(slots.isEmpty()) return slots;
        else return null;
    }


    public ArrayList<Integer> getSlotsFullInvHotbar(int itemId){
        return getSlots(36, 44, itemId);
    }


    public static ArrayList<Integer> getSlotsFullInvNoHotbar(int itemId) {
        return getSlots(9, 35, itemId);
    }


    public static int countItem(int min, int max, int itemId){
        ArrayList<Integer> itemList = getSlots(min, max, itemId);
        int currentCount = 0;
        if (itemList != null) {
            for (int i : itemList) {
                currentCount += mc.player.inventory.getStackInSlot(i).getCount();
            }
        }
        return currentCount;
    }

    
    public static boolean inProgress = false;

    public static void swapSlot(int slot) {
        Minecraft.getMinecraft().player.inventory.currentItem = slot;
        
    }

    static public void removeHoldingItem() {
        if (mc.player.inventory.getItemStack().isEmpty()) return;

        int slot = -1;

        if(getSlotsFullInv(9, 45, 0)!=null){
            slot = getSlotsFullInv(1, 4, 0).get(0);
        } else slot = -999;

        inventoryClick(slot, ClickType.PICKUP);
    }

    public void swapSlotToItem(int itemID) {
        if (getSlotsHotbar(itemID) != null) {
            swapSlot(getSlotsHotbar(itemID).get(0));
        }
        
    }

    private static void inventoryClick(int slot, ClickType type) {
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, slot, 0, type, mc.player);
    }

    public void moveToHotbar(int itemID, int exceptionID, long delayMillis) {
    	ArrayList<Integer> gsfinh = getSlotsFullInvNoHotbar(itemID);
    	if(gsfinh==null) return;
    	
        int slot1 = gsfinh.get(0);
        int slot2 = 36;
        for(int i = 36; i<44;i++) {
        
            ItemStack currentItemStack = mc.player.inventoryContainer.getInventory().get(i);
            if (currentItemStack.isEmpty()) {
                slot2 = i;
                break;
            }
            if (Item.getIdFromItem(currentItemStack.getItem()) != exceptionID) {
                slot2 = i;
                break;
            }
        }
        moveToSlot(slot1, slot2, delayMillis);
    }


    public static void moveToSlot(int slotFrom, int slotTo, long delayMillis) {
        if (inProgress) return;
        
        Thread thread = new Thread(){
	        public void run(){
	        	inProgress = true;
	            GuiScreen prevScreen = mc.currentScreen;
	            mc.displayGuiScreen(new GuiInventory(mc.player));
	            try {
					Thread.sleep(delayMillis);
				} catch (InterruptedException e) {
					
					e.printStackTrace();
				}
	            inventoryClick(slotFrom, ClickType.PICKUP);
	            try {
					Thread.sleep(delayMillis);
				} catch (InterruptedException e) {
					
					e.printStackTrace();
				}
	            inventoryClick(slotTo, ClickType.PICKUP);
	            try {
					Thread.sleep(delayMillis);
				} catch (InterruptedException e) {
					
					e.printStackTrace();
				}
	            inventoryClick(slotFrom, ClickType.PICKUP);
	            mc.displayGuiScreen(prevScreen);
	            inProgress = false;
	        }
	    };
	    thread.start();
    }


    public void moveAllToSlot(int slotFrom, int slotTo, long delayMillis) {
        if (inProgress) return;
        Thread thread = new Thread(){
	        public void run(){
	        	inProgress = true;
	            GuiScreen prevScreen = mc.currentScreen;
	            mc.displayGuiScreen(new GuiInventory(mc.player));
	            try {
					Thread.sleep(delayMillis);
				} catch (InterruptedException e) {
					
					e.printStackTrace();
				}
	            inventoryClick(slotTo, ClickType.PICKUP_ALL);
	            try {
					Thread.sleep(delayMillis);
				} catch (InterruptedException e) {
					
					e.printStackTrace();
				}
	            inventoryClick(slotTo, ClickType.PICKUP);
	            mc.displayGuiScreen(prevScreen);
	            inProgress = false;
	        }
	    };
	    thread.start();
    }

    public static void quickMoveSlot(int slotFrom, long delayMillis) {
        if (inProgress) return;
        Thread thread = new Thread(){
	        public void run(){
	        	inProgress = true;
	            inventoryClick(slotFrom, ClickType.QUICK_MOVE);
	            try {
					Thread.sleep(delayMillis);
				} catch (InterruptedException e) {
					
					e.printStackTrace();
				}
	            inProgress = false;
	        }
	    };
	    thread.start();
    }
    
    public void quickTotem(int slotFrom, long delayMillis) {
        if (inProgress) return;
        Thread thread = new Thread(){
	        public void run(){
	        	inProgress = true;
	        	mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 36, 1, ClickType.THROW, mc.player);
	            try {
					Thread.sleep(delayMillis);
				} catch (InterruptedException e) {
					
					e.printStackTrace();
				}
	            inventoryClick(slotFrom, ClickType.QUICK_MOVE);
	            try {
					Thread.sleep(delayMillis);
				} catch (InterruptedException e) {
					
					e.printStackTrace();
				}
	            inProgress = false;
	        }
	    };
	    thread.start();
    }
    

    public void throwAllInSlot(int slot, long delayMillis) {
        if (inProgress) return;

        Thread thread = new Thread(){
	        public void run(){
	        	inProgress = true;
	            mc.playerController.windowClick(mc.player.inventoryContainer.windowId, slot, 1, ClickType.THROW, mc.player);
	            try {
					Thread.sleep(delayMillis);
				} catch (InterruptedException e) {
					
					e.printStackTrace();
				}
	            inProgress = false;
	        }
	    };
	    thread.start();
    }
    
}