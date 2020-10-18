package com.drunkshulker.bartender.util.salhack.events.player;

import com.drunkshulker.bartender.util.salhack.events.MinecraftEvent;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class EventPlayerDestroyBlock extends MinecraftEvent
{
    public BlockPos Location;

    public EventPlayerDestroyBlock(BlockPos loc)
    {
        Location = loc;
    }
}
