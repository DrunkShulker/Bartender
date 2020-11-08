package com.drunkshulker.bartender.client.module;

import com.drunkshulker.bartender.Bartender;
import com.drunkshulker.bartender.client.gui.clickgui.ClickGuiSetting;
import com.drunkshulker.bartender.util.salhack.BlockInteractionHelper;
import com.drunkshulker.bartender.util.salhack.PlayerUtil;
import com.drunkshulker.bartender.util.salhack.Timer;
import com.drunkshulker.bartender.util.salhack.events.MinecraftEvent;
import com.drunkshulker.bartender.util.salhack.events.network.EventNetworkPacketEvent;
import com.drunkshulker.bartender.util.salhack.events.player.EventPlayerMotionUpdate;
import com.drunkshulker.bartender.util.salhack.events.player.EventPlayerMove;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listenable;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.init.Blocks;


public class Scaffold implements Listenable {

    private static boolean enabled = false;
    public final Modes Mode =  Modes.Tower;
    public final boolean StopMotion = true;
    public final float Delay = 0;
    private Minecraft mc = Bartender.MC;

    public enum Modes {
        Tower,
        Normal,
    }

    private Timer _timer = new Timer();
    private Timer _towerPauseTimer = new Timer();
    private Timer _towerTimer = new Timer();

    @EventHandler
    private Listener<EventPlayerMotionUpdate> onMotionUpdate = new Listener<>(event ->
    {
        if(!enabled) return;
        if (event.isCancelled())
            return;

        if (event.getEra() != MinecraftEvent.Era.PRE)
            return;

        if (!_timer.passed(Delay * 1000))
            return;

        
        ItemStack stack = mc.player.getHeldItemMainhand();

        int prevSlot = -1;

        if (!verifyStack(stack)) {
            for (int i = 0; i < 9; ++i) {
                stack = mc.player.inventory.getStackInSlot(i);

                if (verifyStack(stack)) {
                    prevSlot = mc.player.inventory.currentItem;
                    mc.player.inventory.currentItem = i;
                    mc.playerController.updateController();
                    break;
                }
            }
        }

        if (!verifyStack(stack))
            return;

        _timer.reset();

        BlockPos toPlaceAt = null;

        BlockPos feetBlock = PlayerUtil.GetLocalPlayerPosFloored().down();

        boolean placeAtFeet = isValidPlaceBlockState(feetBlock);

        
        if (Mode == Modes.Tower && placeAtFeet && mc.player.movementInput.jump && _towerTimer.passed(250) && !mc.player.isElytraFlying()) {
            
            if (_towerPauseTimer.passed(1500)) {
                _towerPauseTimer.reset();
                mc.player.motionY = -0.28f;
            } else {
                final float towerMotion = 0.41999998688f;
                mc.player.setVelocity(0, towerMotion, 0);
            }
        }

        if (placeAtFeet)
            toPlaceAt = feetBlock;
        else 
        {
            BlockInteractionHelper.ValidResult result = BlockInteractionHelper.valid(feetBlock);

            
            if (result != BlockInteractionHelper.ValidResult.Ok && result != BlockInteractionHelper.ValidResult.AlreadyBlockThere) {
                BlockPos[] array = {feetBlock.north(), feetBlock.south(), feetBlock.east(), feetBlock.west()};

                BlockPos toSelect = null;
                double lastDistance = 420.0;

                for (BlockPos pos : array) {
                    if (!isValidPlaceBlockState(pos))
                        continue;

                    double dist = pos.getDistance((int) mc.player.posX, (int) mc.player.posY, (int) mc.player.posZ);
                    if (lastDistance > dist) {
                        lastDistance = dist;
                        toSelect = pos;
                    }
                }

                
                if (toSelect != null)
                    toPlaceAt = toSelect;
            }

        }

        if (toPlaceAt != null) {
            
            
            

            final Vec3d eyesPos = new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ);

            for (final EnumFacing side : EnumFacing.values()) {
                final BlockPos neighbor = toPlaceAt.offset(side);
                final EnumFacing side2 = side.getOpposite();

                if (mc.world.getBlockState(neighbor).getBlock().canCollideCheck(mc.world.getBlockState(neighbor), false)) {
                    final Vec3d hitVec = new Vec3d((Vec3i) neighbor).add(0.5, 0.5, 0.5).add(new Vec3d(side2.getDirectionVec()).scale(0.5));
                    if (eyesPos.distanceTo(hitVec) <= 5.0f) {
                        float[] rotations = BlockInteractionHelper.getFacingRotations(toPlaceAt.getX(), toPlaceAt.getY(), toPlaceAt.getZ(), side);

                        event.cancel();
                        PlayerUtil.PacketFacePitchAndYaw(rotations[1], rotations[0]);
                        break;
                    }
                }
            }

            if (BlockInteractionHelper.place(toPlaceAt, 5.0f, false, false, true) == BlockInteractionHelper.PlaceResult.Placed) {
                
            }
        } else
            _towerPauseTimer.reset();

        
        if (prevSlot != -1) {
            mc.player.inventory.currentItem = prevSlot;
            mc.playerController.updateController();
        }
    });

    @EventHandler
    private Listener<EventNetworkPacketEvent> PacketEvent = new Listener<>(event ->
    {
        if(!enabled) return;
        if (event.getPacket() instanceof SPacketPlayerPosLook) {
            
            _towerTimer.reset();
        }
    });

    @EventHandler
    private Listener<EventPlayerMove> OnPlayerMove = new Listener<>(p_Event ->
    {
        if(!enabled) return;
        if (!StopMotion)
            return;

        double x = p_Event.X;
        double y = p_Event.Y;
        double z = p_Event.Z;

        if (mc.player.onGround && !mc.player.noClip) {
            double increment;
            for (increment = 0.05D; x != 0.0D && isOffsetBBEmpty(x, -1.0f, 0.0D); ) {
                if (x < increment && x >= -increment) {
                    x = 0.0D;
                } else if (x > 0.0D) {
                    x -= increment;
                } else {
                    x += increment;
                }
            }
            for (; z != 0.0D && isOffsetBBEmpty(0.0D, -1.0f, z); ) {
                if (z < increment && z >= -increment) {
                    z = 0.0D;
                } else if (z > 0.0D) {
                    z -= increment;
                } else {
                    z += increment;
                }
            }
            for (; x != 0.0D && z != 0.0D && isOffsetBBEmpty(x, -1.0f, z); ) {
                if (x < increment && x >= -increment) {
                    x = 0.0D;
                } else if (x > 0.0D) {
                    x -= increment;
                } else {
                    x += increment;
                }
                if (z < increment && z >= -increment) {
                    z = 0.0D;
                } else if (z > 0.0D) {
                    z -= increment;
                } else {
                    z += increment;
                }
            }
        }

        p_Event.X = x;
        p_Event.Y = y;
        p_Event.Z = z;
        p_Event.cancel();
    });

    private boolean isOffsetBBEmpty(double x, double y, double z) {
        return mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(x, y, z)).isEmpty();
    }

    private boolean isValidPlaceBlockState(BlockPos pos) {
        BlockInteractionHelper.ValidResult result = BlockInteractionHelper.valid(pos);

        if (result == BlockInteractionHelper.ValidResult.AlreadyBlockThere)
            return mc.world.getBlockState(pos).getMaterial().isReplaceable();

        return result == BlockInteractionHelper.ValidResult.Ok;
    }

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

    private static boolean verifyStack(ItemStack stack) {
        if (stack == ItemStack.EMPTY || !(stack.getItem() instanceof ItemBlock)) return false;
        if (Item.getIdFromItem(stack.getItem()) == Item.getIdFromItem(Item.getItemFromBlock(Blocks.TNT))) return true;
        return false;
    }

}












