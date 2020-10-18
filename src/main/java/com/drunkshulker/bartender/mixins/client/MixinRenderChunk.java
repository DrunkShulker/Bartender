package com.drunkshulker.bartender.mixins.client;

import com.drunkshulker.bartender.Bartender;

import com.drunkshulker.bartender.util.salhack.events.render.EventRenderChunk;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderChunk.class)
public class MixinRenderChunk {
    @Inject(method = "setPosition", at = @At("INVOKE"))
    private void setPosition(int x, int y, int z, CallbackInfo callbackInfo) {
        Bartender.EVENT_BUS.post(new EventRenderChunk((RenderChunk) (Object) this, new BlockPos(x, y, z)));
    }
}
