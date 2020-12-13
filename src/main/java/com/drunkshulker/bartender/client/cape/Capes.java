
package com.drunkshulker.bartender.client.cape;

import com.drunkshulker.bartender.Bartender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Capes {

    static ResourceLocation cachedTexture = getCapeTexture();

    public static ResourceLocation getCapeTexture() {
        ResourceLocation location = null;
        location = Minecraft.getMinecraft().getTextureManager()
                .getDynamicTextureLocation(Bartender.MOD_ID, new DynamicTexture(ImageUtils.getImageFromURL("https:/"+"/firebasestorage.googleapis.com/v0/b/g3fh-h56f-x9da-0asd.appspot.com/o/elytra.png?alt=media")));

        return location;
    }

    static public ResourceLocation getCachedTexture() {
        return cachedTexture;
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (!(event.getEntity() instanceof EntityPlayer)) return; 
        if (event.getEntity().getPersistentID().equals(Bartender.MC.getSession().getProfile().getId())) renderCape();
    }

    private void renderCape() {
        Bartender.MC.gameSettings.setModelPartEnabled(EnumPlayerModelParts.CAPE, true);
        for (RenderPlayer render : Minecraft.getMinecraft().getRenderManager().getSkinMap().values()) {
            render.addLayer(new CapeLayer(render));
        }
    }
}
