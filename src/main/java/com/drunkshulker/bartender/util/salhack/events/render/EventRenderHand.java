package com.drunkshulker.bartender.util.salhack.events.render;

import com.drunkshulker.bartender.util.salhack.events.MinecraftEvent;
public class EventRenderHand extends MinecraftEvent
{
    public float PartialTicks;
    public int Pass;

    public EventRenderHand(float partialTicks, int pass)
    {
        super();
        
        PartialTicks = partialTicks;
        Pass = pass;
    }

}
