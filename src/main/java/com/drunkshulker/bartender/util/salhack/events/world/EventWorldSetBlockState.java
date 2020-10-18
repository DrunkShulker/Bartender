package com.drunkshulker.bartender.util.salhack.events.world;

import com.drunkshulker.bartender.util.salhack.events.MinecraftEvent;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;

public class EventWorldSetBlockState extends MinecraftEvent
{
    public BlockPos Pos;
    public IBlockState NewState;
    public int Flags;

    public EventWorldSetBlockState(BlockPos pos, IBlockState newState, int flags)
    {
        Pos = pos;
        NewState = newState;
        Flags = flags;
    }
}
