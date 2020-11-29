package com.drunkshulker.bartender.client.module;

import com.drunkshulker.bartender.Bartender;
import com.drunkshulker.bartender.client.gui.clickgui.ClickGuiSetting;
import com.drunkshulker.bartender.util.kami.InventoryUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;

public class AutoRefill {
    public static boolean enabled = false;

    int itemId = -1;
    ArrayList<Integer> slotsWithItem = new ArrayList<>();

    long lastFillStamp = System.currentTimeMillis();
    final int interval = 1100;

    public static void applyPreferences(ClickGuiSetting[] contents) {
        for (ClickGuiSetting setting : contents) {
            if (setting.title.equals("auto refill")) enabled = setting.value == 1;
        }
    }

    
    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if(!enabled) return;

        if(System.currentTimeMillis()-lastFillStamp<interval) return;
        lastFillStamp = System.currentTimeMillis();

        if(Bartender.MC.player==null
                ||Bartender.MC.player.isCreative()
                ||Bartender.MC.player.isSpectator()
                ||Bartender.MC.currentScreen!=null
                ||AutoEat.eating
                ||SafeTotemSwap.taskInProgress
                ||InventoryUtils.inProgress
                ||Dupe.inProgress()) return;

        int slotToFill = getSlotTpFill();
        if(slotToFill<=0||itemId==-1||slotsWithItem == null||slotsWithItem.isEmpty()) return;

        Bartender.MC.playerController.windowClick(Bartender.MC.player.inventoryContainer.windowId, slotsWithItem.get(0), 0, ClickType.QUICK_MOVE, Bartender.MC.player);
    }

    private int getSlotTpFill() {
        int newSlot = -1;
        itemId = -1;
        if(slotsWithItem!=null)slotsWithItem.clear();
        for (int i = 0; i <= 8; i++) {
            
            ItemStack stack = Minecraft.getMinecraft().player.inventory.getStackInSlot(i);
            if(stack.isEmpty||stack.getItem()==Items.AIR) continue;
            if(stack.getItem() == Items.TOTEM_OF_UNDYING) continue;
            if(!stack.isStackable()) continue;
            if(stack.getCount()>10) continue;

            itemId = Item.getIdFromItem(stack.getItem());
            slotsWithItem = Bartender.INVENTORY_UTILS.getSlotsFullInvNoHotbar(itemId);
            if(slotsWithItem==null||slotsWithItem.isEmpty()) continue;

            newSlot = i;
        }
        return newSlot;
    }
}
