package com.drunkshulker.bartender.mixins.client;

import com.drunkshulker.bartender.Bartender;
import com.drunkshulker.bartender.client.module.AntiOverlay;
import com.drunkshulker.bartender.util.salhack.events.particles.EventParticleEmitParticleAtEntity;
import net.minecraft.util.EnumParticleTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.Entity;

@Mixin(ParticleManager.class)
public class MixinParticleManager
{

    /*@Inject(method = "emitParticleAtEntity", at = @At("HEAD"), cancellable = true)
    public void emitParticleAtEntity(Entity p_Entity, EnumParticleTypes p_Type, int p_Amount, CallbackInfo p_Info)
    {
        EventParticleEmitParticleAtEntity l_Event = new EventParticleEmitParticleAtEntity(p_Entity, p_Type, p_Amount);
        
        Bartender.EVENT_BUS.post(l_Event);
        if (l_Event.isCancelled())l_Event.cancel();

    }*/
}
