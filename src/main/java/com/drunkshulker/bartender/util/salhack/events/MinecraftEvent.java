package com.drunkshulker.bartender.util.salhack.events;



import me.zero.alpine.fork.event.type.Cancellable;
import net.minecraft.client.Minecraft;

public class MinecraftEvent extends Cancellable
{
    private Era era = Era.PRE;
    private final float partialTicks;

    public MinecraftEvent()
    {
        partialTicks = Minecraft.getMinecraft().getRenderPartialTicks();
    }

    public MinecraftEvent(Era p_Era)
    {
        partialTicks = Minecraft.getMinecraft().getRenderPartialTicks();
        era = p_Era;
    }

    public Era getEra()
    {
        return era;
    }

    public float getPartialTicks()
    {
        return partialTicks;
    }

    public enum Era
    {
        PRE,
        PERI,
        POST
    }

}