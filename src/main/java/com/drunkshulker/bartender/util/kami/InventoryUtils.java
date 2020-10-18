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
    private Minecraft mc = Minecraft.getMinecraft();

    /**
     * Returns slots contains item with given item id  in full player inventory
     *
     * @return Array contains slot index, null if no item found
     */
    public ArrayList<Integer> getSlots(int min, int max, int itemID) {
        ArrayList<Integer> slots = new ArrayList<>();
        for(int i = min; i <= max; i++) {
            if (Item.getIdFromItem(mc.player.inventory.getStackInSlot(i).getItem()) == itemID) {
                slots.add(i);
            }
        }
        
        if(slots.isEmpty()==false) return slots; 
        else return null;
    }

    /**
     * Returns slots contains item with given item id in player hotbar
     *
     * @return Array contains slot index, null if no item found
     */
    public ArrayList<Integer> getSlotsHotbar(int itemId) {
        return getSlots(0, 8, itemId);
    }

    /**
     * Returns slots contains with given item id in player inventory (without hotbar)
     *
     * @return Array contains slot index, null if no item found
     */
    public ArrayList<Integer> getSlotsNoHotbar(int itemId) {
        return getSlots(9, 35, itemId);
    }

    /**
     * Returns slots in full inventory contains item with given [itemId] in full player inventory
     * This is same as [getSlots] but it returns full inventory slot index
     *
     * @return Array contains full inventory slot index, null if no item found
     */
    public ArrayList<Integer> getSlotsFullInv(int min, int max, int itemId) {
    	ArrayList<Integer> slots = new ArrayList<>();
    	for(int i = min; i < max; i++) {
            if (Item.getIdFromItem(mc.player.inventoryContainer.getInventory().get(i).getItem()) == itemId) {
                slots.add(i);
            }
        }
    	if(slots.isEmpty()) return slots;
        else return null;
    }

    /**
     * Returns slots contains item with given [itemId] in player hotbar
     * This is same as [getSlots] but it returns full inventory slot index
     *
     * @return Array contains slot index, null if no item found
     */
    public ArrayList<Integer> getSlotsFullInvHotbar(int itemId){
        return getSlots(36, 44, itemId);
    }

    /**
     * Returns slots contains with given [itemId] in player inventory (without hotbar)
     * This is same as [getSlots] but it returns full inventory slot index
     *
     * @return Array contains slot index, null if no item found
     */
    public ArrayList<Integer> getSlotsFullInvNoHotbar(int itemId) {
        return getSlots(9, 35, itemId);
    }

    /**
     * Counts number of item in range of slots
     *
     * @return Number of item with given [itemId] from slot [min] to slot [max]
     */
    public int countItem(int min, int max, int itemId){
        ArrayList<Integer> itemList = getSlots(min, max, itemId);
        int currentCount = 0;
        if (itemList != null) {
            for (int i : itemList) {
                currentCount += mc.player.inventory.getStackInSlot(i).getCount();
            }
        }
        return currentCount;
    }

    /* Inventory management */
    public boolean inProgress = false;

    /**
     * Swap current held item to given [slot]
     */
    public static void swapSlot(int slot) {
        Minecraft.getMinecraft().player.inventory.currentItem = slot;
        //mc.playerController.syncCurrentPlayItem();
    }

    /**
     * Try to swap current held item to item with given [itemID]
     */
    public void swapSlotToItem(int itemID) {
        if (getSlotsHotbar(itemID) != null) {
            swapSlot(getSlotsHotbar(itemID).get(0));
        }
        //mc.playerController.syncCurrentPlayItem();
    }

    private void inventoryClick(int slot, ClickType type) {
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, slot, 0, type, mc.player);
    }

    /**
     * Try to move item with given [itemID] to empty hotbar slot or slot contains no exception [exceptionID]
     * If none of those found, then move it to slot 0
     */
    public void moveToHotbar(int itemID, int exceptionID, long delayMillis) {
    	ArrayList<Integer> gsfinh = getSlotsFullInvNoHotbar(itemID);
    	if(gsfinh==null) return;
    	
        int slot1 = gsfinh.get(0);
        int slot2 = 36;
        for(int i = 36; i<44;i++) {
        //for (i in 36..44) { /* Finds slot contains no exception item first */
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

    /**
     * Move the item in [slotFrom] to [slotTo], if [slotTo] contains an item, then move it to [slotFrom]
     */
    public void moveToSlot(int slotFrom, int slotTo, long delayMillis) {
        if (inProgress) return;
        
        Thread thread = new Thread(){
	        public void run(){
	        	inProgress = true;
	            GuiScreen prevScreen = mc.currentScreen;
	            mc.displayGuiScreen(new GuiInventory(mc.player));
	            try {
					Thread.sleep(delayMillis);
				} catch (InterruptedException e) {
					// Autogen block
					e.printStackTrace();
				}
	            inventoryClick(slotFrom, ClickType.PICKUP);
	            try {
					Thread.sleep(delayMillis);
				} catch (InterruptedException e) {
					// Autogen block
					e.printStackTrace();
				}
	            inventoryClick(slotTo, ClickType.PICKUP);
	            try {
					Thread.sleep(delayMillis);
				} catch (InterruptedException e) {
					// Autogen block
					e.printStackTrace();
				}
	            inventoryClick(slotFrom, ClickType.PICKUP);
	            mc.displayGuiScreen(prevScreen);
	            inProgress = false;
	        }
	    };
	    thread.start();
    }

    /**
     * Move all the item that equals to the item in [slotFrom] to [slotTo], if [slotTo] contains an item, then move it to [slotFrom]
     * Note: Not working
     */
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
					// Autogen block
					e.printStackTrace();
				}
	            inventoryClick(slotTo, ClickType.PICKUP_ALL);
	            try {
					Thread.sleep(delayMillis);
				} catch (InterruptedException e) {
					// Autogen block
					e.printStackTrace();
				}
	            inventoryClick(slotTo, ClickType.PICKUP);
	            mc.displayGuiScreen(prevScreen);
	            inProgress = false;
	        }
	    };
	    thread.start();
    }

    /**
     * Quick move the item in [slotFrom] (Shift + Click)
     */
    public void quickMoveSlot(int slotFrom, long delayMillis) {
        if (inProgress) return;
        Thread thread = new Thread(){
	        public void run(){
	        	inProgress = true;
	            inventoryClick(slotFrom, ClickType.QUICK_MOVE);
	            try {
					Thread.sleep(delayMillis);
				} catch (InterruptedException e) {
					// Autogen block
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
					// Autogen block
					e.printStackTrace();
				}
	            inventoryClick(slotFrom, ClickType.QUICK_MOVE);
	            try {
					Thread.sleep(delayMillis);
				} catch (InterruptedException e) {
					// Autogen block
					e.printStackTrace();
				}
	            inProgress = false;
	        }
	    };
	    thread.start();
    }
    

    /**
     * Throw all the item in [slot]
     */
    public void throwAllInSlot(int slot, long delayMillis) {
        if (inProgress) return;

        Thread thread = new Thread(){
	        public void run(){
	        	inProgress = true;
	            mc.playerController.windowClick(mc.player.inventoryContainer.windowId, slot, 1, ClickType.THROW, mc.player);
	            try {
					Thread.sleep(delayMillis);
				} catch (InterruptedException e) {
					// Autogen block
					e.printStackTrace();
				}
	            inProgress = false;
	        }
	    };
	    thread.start();
    }
    /* End of inventory management */
}