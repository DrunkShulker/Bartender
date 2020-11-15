package com.drunkshulker.bartender.client.module;

import com.drunkshulker.bartender.client.gui.clickgui.ClickGuiSetting;
import com.drunkshulker.bartender.client.gui.overlaygui.OverlayGui;
import com.drunkshulker.bartender.client.social.PlayerFriends;
import com.drunkshulker.bartender.client.social.PlayerGroup;
import com.drunkshulker.bartender.util.kami.EntityUtils;
import com.drunkshulker.bartender.util.salhack.MathUtil;
import com.drunkshulker.bartender.util.salhack.RenderUtil;
import com.drunkshulker.bartender.util.salhack.events.render.RenderEvent;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listenable;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;

public class Tracers implements Listenable {
    Minecraft mc = Minecraft.getMinecraft();

    enum TracerMobsMode {
        NONE,
        CREEPER,
        HOSTILE,
        PASSIVE,
        ALL
    }

    static TracerMobsMode tracerMobsMode = TracerMobsMode.NONE;
    static boolean players = true;

    public static void applyPreferences(ClickGuiSetting[] contents) {
        for (ClickGuiSetting setting : contents) {
            switch (setting.title) {
                case "players":
                    players = setting.value == 1;
                    break;
                case "mobs":
                    if (setting.value == 0) tracerMobsMode = TracerMobsMode.NONE;
                    else if (setting.value == 1) tracerMobsMode = TracerMobsMode.CREEPER;
                    else if (setting.value == 2) tracerMobsMode = TracerMobsMode.HOSTILE;
                    else if (setting.value == 3) tracerMobsMode = TracerMobsMode.PASSIVE;
                    else if (setting.value == 4) tracerMobsMode = TracerMobsMode.ALL;
                    break;
                default:
                    break;
            }
        }
    }

    @EventHandler
    private Listener<RenderEvent> OnRenderEvent = new Listener<>(p_Event ->
    {
        if (mc.getRenderManager() == null || mc.getRenderManager().options == null)
            return;

        if (OverlayGui.targetGuiActive) { 
            for (Entity entity : mc.world.loadedEntityList) {
                if (isHostileSelectionTarget(entity)) {
                    final Vec3d pos = MathUtil.interpolateEntity(entity, p_Event.getPartialTicks()).subtract(mc.getRenderManager().renderPosX, mc.getRenderManager().renderPosY, mc.getRenderManager().renderPosZ);

                    mc.entityRenderer.setupCameraTransform(p_Event.getPartialTicks(), 0);
                    final Vec3d forward = new Vec3d(0, 0, 1).rotatePitch(-(float) Math.toRadians(Minecraft.getMinecraft().player.rotationPitch)).rotateYaw(-(float) Math.toRadians(Minecraft.getMinecraft().player.rotationYaw));
                    RenderUtil.drawLine3D((float) forward.x, (float) forward.y + mc.player.getEyeHeight(), (float) forward.z, (float) pos.x, (float) pos.y, (float) pos.z, 1f, getColor(entity));
                    mc.entityRenderer.setupCameraTransform(p_Event.getPartialTicks(), 0);
                }
            }
        } else { 
            for (Entity entity : mc.world.loadedEntityList) {
                final Vec3d pos = MathUtil.interpolateEntity(entity, p_Event.getPartialTicks()).subtract(mc.getRenderManager().renderPosX, mc.getRenderManager().renderPosY, mc.getRenderManager().renderPosZ);

                if(!EntityUtils.isLiving(entity)) continue;

                if((players&&entity instanceof EntityPlayer)
                ||(tracerMobsMode == TracerMobsMode.ALL)
                ||(tracerMobsMode==TracerMobsMode.CREEPER&&entity instanceof EntityCreeper)
                ||(tracerMobsMode==TracerMobsMode.HOSTILE&&EntityUtils.isHostileMob(entity))
                ||(tracerMobsMode==TracerMobsMode.PASSIVE&&!EntityUtils.isHostileMob(entity))
                ){
                    mc.entityRenderer.setupCameraTransform(p_Event.getPartialTicks(), 0);
                    final Vec3d forward = new Vec3d(0, 0, 1).rotatePitch(-(float) Math.toRadians(Minecraft.getMinecraft().player.rotationPitch)).rotateYaw(-(float) Math.toRadians(Minecraft.getMinecraft().player.rotationYaw));
                    RenderUtil.drawLine3D((float) forward.x, (float) forward.y + mc.player.getEyeHeight(), (float) forward.z, (float) pos.x, (float) pos.y, (float) pos.z, 1f, getColor(entity));
                    mc.entityRenderer.setupCameraTransform(p_Event.getPartialTicks(), 0);
                    continue;
                }
            }
        }
    });

    public boolean isHostileSelectionTarget(Entity e) {
        if (!OverlayGui.targetGuiActive) return false;

        if (e == Minecraft.getMinecraft().player) return false;

        if (e instanceof EntityPlayer) {
            if (OverlayGui.availableTargets != null && !OverlayGui.availableTargets.isEmpty()) {
                if (OverlayGui.availableTargets.contains(((EntityPlayer) e).getDisplayNameString())) return true;
            }
        }

        return false;
    }

    private int getColor(Entity e) {
        if (OverlayGui.targetGuiActive){ 
            if (e instanceof EntityPlayer) {
                if (OverlayGui.availableTargets != null && !OverlayGui.availableTargets.isEmpty()) {
                    if (OverlayGui.availableTargets.get(OverlayGui.currentSelectedTargetIndex).equals(((EntityPlayer) e).getDisplayNameString())) {
                        return 0xFFFF0000;
                    }
                }
            }
            return 0x00000000;
        } else { 
            if (e instanceof EntityPlayer) {
                if(players){
                    String name = ((EntityPlayer) e).getDisplayNameString();
                    if(name.equals(mc.player.getDisplayNameString())) return 0x00000000;
                    if(PlayerGroup.members.contains(name)) return 0xFF00FF21;
                    if(PlayerFriends.friends.contains(name)) return 0xFF00FF21;
                    if(PlayerFriends.impactFriends.contains(name)) return 0xFF00FF21;
                    if(PlayerGroup.DEFAULTS.contains(name)) return 0xFFFF00DC;
                }
                else return 0x00000000;
            }
        }

        return 0xFFFFFFFF; 
    }
}