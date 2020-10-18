package com.drunkshulker.bartender.util.salhack.events.player;

import com.drunkshulker.bartender.util.salhack.events.MinecraftEvent;
import net.minecraft.util.EnumHand;

public class EventPlayerSwingArm extends MinecraftEvent
{
    public EnumHand Hand;
    
    public EventPlayerSwingArm(EnumHand p_Hand)
    {
        super();
        Hand = p_Hand;
    }
}
