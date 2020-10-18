package com.drunkshulker.bartender.mixins.client;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.renderer.ActiveRenderInfo;

@Mixin(ActiveRenderInfo.class)
public class MixinActiveRenderInfo
{
    /*@Inject(method = "updateRenderInfo", at = @At("RETURN"))
    private static void updateRenderInfo(EntityPlayer entityplayerIn, boolean p_74583_1_, CallbackInfo info)
    {
        RenderUtil.updateModelViewProjectionMatrix();
    }*/
}
