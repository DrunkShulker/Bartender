package com.drunkshulker.bartender.util.salhack.events.player;

import com.drunkshulker.bartender.util.salhack.events.MinecraftEvent;
import net.minecraft.entity.Entity;
import net.minecraft.potion.Potion;

public class EventPlayerIsPotionActive extends MinecraftEvent
{
    public Potion potion;
    
    public EventPlayerIsPotionActive(Potion p_Potion)
    {
        super();
        
        potion = p_Potion;
    }
}
