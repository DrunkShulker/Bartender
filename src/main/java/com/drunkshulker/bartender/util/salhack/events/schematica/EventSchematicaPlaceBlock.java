package com.drunkshulker.bartender.util.salhack.events.schematica;

import com.drunkshulker.bartender.util.salhack.events.MinecraftEvent;
import net.minecraft.util.math.BlockPos;

public class EventSchematicaPlaceBlock extends MinecraftEvent
{
    public BlockPos Pos;
    
    public EventSchematicaPlaceBlock(BlockPos p_Pos)
    {
        Pos = p_Pos;
    }
}
