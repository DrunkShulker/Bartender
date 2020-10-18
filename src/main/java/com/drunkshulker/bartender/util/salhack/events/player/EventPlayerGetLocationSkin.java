package com.drunkshulker.bartender.util.salhack.events.player;

import com.drunkshulker.bartender.mixins.client.MixinAbstractClientPlayer;
import com.drunkshulker.bartender.util.salhack.events.MinecraftEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class EventPlayerGetLocationSkin extends MinecraftEvent
{
    private ResourceLocation m_Location = null;
    public MixinAbstractClientPlayer Player;
    
    public EventPlayerGetLocationSkin(MixinAbstractClientPlayer p_Player)
    {
        super();
        
        Player = p_Player;
    }
    
    public void SetResourceLocation(ResourceLocation p_Location)
    {
        m_Location = p_Location;
    }

    public ResourceLocation GetResourceLocation()
    {
        return m_Location;
    }
}
