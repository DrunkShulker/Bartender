package com.drunkshulker.bartender.client.module;

import com.drunkshulker.bartender.client.gui.clickgui.ClickGuiSetting;
import com.drunkshulker.bartender.util.salhack.MathUtil;

import com.drunkshulker.bartender.util.salhack.events.blocks.EventSetOpaqueCube;
import com.drunkshulker.bartender.util.salhack.events.network.EventNetworkPacketEvent;
import com.drunkshulker.bartender.util.salhack.events.player.EventPlayerMove;
import com.drunkshulker.bartender.util.salhack.events.player.EventPlayerUpdate;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listenable;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.*;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

public class Freecam implements Listenable {
    public static boolean enabled = false;
    public final float speed = 1.0f;
    public final boolean CancelPackes = true;

    public static final Modes Mode = Modes.Normal;

    public static void applyPreferences(ClickGuiSetting[] contents) {
        for (ClickGuiSetting setting : contents) {
            if(setting.title.equals("freecam")) {
                if(enabled != (setting.value==1)){
                    setState(setting.value==1);
                }
            }
        }
    }

    public enum Modes {
        Normal,
        Camera,
    }

    private static Minecraft mc = Minecraft.getMinecraft();
    private static Entity riding;
    private static EntityOtherPlayerMP camera;
    private static Vec3d position;
    private static float yaw;
    private static float pitch;
    private static int lastThirdPersonView = 0;

    public static void onEnable() {

        if (mc.world == null)
            return;

        lastThirdPersonView = mc.gameSettings.thirdPersonView;
        mc.gameSettings.thirdPersonView = 0;
        mc.gameSettings.saveOptions();

        if (Mode == Modes.Normal) {
            riding = null;

            if (mc.player.getRidingEntity() != null) {
                riding = mc.player.getRidingEntity();
                mc.player.dismountRidingEntity();
            }

            camera = new EntityOtherPlayerMP(mc.world, mc.getSession().getProfile());
            camera.copyLocationAndAnglesFrom(mc.player);
            camera.prevRotationYaw = mc.player.rotationYaw;
            camera.rotationYawHead = mc.player.rotationYawHead;
            camera.inventory.copyInventory(mc.player.inventory);
            mc.world.addEntityToWorld(-69, camera);

            position = mc.player.getPositionVector();
            yaw = mc.player.rotationYaw;
            pitch = mc.player.rotationPitch;

            mc.player.noClip = true;
        } else 
        {
            camera = new EntityOtherPlayerMP(mc.world, mc.getSession().getProfile());
            camera.copyLocationAndAnglesFrom(mc.player);
            camera.prevRotationYaw = mc.player.rotationYaw;
            camera.rotationYawHead = mc.player.rotationYawHead;
            camera.inventory.copyInventory(mc.player.inventory);
            camera.noClip = true;
            mc.world.addEntityToWorld(-69, camera);
            mc.setRenderViewEntity(camera);
        }
    }

    public static void onDisable() {

        mc.gameSettings.thirdPersonView = lastThirdPersonView;
        mc.gameSettings.saveOptions();

        if (mc.world != null && Mode == Modes.Normal) {
            if (riding != null) {
                mc.player.startRiding(riding, true);
                riding = null;
            }
            if (camera != null) {
                mc.world.removeEntity(camera);
            }
            if (position != null) {
                mc.player.setPosition(position.x, position.y, position.z);
            }
            mc.player.rotationYaw = yaw;
            mc.player.rotationPitch = pitch;
            mc.player.noClip = false;
            mc.player.setVelocity(0, 0, 0);
        } else if (Mode == Modes.Camera) {
            if (camera != null) {
                mc.world.removeEntity(camera);
            }
            mc.setRenderViewEntity(mc.player);
        }
    }

    @EventHandler
    private Listener<EventPlayerMove> OnPlayerMove = new Listener<>(p_Event ->
    {
        if(!enabled) return;
        if (Mode == Modes.Normal)
            mc.player.noClip = true;
    });

    @EventHandler
    private Listener<EventSetOpaqueCube> OnEventSetOpaqueCube = new Listener<>(p_Event ->
    {
        if(!enabled) return;
        p_Event.cancel();
    });

    @EventHandler
    private Listener<EventPlayerUpdate> OnPlayerUpdate = new Listener<>(p_Event ->
    {
        if(!enabled) return;
        if (Mode == Modes.Normal) {
            mc.player.noClip = true;

            mc.player.setVelocity(0, 0, 0);

            final double[] dir = MathUtil.directionSpeed(this.speed);

            if (mc.player.movementInput.moveStrafe != 0 || mc.player.movementInput.moveForward != 0) {
                mc.player.motionX = dir[0];
                mc.player.motionZ = dir[1];
            } else {
                mc.player.motionX = 0;
                mc.player.motionZ = 0;
            }

            mc.player.setSprinting(false);

            if (mc.gameSettings.keyBindJump.isKeyDown()) {
                mc.player.motionY += this.speed;
            }

            if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                mc.player.motionY -= this.speed;
            }
        }
    });

    static void setState(boolean setEnabled) {
        enabled = setEnabled;
        if (enabled) onEnable();
        else onDisable();
    }

    @EventHandler
    private Listener<EntityJoinWorldEvent> OnWorldEvent = new Listener<>(p_Event ->
    {
        if(!enabled) return;
        if (p_Event.getEntity() == mc.player) {
            setState(false);
        }
    });

    @EventHandler
    private Listener<EventNetworkPacketEvent> PacketEvent = new Listener<>(p_Event ->
    {
        if(!enabled) return;
        if (Mode == Modes.Normal) {
            if (!CancelPackes)
                return;

            if ((p_Event.getPacket() instanceof CPacketUseEntity)
                    || (p_Event.getPacket() instanceof CPacketPlayerTryUseItem)
                    || (p_Event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock)
                    || (p_Event.getPacket() instanceof CPacketPlayer)
                    || (p_Event.getPacket() instanceof CPacketVehicleMove)
                    || (p_Event.getPacket() instanceof CPacketChatMessage)) {
                p_Event.cancel();
            }
        }
    });
}