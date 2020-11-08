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

    
}
