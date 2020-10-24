package com.drunkshulker.bartender.mixins.client;


import com.drunkshulker.bartender.Bartender;
import com.drunkshulker.bartender.client.module.AntiOverlay;
import com.drunkshulker.bartender.util.salhack.RenderUtil;
import com.drunkshulker.bartender.util.salhack.events.render.*;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import com.google.common.base.Predicate;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import scala.xml.PrettyPrinter;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = EntityRenderer.class, priority = Integer.MAX_VALUE)
public class MixinEntityRenderer {

    @Inject(method = "displayItemActivation", at = @At(value = "HEAD"), cancellable = true)
    public void displayItemActivation(ItemStack stack, CallbackInfo callbackInfo) {
        if (AntiOverlay.enabled && AntiOverlay.totems) {
            callbackInfo.cancel();
        }
    }


    @Inject(method = "setupFog", at = @At("HEAD"), cancellable = true)
    public void setupFog(int startCoords, float partialTicks, CallbackInfo p_Info)
    {
        EventRenderSetupFog l_Event = new EventRenderSetupFog(startCoords, partialTicks);
        Bartender.EVENT_BUS.post(l_Event);
        if (l_Event.isCancelled())
            p_Info.cancel();
    }

    @Redirect(method = "getMouseOver", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/WorldClient;getEntitiesInAABBexcluding(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/AxisAlignedBB;Lcom/google/common/base/Predicate;)Ljava/util/List;"))
    public List<Entity> getEntitiesInAABBexcluding(WorldClient worldClient, Entity entityIn, AxisAlignedBB boundingBox, Predicate predicate)
    {
        EventRenderGetEntitiesINAABBexcluding l_Event = new EventRenderGetEntitiesINAABBexcluding(worldClient, entityIn, boundingBox, predicate);
        Bartender.EVENT_BUS.post(l_Event);
        if (l_Event.isCancelled())
            return new ArrayList<>();
        else
            return worldClient.getEntitiesInAABBexcluding(entityIn, boundingBox, predicate);
    }

    @Inject(method = "renderHand", at = @At("HEAD"), cancellable = true)
    private void renderHand(float partialTicks, int pass, CallbackInfo p_Info)
    {
        EventRenderHand l_Event = new EventRenderHand(partialTicks, pass);
        Bartender.EVENT_BUS.post(l_Event);
        if (l_Event.isCancelled())
            p_Info.cancel();
    }

    @Inject(method = "hurtCameraEffect", at = @At("HEAD"), cancellable = true)
    public void hurtCameraEffect(float ticks, CallbackInfo info)
    {
        EventRenderHurtCameraEffect l_Event = new EventRenderHurtCameraEffect(ticks);

        Bartender.EVENT_BUS.post(l_Event);

        if (l_Event.isCancelled())
            info.cancel();
    }

    @Inject(method = "updateLightmap", at = @At("HEAD"), cancellable = true)
    private void updateLightmap(float partialTicks, CallbackInfo p_Info)
    {
        EventRenderUpdateLightmap l_Event = new EventRenderUpdateLightmap(partialTicks);

        Bartender.EVENT_BUS.post(l_Event);

        if (l_Event.isCancelled())
            p_Info.cancel();
    }

    @Inject(method = "renderWorldPass", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/EntityRenderer;renderHand:Z", shift = At.Shift.AFTER))
    private void renderWorldPassPost(int pass, float partialTicks, long finishTimeNano, CallbackInfo callbackInfo)
    {
        RenderUtil.updateModelViewProjectionMatrix();
    }

    @Redirect(method = "orientCamera", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/WorldClient;rayTraceBlocks(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/RayTraceResult;"), expect = 0)
    private RayTraceResult rayTraceBlocks(WorldClient worldClient, Vec3d start, Vec3d end)
    {
        EventRenderOrientCamera event = new EventRenderOrientCamera();
        Bartender.EVENT_BUS.post(event);
        if (event.isCancelled())
            return null;
        else
            return worldClient.rayTraceBlocks(start, end);
    }
}
