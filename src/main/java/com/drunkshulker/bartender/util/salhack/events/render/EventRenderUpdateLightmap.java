package com.drunkshulker.bartender.util.salhack.events.render;

import com.drunkshulker.bartender.util.salhack.events.MinecraftEvent;
public class EventRenderUpdateLightmap extends MinecraftEvent
{
    public float PartialTicks;
    
    public EventRenderUpdateLightmap(float p_PartialTicks)
    {
        super();
        PartialTicks = p_PartialTicks;
    }
}
