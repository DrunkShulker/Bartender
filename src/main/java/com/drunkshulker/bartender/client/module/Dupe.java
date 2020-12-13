package com.drunkshulker.bartender.client.module;

import com.drunkshulker.bartender.Bartender;
import com.drunkshulker.bartender.client.gui.GuiHandler;
import com.drunkshulker.bartender.util.kami.InventoryUtils;
import org.lwjgl.input.Mouse;
import com.drunkshulker.bartender.client.gui.clickgui.ClickGuiSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.item.Item;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Dupe {

    static final int timeoutMillis = 5000;
    static public WaitPhase currentWaitPhase = WaitPhase.NONE;
    static long startTimeStamp = 0;
    static int countBefore = 0, idBefore = 0, slotBefore = 0;
    static final int clickIntervalMillis = 300;
    static long lastClickStamp = System.currentTimeMillis();

    public enum WaitPhase{
        NONE,
        DROP,
        WAIT_PICKUP
    }

    static boolean inProgress(){
        return currentWaitPhase!=WaitPhase.NONE;
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if(!inProgress() || Minecraft.getMinecraft().player==null) return;
        Minecraft mc = Minecraft.getMinecraft();

        
        if(System.currentTimeMillis()-startTimeStamp > timeoutMillis){
            abort("<Bartender> Dupe failed. Timed out.");
        }

        if(currentWaitPhase==WaitPhase.DROP){
            if(mc.player.inventory.getCurrentItem().isEmpty()){
                abort("<Bartender> Dupe failed. Empty slot.");
                return;
            }
            
            BaseFinder.lookAt(mc.player.getPosition().getX(), -999, mc.player.getPosition().getZ(), mc.player, false);
            if(System.currentTimeMillis()-startTimeStamp < 120){
                if(!mc.player.getRecipeBook().isGuiOpen())mc.player.getRecipeBook().setGuiOpen(true);
                return;
            }

            
            idBefore = Item.getIdFromItem(mc.player.inventory.getCurrentItem().getItem());
            countBefore = InventoryUtils.countItem(0,8, idBefore);
            slotBefore = mc.player.inventory.currentItem;
            
            Bartender.INVENTORY_UTILS.throwAllInSlot(slotBefore+36,500);
            
            mc.displayGuiScreen(new GuiInventory(mc.player));
            if(!mc.player.getRecipeBook().isGuiOpen()){
                abort("<Bartender> Recipe book was closed. Dupe failed.");
                return;
            }
            

            currentWaitPhase = WaitPhase.WAIT_PICKUP;
        } else if(currentWaitPhase==WaitPhase.WAIT_PICKUP){

            if(System.currentTimeMillis()-lastClickStamp<clickIntervalMillis) return;
            else lastClickStamp = System.currentTimeMillis();

            
            
            if(InventoryUtils.countItem(0,8,idBefore)>countBefore){
                abort("<Bartender> Dupe success.");
            }
        }
    }

    private static void abort(String msg) {
        Minecraft mc = Minecraft.getMinecraft();
        mc.player.sendMessage(new TextComponentString(msg));
        currentWaitPhase = WaitPhase.NONE;
        
        mc.displayGuiScreen(null);
    }

    private static void dupeCurrent() {
        if(Minecraft.getMinecraft().player==null) return;
        currentWaitPhase = WaitPhase.DROP;
        startTimeStamp = System.currentTimeMillis();
    }

    public static void applyPreferences(ClickGuiSetting[] contents) {
        for (ClickGuiSetting setting : contents) {
            switch (setting.title) {
                default:
                    break;
            }
        }
    }

    public static void clickAction(String title) {
        switch (title) {
            case "dupe current":
                dupeCurrent();
                break;
            default:
                break;
        }
    }
}
