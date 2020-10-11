package com.drunkshulker.bartender.mixins.client;

import com.drunkshulker.bartender.Bartender;
import com.drunkshulker.bartender.util.salhack.events.EventPlayerTravel;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(value = EntityPlayer.class, priority = Integer.MAX_VALUE)
public abstract class MixinEntityPlayer extends MixinEntityLivingBase
{
    public MixinEntityPlayer()
    {
        super();
    }

    @Inject(method = "travel", at = @At("HEAD"), cancellable = true)
    public void travel(float strafe, float vertical, float forward, CallbackInfo info)
    {
        EventPlayerTravel l_Event = new EventPlayerTravel(strafe, vertical, forward);
        Bartender.EVENT_BUS.post(l_Event);
        if (l_Event.isCancelled())
        {
            move(MoverType.SELF, motionX, motionY, motionZ);
            info.cancel();
        }
    }


}
