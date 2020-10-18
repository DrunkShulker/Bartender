package com.drunkshulker.bartender.client.module;

import com.drunkshulker.bartender.client.gui.overlaygui.OverlayGui;
import com.drunkshulker.bartender.util.salhack.MathUtil;
import com.drunkshulker.bartender.util.salhack.RenderUtil;
import com.drunkshulker.bartender.util.salhack.events.render.RenderEvent;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listenable;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;

public class Tracers implements Listenable {
    Minecraft mc = Minecraft.getMinecraft();

    @EventHandler
    private Listener<RenderEvent> OnRenderEvent = new Listener<>(p_Event ->
    {
        if (!OverlayGui.targetGuiActive) return;

        if (mc.getRenderManager() == null || mc.getRenderManager().options == null)
            return;

        for (Entity entity : mc.world.loadedEntityList) {
            if (shouldRenderTracer(entity)) {
                final Vec3d pos = MathUtil.interpolateEntity(entity, p_Event.getPartialTicks()).subtract(mc.getRenderManager().renderPosX, mc.getRenderManager().renderPosY, mc.getRenderManager().renderPosZ);

                if (pos != null) {
                    mc.entityRenderer.setupCameraTransform(p_Event.getPartialTicks(), 0);
                    final Vec3d forward = new Vec3d(0, 0, 1).rotatePitch(-(float) Math.toRadians(Minecraft.getMinecraft().player.rotationPitch)).rotateYaw(-(float) Math.toRadians(Minecraft.getMinecraft().player.rotationYaw));
                    RenderUtil.drawLine3D((float) forward.x, (float) forward.y + mc.player.getEyeHeight(), (float) forward.z, (float) pos.x, (float) pos.y, (float) pos.z, 1f, getColor(entity));
                    mc.entityRenderer.setupCameraTransform(p_Event.getPartialTicks(), 0);
                }
            }
        }
    });

    public boolean shouldRenderTracer(Entity e) {
        if (!OverlayGui.targetGuiActive) return false;

        if (e == Minecraft.getMinecraft().player) return false;

        if (e instanceof EntityPlayer){
            if(OverlayGui.availableTargets!=null&&!OverlayGui.availableTargets.isEmpty()){
                if(OverlayGui.availableTargets.contains(((EntityPlayer) e).getDisplayNameString())) return true;
            }
        }

        return false;
    }

    private int getColor(Entity e) {
        if (e instanceof EntityPlayer){
            if(OverlayGui.availableTargets!=null&&!OverlayGui.availableTargets.isEmpty()){
                if(OverlayGui.availableTargets.get(OverlayGui.currentSelectedTargetIndex).equals(((EntityPlayer) e).getDisplayNameString())){
                    return 0xFFFF0000;
                }
            }
        }

        return 0xFFFFFFFF;
    }
}