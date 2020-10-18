package com.drunkshulker.bartender.client.module;

import com.drunkshulker.bartender.client.gui.clickgui.ClickGuiSetting;

import com.drunkshulker.bartender.util.salhack.events.network.EventNetworkPacketEvent;
import com.drunkshulker.bartender.util.salhack.events.player.EventPlayerApplyCollision;
import com.drunkshulker.bartender.util.salhack.events.player.EventPlayerPushOutOfBlocks;
import com.drunkshulker.bartender.util.salhack.events.player.EventPlayerPushedByWater;
import com.mojang.realmsclient.gui.ChatFormatting;

import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listenable;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SPacketEntityMetadata;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;

public final class Velocity implements Listenable
{
    public static boolean enabled = false;
    public final int horizontal_vel = 0;
    public final int vertical_vel = 0;
    public final boolean explosions = true;
    public final boolean bobbers = true;
    public final boolean NoPush = true;
    Minecraft mc = Minecraft.getMinecraft();

    @EventHandler
    private Listener<EventPlayerPushOutOfBlocks> PushOutOfBlocks = new Listener<>(p_Event ->
    {
        if(!enabled) return;
        if (!NoPush)
            return;

        p_Event.cancel();
    });

    @EventHandler
    private Listener<EventPlayerPushedByWater> PushByWater = new Listener<>(p_Event ->
    {
        if(!enabled) return;
        if (!NoPush)
            return;

        p_Event.cancel();
    });

    @EventHandler
    private Listener<EventPlayerApplyCollision> ApplyCollision = new Listener<>(p_Event ->
    {
        if(!enabled) return;
        if (!NoPush)
            return;

        p_Event.cancel();
    });

    @EventHandler
    private Listener<EventNetworkPacketEvent> PacketEvent = new Listener<>(p_Event ->
    {
        if(!enabled) return;
        if (mc.player == null)
            return;

        if (p_Event.getPacket() instanceof SPacketEntityStatus && this.bobbers)
        {
            final SPacketEntityStatus packet = (SPacketEntityStatus) p_Event.getPacket();
            if (packet.getOpCode() == 31)
            {
                final Entity entity = packet.getEntity(Minecraft.getMinecraft().world);
                if (entity != null && entity instanceof EntityFishHook)
                {
                    final EntityFishHook fishHook = (EntityFishHook) entity;
                    if (fishHook.caughtEntity == Minecraft.getMinecraft().player)
                    {
                        p_Event.cancel();
                    }
                }
            }
        }
        if (p_Event.getPacket() instanceof SPacketEntityVelocity)
        {
            final SPacketEntityVelocity packet = (SPacketEntityVelocity) p_Event.getPacket();
            if (packet.getEntityID() == mc.player.getEntityId())
            {
                if (this.horizontal_vel == 0 && this.vertical_vel == 0)
                {
                    p_Event.cancel();
                    return;
                }

                if (this.horizontal_vel != 100)
                {
                    packet.motionX = packet.motionX / 100 * this.horizontal_vel;
                    packet.motionZ = packet.motionZ / 100 * this.horizontal_vel;
                }

                if (this.vertical_vel != 100)
                {
                    packet.motionY = packet.motionY / 100 * this.vertical_vel;
                }
            }
        }
        if (p_Event.getPacket() instanceof SPacketExplosion && this.explosions)
        {
            final SPacketExplosion packet = (SPacketExplosion) p_Event.getPacket();

            if (this.horizontal_vel == 0 && this.vertical_vel == 0)
            {
                p_Event.cancel();
                return;
            }

            if (this.horizontal_vel != 100)
            {
                packet.motionX = packet.motionX / 100 * this.horizontal_vel;
                packet.motionZ = packet.motionZ / 100 * this.horizontal_vel;
            }

            if (this.vertical_vel != 100)
            {
                packet.motionY = packet.motionY / 100 * this.vertical_vel;
            }
        }
    });

    public static void applyPreferences(ClickGuiSetting[] contents) {
        for (ClickGuiSetting setting : contents) {
            if(setting.title.equals("velocity")) enabled = setting.value==1;
        }
    }
}