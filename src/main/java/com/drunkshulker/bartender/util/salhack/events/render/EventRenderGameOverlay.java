package com.drunkshulker.bartender.util.salhack.events.render;

import com.drunkshulker.bartender.util.salhack.events.MinecraftEvent;
import net.minecraft.client.gui.ScaledResolution;

public class EventRenderGameOverlay extends MinecraftEvent
{
    public float PartialTicks;
    public ScaledResolution scaledResolution;

    public EventRenderGameOverlay(float p_PartialTicks, ScaledResolution p_Res)
    {
        super();
        PartialTicks = p_PartialTicks;
        scaledResolution = p_Res;
    }

    public ScaledResolution getScaledResolution()
    {
        return scaledResolution;
    }

}
