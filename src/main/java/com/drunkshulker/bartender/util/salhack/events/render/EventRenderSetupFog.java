package com.drunkshulker.bartender.util.salhack.events.render;

import com.drunkshulker.bartender.util.salhack.events.MinecraftEvent;
public class EventRenderSetupFog extends MinecraftEvent
{
    public int StartCoords;
    public float PartialTicks;

    public EventRenderSetupFog(int startCoords, float partialTicks)
    {
        StartCoords = startCoords;
        PartialTicks = partialTicks;
    }

}
