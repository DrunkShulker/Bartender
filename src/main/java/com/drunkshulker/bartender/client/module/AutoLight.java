package com.drunkshulker.bartender.client.module;

import com.drunkshulker.bartender.Bartender;
import com.drunkshulker.bartender.client.gui.clickgui.ClickGuiSetting;
import com.drunkshulker.bartender.util.kami.BlockUtils;
import com.drunkshulker.bartender.util.kami.EntityUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;

public class AutoLight {

    public static boolean enabled = false;

    public static void applyPreferences(ClickGuiSetting[] contents) {
        for (ClickGuiSetting setting : contents) {
            switch (setting.title) {
                case "auto light":
                    enabled = setting.value == 1;
                    break;

                default:
                    break;
            }
        }
    }

    private void lightItUp(BlockPos pos){
        Minecraft mc = Minecraft.getMinecraft();
        Vec3d eyesPos = new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ);
        for (EnumFacing side : EnumFacing.values()) {
            BlockPos neighbor = pos;
            EnumFacing side2 = side.getOpposite();

            
            if (!BlockUtils.canBeClicked(neighbor)) {
                continue;
            }
            Vec3d hitVec = new Vec3d(neighbor).add(0.5, 0.5, 0.5).add(new Vec3d(side2.getDirectionVec()).scale(0.5));

            
            if (eyesPos.squareDistanceTo(hitVec) > 18.0625) {
                continue;
            }

            
            BlockUtils.faceVectorPacketInstant(hitVec);
            BlockUtils.processRightClickBlock(neighbor, side2, hitVec);
            mc.player.swingArm(EnumHand.MAIN_HAND);
            mc.rightClickDelayTimer = 4;
            return;
        }
    }

    @SubscribeEvent
    public void onMouseInput(TickEvent.ClientTickEvent event) {
        if(Minecraft.getMinecraft().player==null) return;
        if(!enabled) return;
        if(SafeTotemSwap.enabled&&SafeTotemSwap.taskInProgress) return;
        if(Dupe.inProgress()) return;
        if(AutoEat.eating) return;
        Minecraft mc = Minecraft.getMinecraft();
        if(mc.objectMouseOver==null||mc.objectMouseOver.typeOfHit==null) return;

        if (mc.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK) {
            BlockPos blockpos = mc.objectMouseOver.getBlockPos();
            if(Item.getIdFromItem(Item.getItemFromBlock(BlockUtils.getBlock(blockpos))) == Item.getIdFromItem(Item.getItemFromBlock(Blocks.TNT))){
                if(Item.getIdFromItem(mc.player.getHeldItemMainhand().getItem()) == Item.getIdFromItem(Items.FLINT_AND_STEEL)){
                    lightItUp(blockpos);
                }
                else {
                   int beforeSwap = mc.player.inventory.currentItem;

                   ArrayList<Integer> slots = Bartender.INVENTORY_UTILS.getSlotsHotbar(Item.getIdFromItem(Items.FLINT_AND_STEEL));
                   if(slots==null||slots.isEmpty()) return;

                   int flintNSteelSlot = slots.get(0);
                   if(flintNSteelSlot==-1) return;

                   AutoWeapon.equip(flintNSteelSlot);
                   lightItUp(blockpos);
                   AutoWeapon.equip(beforeSwap);
                }
            }
        }

    }
}
