package com.drunkshulker.bartender.mixins.client;

import com.drunkshulker.bartender.Bartender;
import com.drunkshulker.bartender.util.salhack.events.entity.EventHorseSaddled;
import com.drunkshulker.bartender.util.salhack.events.entity.EventSteerEntity;
import net.minecraft.entity.passive.AbstractHorse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractHorse.class)
public class MixinAbstractHorse
{
    @Inject(method = "canBeSteered", at = @At("HEAD"), cancellable = true)
    public void canBeSteered(CallbackInfoReturnable<Boolean> cir)
    {
        EventSteerEntity l_Event = new EventSteerEntity();
        Bartender.EVENT_BUS.post(l_Event);

        if (l_Event.isCancelled())
        {
            cir.cancel();
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "isHorseSaddled", at = @At("HEAD"), cancellable = true)
    public void isHorseSaddled(CallbackInfoReturnable<Boolean> cir)
    {
        EventHorseSaddled l_Event = new EventHorseSaddled();
        Bartender.EVENT_BUS.post(l_Event);

        if (l_Event.isCancelled())
        {
            cir.cancel();
            cir.setReturnValue(true);
        }
    }
}
