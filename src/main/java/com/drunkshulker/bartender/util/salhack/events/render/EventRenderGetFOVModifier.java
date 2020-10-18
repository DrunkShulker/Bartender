package com.drunkshulker.bartender.util.salhack.events.render;

import com.drunkshulker.bartender.util.salhack.events.MinecraftEvent;
public class EventRenderGetFOVModifier extends MinecraftEvent
{
    public float PartialTicks;
    public boolean UseFOVSetting;
    private float FOV;

    public EventRenderGetFOVModifier(float p_PartialTicks, boolean p_UseFOVSetting)
    {
        super();
        PartialTicks = p_PartialTicks;
        UseFOVSetting = p_UseFOVSetting;
    }
    
    public void SetFOV(float p_FOV)
    {
        FOV = p_FOV;
    }

    public float GetFOV()
    {
        return FOV;
    }

}
