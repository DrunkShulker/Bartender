package com.drunkshulker.bartender.client.module;

import com.drunkshulker.bartender.Bartender;
import com.drunkshulker.bartender.client.gui.clickgui.ClickGuiSetting;
import com.drunkshulker.bartender.util.kami.BlockUtils;
import com.drunkshulker.bartender.util.kami.CoordUtil;
import com.drunkshulker.bartender.util.kami.EntityUtils;
import com.drunkshulker.bartender.util.kami.MathsUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Random;

public class Scaffold {

    public static boolean enabled = false;
    public static boolean controlPressed = false;
    private static boolean shouldSlow = false;
    private static double towerStart = 0.0;
    private static boolean holding = false;

    public static void applyPreferences(ClickGuiSetting[] contents) {
        for (ClickGuiSetting setting : contents) {
            switch (setting.title) {
                case "scaffold":
                    enabled = setting.value == 1;
                    break;
                default:
                    break;
            }
        }
    }

    int spreadRange(boolean diagonal){
        int r = Bartender.MC.player.rand.nextInt(5);
        if(r<2) r = (diagonal)?2:1;
        return (Bartender.MC.player.rand.nextBoolean())?r:r*-1;
    }

    @SubscribeEvent
    public void playerTick(InputUpdateEvent event) {
        if (!enabled) return;

        if (SafeTotemSwap.enabled() && SafeTotemSwap.taskInProgress) return;
        if (AutoEat.eating) return;
        if (Minecraft.getMinecraft().player == null) return;
        Minecraft mc = Minecraft.getMinecraft();

        boolean towering = mc.gameSettings.keyBindJump.isKeyDown();
        boolean rMode = !towering && controlPressed && !mc.player.isElytraFlying();
        Vec3d vec3d = EntityUtils.getInterpolatedPos(mc.player, 2f);
        if(!rMode && !towering && !mc.player.isElytraFlying())return;
        BlockPos blockPos;
        if (rMode) {
            blockPos = new BlockPos(vec3d.x + spreadRange(false), vec3d.y, vec3d.z + spreadRange(false));
            
        } else {
            blockPos = new BlockPos(vec3d).down();
            if (mc.player.isElytraFlying()) blockPos = blockPos.down(); 
        }

        BlockPos belowBlockPos = blockPos.down();
        BlockPos legitPos = new BlockPos(EntityUtils.getInterpolatedPos(mc.player, 2f));

        if (towering&&controlPressed) {
            if (mc.player.posY <= blockPos.y + 1.0f) {
                return;
            }
        }else if(rMode&&!mc.player.isElytraFlying()&&!Bodyguard.isBlockAir(mc.world, blockPos)) blockPos = blockPos.up();


        if (!mc.world.getBlockState(blockPos).getMaterial().isReplaceable()) {
            return;
        }

        int oldSlot = mc.player.inventory.currentItem;
        setSlotToBlocks(belowBlockPos);

        
        if (!BlockUtils.checkForNeighbours(blockPos)) return;

        
        if (enabled && isTNT(mc.player.getHeldItemMainhand())) {
            BlockUtils.placeBlockScaffold(blockPos);
        }


        
        if (!holding) mc.player.inventory.currentItem = oldSlot;

        if (towering&&controlPressed) {
            double motion = 0.42; 
            if (mc.player.onGround) {
                towerStart = mc.player.posY;
                mc.player.motionY = motion;
            }
            if (mc.player.posY > towerStart + motion) {
                mc.player.setPosition(mc.player.posX, Math.round(mc.player.posY), mc.player.posZ);
                mc.player.motionY = motion;
                towerStart = mc.player.posY;
            }
        } else {
            towerStart = 0.0;
        }
    }

    private static void setSlotToBlocks(BlockPos belowBlockPos) {
        if (isTNT(Minecraft.getMinecraft().player.getHeldItemMainhand())) {
            holding = true;
            return;
        }
        holding = false;

        
        int newSlot = -1;
        for (int i = 0; i <= 8; i++) {
            
            ItemStack stack = Minecraft.getMinecraft().player.inventory.getStackInSlot(i);

            if (isTNT(stack)) {
                newSlot = i;
                break;
            }
        }

        
        if (newSlot != -1) {
            Minecraft.getMinecraft().player.inventory.currentItem = newSlot;
        }
    }

    private static boolean isTNT(ItemStack stack) {
        if (stack == ItemStack.EMPTY || !(stack.getItem() instanceof ItemBlock)) return false;
        if (Item.getIdFromItem(stack.getItem()) == Item.getIdFromItem(Item.getItemFromBlock(Blocks.TNT))) return true;
        return false;
    }
}