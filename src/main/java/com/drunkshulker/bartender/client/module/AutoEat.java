package com.drunkshulker.bartender.client.module;

import com.drunkshulker.bartender.Bartender;
import com.drunkshulker.bartender.client.gui.clickgui.ClickGuiSetting;

import baritone.api.BaritoneAPI;
import com.drunkshulker.bartender.util.baritone.PauseProcess;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.FoodStats;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;

public class AutoEat {
	
    static float foodLevel = 1;
    static float healthLevel = 1;

    private int lastSlot = -1;
    public static boolean eating = false, enabled = false, fireResistance=true;

    private static int chorusCountBeforeEat = 0, chorusSlot = 0; 
    private static boolean eatChorus = false;

    
    public static void chorus() {
        if(eatChorus) return;
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        ArrayList<Integer> slots = Bartender.INVENTORY_UTILS.getSlotsHotbar(Item.getIdFromItem(Items.CHORUS_FRUIT));
        if(slots==null||slots.isEmpty()){
            Bartender.msg("Cannot eat chorus because you don't have any in your hotbar!");
            eatChorus=false;
            return;
        }
        
        chorusSlot = slots.get(0);
        chorusCountBeforeEat = player.inventory.getStackInSlot(chorusSlot).getCount();
        if(chorusCountBeforeEat<=0){
            Bartender.msg("Unexpected error while trying to eat chorus.");
            eatChorus=false;
            return;
        }
        eatChorus = true;
    }

    boolean isValid(ItemStack stack, int food){
        return passItemCheck(stack) && stack.getItem() instanceof ItemFood && foodLevel - food >= ((ItemFood)stack.getItem()).getHealAmount(stack) ||
                passItemCheck(stack) && stack.getItem() instanceof ItemFood && healthLevel - (Minecraft.getMinecraft().player.getHealth() + Minecraft.getMinecraft().player.getAbsorptionAmount()) > 0f;
    }

    boolean passItemCheck(ItemStack stack) {
        Item item = stack.getItem();
        if (item == Items.ROTTEN_FLESH
                || item == Items.SPIDER_EYE
                || item == Items.POISONOUS_POTATO
                || (item == Items.FISH && (stack.getMetadata() == 3 || stack.getMetadata() == 2)) 
                || item == Items.CHORUS_FRUIT) {
            return false;
        }
        return true;
    }

    @SubscribeEvent
	public void playerTick(TickEvent.PlayerTickEvent event){
    	if(!enabled) return;
    	if(SafeTotemSwap.enabled()&&SafeTotemSwap.taskInProgress) return;
    	if(Minecraft.getMinecraft().player==null) return;
    	Minecraft mc = Minecraft.getMinecraft();

    	
        if(eating) BaritoneAPI.getProvider().getPrimaryBaritone().getPathingControlManager().registerProcess(new PauseProcess());

    	
    	boolean needFireResistance = fireResistance&&mc.player.isBurning()&&!mc.player.isPotionActive(MobEffects.FIRE_RESISTANCE);

        if (eating && !mc.player.isHandActive()) {
            if (lastSlot != -1) {
                mc.player.inventory.currentItem = lastSlot;
                lastSlot = -1;
            }
            eating = false;
            

            BaritoneAPI.getSettings().allowInventory.value = false;

            KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
            return;
        }

        if (eating) return;

        FoodStats stats = mc.player.getFoodStats();

        if (!SafeTotemSwap.enabled() && isValid(mc.player.getHeldItemOffhand(), stats.getFoodLevel())) {
            mc.player.setActiveHand(EnumHand.OFF_HAND);

            

            eating = true;
            BaritoneAPI.getSettings().allowInventory.value = true;

            KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);
            mc.playerController.processRightClick(mc.player, mc.world, EnumHand.OFF_HAND);
        } else {
            
            if(eatChorus){
                if(Minecraft.getMinecraft().player.inventory.getStackInSlot(chorusSlot).isEmpty
                        ||Minecraft.getMinecraft().player.inventory.getStackInSlot(chorusSlot).getItem()!=Items.CHORUS_FRUIT
                        ||chorusCountBeforeEat>Minecraft.getMinecraft().player.inventory.getStackInSlot(chorusSlot).getCount()){
                    eatChorus=false;
                    return;
                }

                lastSlot = mc.player.inventory.currentItem;
                mc.player.inventory.currentItem = chorusSlot;

                 

                eating = true;
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);
                mc.playerController.processRightClick(mc.player, mc.world, EnumHand.MAIN_HAND);
                return;
            }
            else if(needFireResistance){
                for (int i = 0; i <= 8; i++) {
                    if (isGapple(mc.player.inventory.getStackInSlot(i))) {
                        lastSlot = mc.player.inventory.currentItem;
                        mc.player.inventory.currentItem = i;

                         

                        eating = true;
                        KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);
                        mc.playerController.processRightClick(mc.player, mc.world, EnumHand.MAIN_HAND);
                        return;
                    }
                }
            }
            else {
                for (int i = 0; i <= 8; i++) {
                     if (isValid(mc.player.inventory.getStackInSlot(i), stats.getFoodLevel())) {
                         lastSlot = mc.player.inventory.currentItem;
                         mc.player.inventory.currentItem = i;

                         

                         eating = true;
                         KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);
                         mc.playerController.processRightClick(mc.player, mc.world, EnumHand.MAIN_HAND);
                         return;
                     }
                }
            }

        }
    }

    private boolean isGapple(ItemStack stackInSlot) {
        Item item = stackInSlot.getItem();
        return (item == Items.GOLDEN_APPLE)&&item.hasEffect(stackInSlot);
    }

    

	public static void applyPreferences(ClickGuiSetting[] contents) {
		for (ClickGuiSetting setting : contents) {
			switch (setting.title) {
			case "state":
				enabled = setting.value == 1;
				break;
			case "fire resistance":
                fireResistance = setting.value == 1;
                break;
			case "health":
				healthLevel = Integer.parseInt(setting.values.get(setting.value).getAsString());
				break;
			case "hunger":
				foodLevel = Integer.parseInt(setting.values.get(setting.value).getAsString());
				break;
			default:
				break;
			}
		}	
	}
}