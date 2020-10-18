package com.drunkshulker.bartender.client.module;


import com.drunkshulker.bartender.client.gui.clickgui.ClickGuiSetting;
import com.drunkshulker.bartender.util.kami.EntityUtils;
import com.drunkshulker.bartender.util.salhack.events.client.EventClientTick;
import com.drunkshulker.bartender.util.salhack.events.liquid.EventLiquidCollisionBB;
import com.drunkshulker.bartender.util.salhack.events.network.EventNetworkPacketEvent;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listenable;;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class Jesus implements Listenable {

    private Minecraft mc = Minecraft.getMinecraft();
    public static boolean enabled = false;
    public final Mode mode = Mode.NCP;
    public final float offset = 0f;

    public final float JumpHeight = 1.18f;

    private enum Mode {
        VANILLA, NCP, BOUNCE,
    }

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
    private Listener<EventLiquidCollisionBB> OnLiquidCollisionBB = new Listener<>(p_Event ->
    {
        if (!enabled || mc.player == null || mc.player.isElytraFlying()) return;
        if (mc.world != null && mc.player != null) {
            if (this.checkCollide() && !(mc.player.motionY >= 0.1f)
                    && p_Event.getBlockPos().getY() < mc.player.posY - this.offset) {
                if (mc.player.getRidingEntity() != null) {
                    p_Event.setBoundingBox(new AxisAlignedBB(0, 0, 0, 1, 1 - this.offset, 1));
                } else {
                    if (this.mode == Mode.BOUNCE) {
                        p_Event.setBoundingBox( new AxisAlignedBB(0.D, 0.D, 0.D, 1.D, 0.99D, 1.D));
                    } else {
                        p_Event.setBoundingBox( new AxisAlignedBB(0.D, 0.D, 0.D, 1.D, 0.99D, 1.D));
                    }
                }
                p_Event.cancel();
            }
        }
    });

    @EventHandler
    private Listener<EventNetworkPacketEvent> PacketEvent = new Listener<>(event ->
    {
        if (!enabled || mc.player == null || mc.player.isElytraFlying()) return;
        if (event.getPacket() instanceof CPacketPlayer) {
            if (EntityUtils.isAboveWater(mc.player, true)
                    && !EntityUtils.isInWater(mc.player)
                    && !isAboveBlock(mc.player, mc.player.getPosition())) {
                int ticks = mc.player.ticksExisted % 2;
                CPacketPlayer p = (CPacketPlayer) event.getPacket();
                if (ticks == 0) {
                    p.y += 0.02D;
                }
            }
        }
    });

    private boolean checkCollide() {
        if (mc.player.isSneaking()) {
            return false;
        }

        if (mc.player.getRidingEntity() != null) {
            if (mc.player.getRidingEntity().fallDistance >= 3.0f) {
                return false;
            }
        }

        if (mc.player.fallDistance >= 3.0f) {
            return false;
        }

        return true;
    }

    public boolean isInLiquid() {
        if (mc.player.fallDistance >= 3.0f) {
            return false;
        }

        if (mc.player != null) {
            boolean inLiquid = false;
            final AxisAlignedBB bb = mc.player.getRidingEntity() != null
                    ? mc.player.getRidingEntity().getEntityBoundingBox()
                    : mc.player.getEntityBoundingBox();
            int y = (int) bb.minY;
            for (int x = MathHelper.floor(bb.minX); x < MathHelper.floor(bb.maxX) + 1; x++) {
                for (int z = MathHelper.floor(bb.minZ); z < MathHelper.floor(bb.maxZ) + 1; z++) {
                    final Block block = mc.world.getBlockState(new BlockPos(x, y, z)).getBlock();
                    if (!(block instanceof BlockAir)) {
                        if (!(block instanceof BlockLiquid)) {
                            return false;
                        }
                        inLiquid = true;
                    }
                }
            }
            return inLiquid;
        }
        return false;
    }

    public boolean isOnLiquid(double offset) {
        if (mc.player.fallDistance >= 3.0f) {
            return false;
        }

        if (mc.player != null) {
            final AxisAlignedBB bb = mc.player.getRidingEntity() != null
                    ? mc.player.getRidingEntity().getEntityBoundingBox().contract(0.0d, 0.0d, 0.0d).offset(0.0d,
                    -offset, 0.0d)
                    : mc.player.getEntityBoundingBox().contract(0.0d, 0.0d, 0.0d).offset(0.0d, -offset, 0.0d);
            boolean onLiquid = false;
            int y = (int) bb.minY;
            for (int x = MathHelper.floor(bb.minX); x < MathHelper.floor(bb.maxX + 1.0D); x++) {
                for (int z = MathHelper.floor(bb.minZ); z < MathHelper.floor(bb.maxZ + 1.0D); z++) {
                    final Block block = mc.world.getBlockState(new BlockPos(x, y, z)).getBlock();
                    if (block != Blocks.AIR) {
                        if (!(block instanceof BlockLiquid)) {
                            return false;
                        }
                        onLiquid = true;
                    }
                }
            }
            return onLiquid;
        }

        return false;
    }



    private static boolean isAboveBlock(Entity entity, BlockPos pos) {
        return entity.posY >= pos.getY();
    }
}