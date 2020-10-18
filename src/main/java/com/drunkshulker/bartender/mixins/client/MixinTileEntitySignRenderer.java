package com.drunkshulker.bartender.mixins.client;

import com.drunkshulker.bartender.Bartender;
import com.drunkshulker.bartender.util.salhack.events.render.EventRenderSign;
import net.minecraft.client.renderer.tileentity.TileEntitySignRenderer;
import net.minecraft.tileentity.TileEntitySign;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(TileEntitySignRenderer.class)
public class MixinTileEntitySignRenderer
{
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(TileEntitySign te, double x, double y, double z, float partialTicks, int destroyStage, float alpha, CallbackInfo p_Info)
    {
        EventRenderSign l_Event = new EventRenderSign();
        Bartender.EVENT_BUS.post(l_Event);
        
        if (l_Event.isCancelled())
        {
           // destroyStage = 0;
            p_Info.cancel();
        }
    }
}
