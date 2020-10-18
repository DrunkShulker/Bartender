package com.drunkshulker.bartender.util.salhack.events.render;

import com.drunkshulker.bartender.util.salhack.events.MinecraftEvent;
import com.google.common.base.Predicate;


import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;

public class EventRenderGetEntitiesINAABBexcluding extends MinecraftEvent
{

    public EventRenderGetEntitiesINAABBexcluding(WorldClient worldClient, Entity entityIn, AxisAlignedBB boundingBox, Predicate predicate)
    {

    }

}
