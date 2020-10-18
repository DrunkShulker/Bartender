package com.drunkshulker.bartender.mixins.client;

import com.drunkshulker.bartender.Bartender;
import com.drunkshulker.bartender.util.salhack.events.player.EventPlayerApplyCollision;
import com.drunkshulker.bartender.util.salhack.events.player.EventPlayerPushedByWater;
import com.drunkshulker.bartender.util.salhack.events.player.EventPlayerTravel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import org.spongepowered.asm.lib.Type;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


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

    @Inject(method = "applyEntityCollision", at = @At("HEAD"), cancellable = true)
    public void applyEntityCollision(Entity p_Entity, CallbackInfo info)
    {
        EventPlayerApplyCollision l_Event = new EventPlayerApplyCollision(p_Entity);
        Bartender.EVENT_BUS.post(l_Event);
        if (l_Event.isCancelled())
            info.cancel();
    }

    @Inject(method = "isPushedByWater()Z", at = @At("HEAD"), cancellable = true)
    public void isPushedByWater(CallbackInfoReturnable<Boolean> ci)
    {
        EventPlayerPushedByWater l_Event = new EventPlayerPushedByWater();
        Bartender.EVENT_BUS.post(l_Event);
        if (l_Event.isCancelled())
            ci.setReturnValue(false);
    }
}
