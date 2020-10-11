package com.drunkshulker.bartender.mixins.client;

import net.minecraft.entity.EntityLivingBase;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase extends MixinEntity
{
    public MixinEntityLivingBase()
    {
        super();
    }

    @Shadow
    public void jump()
    {
    }


}
