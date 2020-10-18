package com.drunkshulker.bartender.util.salhack.events.player;

import com.drunkshulker.bartender.util.salhack.events.MinecraftEvent;
public class EventPlayerJump extends MinecraftEvent
{
    public double MotionX;
    public double MotionY;
    
    public EventPlayerJump(double p_MotionX, double p_MotionY)
    {
        super();
        MotionX = p_MotionX;
        MotionY = p_MotionY;
    }
}
