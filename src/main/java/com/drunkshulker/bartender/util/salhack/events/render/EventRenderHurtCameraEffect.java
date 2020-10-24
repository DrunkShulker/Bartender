package com.drunkshulker.bartender.util.salhack.events.render;

import com.drunkshulker.bartender.client.module.Velocity;
import com.drunkshulker.bartender.util.salhack.events.MinecraftEvent;
public class EventRenderHurtCameraEffect extends MinecraftEvent
{
    public float Ticks;
    
    public EventRenderHurtCameraEffect(float p_Ticks)
    {
        super();
        Ticks = p_Ticks;
        if(!isCancelled()&& Velocity.enabled) cancel();
    }
}
