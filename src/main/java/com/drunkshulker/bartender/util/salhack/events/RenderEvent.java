package com.drunkshulker.bartender.util.salhack.events;

public class RenderEvent extends MinecraftEvent
{
    private float _partialTicks;

    public RenderEvent(float partialTicks)
    {
        _partialTicks = partialTicks;
    }

    public float getPartialTicks()
    {
        return _partialTicks;
    }
}
