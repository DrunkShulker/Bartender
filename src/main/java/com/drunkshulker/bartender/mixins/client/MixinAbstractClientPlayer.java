package com.drunkshulker.bartender.mixins.client;

import com.drunkshulker.bartender.Bartender;
import com.drunkshulker.bartender.util.salhack.events.player.EventPlayerGetLocationCape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.util.ResourceLocation;

@Mixin(AbstractClientPlayer.class)
public abstract class MixinAbstractClientPlayer extends MixinEntityPlayer
{
    public MixinAbstractClientPlayer()
    {
        super();
    }

    @Inject(method = "getLocationCape", at = @At(value = "RETURN"), cancellable = true)
    public void getCape(CallbackInfoReturnable<ResourceLocation> callbackInfo)
    {
        EventPlayerGetLocationCape l_Event = new EventPlayerGetLocationCape((AbstractClientPlayer)(Object)this);
        Bartender.EVENT_BUS.post(l_Event);

        if (l_Event.isCancelled())
        {
            // p_Callback.cancel();
            callbackInfo.setReturnValue(l_Event.GetResourceLocation());
        }
    }
}
