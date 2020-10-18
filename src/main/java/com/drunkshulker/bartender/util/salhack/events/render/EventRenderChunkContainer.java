package com.drunkshulker.bartender.util.salhack.events.render;

import com.drunkshulker.bartender.util.salhack.events.MinecraftEvent;
import net.minecraft.client.renderer.chunk.RenderChunk;

public class EventRenderChunkContainer extends MinecraftEvent
{
    public RenderChunk RenderChunk;
    public EventRenderChunkContainer(RenderChunk renderChunk)
    {
        RenderChunk = renderChunk;
    }
}
