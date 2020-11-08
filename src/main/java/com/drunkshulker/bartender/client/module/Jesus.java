package com.drunkshulker.bartender.client.module;


import com.drunkshulker.bartender.Bartender;
import com.drunkshulker.bartender.client.gui.clickgui.ClickGuiSetting;
import com.drunkshulker.bartender.util.kami.AddCollisionBoxToListEvent;
import com.drunkshulker.bartender.util.kami.EntityUtils;
import com.drunkshulker.bartender.util.salhack.events.client.EventClientTick;
import com.drunkshulker.bartender.util.salhack.events.liquid.EventLiquidCollisionBB;
import com.drunkshulker.bartender.util.salhack.events.network.EventNetworkPacketEvent;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listenable;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class Jesus implements Listenable {

    private Minecraft mc = Bartender.MC;
    public final float offset = 0f;
    public static boolean enabled = false;

    public static void applyPreferences(ClickGuiSetting[] contents) {
        for (ClickGuiSetting setting : contents) {
            if (setting.title.equals("jesus")) enabled = setting.value == 1;
        }
    }

    @EventHandler
    private Listener<EventClientTick> onClientTick = new Listener<>(it ->
    {
        if (!enabled || mc.player == null || mc.player.isElytraFlying()) return;
            if (EntityUtils.isInWater(mc.player) && !mc.player.isSneaking()) {
                mc.player.motionY = 0.1;
                if (mc.player.getRidingEntity() != null && !(mc.player.getRidingEntity() instanceof EntityBoat)) {
                    mc.player.getRidingEntity().motionY = 0.3;
                }
        }

    });

    @EventHandler
    private Listener<AddCollisionBoxToListEvent> packetEvefnt = new Listener<>(it -> {
        if(!enabled) return;

            if (!(it.block instanceof BlockLiquid) || !(it.entity instanceof EntityBoat) || mc.player == null || mc.player.isSneaking() || mc.player.fallDistance > 3) return;
            if ((EntityUtils.isDrivenByPlayer(it.entity)
                    || it.entity == mc.player)
                    && !EntityUtils.isInWater(mc.player)
                    && (EntityUtils.isAboveWater(mc.player, false)
                    || EntityUtils.isAboveWater(mc.player.getRidingEntity(), false))
                    && isAboveBlock(mc.player, it.pos)) {
                AxisAlignedBB axisAlignedBB = WATER_WALK_AA().offset(it.pos);
                if (it.entityBox.intersects(axisAlignedBB)) it.collidingBoxes.add(axisAlignedBB);
                it.cancel();
            }
        });

        @EventHandler
        private Listener<EventNetworkPacketEvent> packetEvent = new Listener<>(event -> {
            if(!enabled) return;
            if (event.getPacket() instanceof CPacketPlayer
                    && EntityUtils.isAboveWater(mc.player, true)
                    && !EntityUtils.isInWater(mc.player)
                    && !isAboveLand(mc.player)) {
                int ticks = mc.player.ticksExisted % 2;
                if (ticks == 0) ((CPacketPlayer) event.getPacket()).y += 0.02;
            }

    });

    private AxisAlignedBB WATER_WALK_AA (){return new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.99, 1.0);}

    private boolean isAboveLand(Entity entity) {
        double y = entity.posY - 0.01;
        for (int x = MathHelper.floor(entity.posX); x < MathHelper.ceil(entity.posX); x++) {
            for (int z = MathHelper.floor(entity.posZ); z < MathHelper.ceil(entity.posZ); z++) {
                BlockPos pos = new BlockPos(x, MathHelper.floor(y), z);
                if (mc.world.getBlockState(pos).isFullBlock()) return true;
            }
        }
        return false;
    }

    private boolean isAboveBlock(Entity entity, BlockPos pos) {
        return entity.posY >= pos.getY();
    }
}