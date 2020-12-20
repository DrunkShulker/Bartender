
package com.drunkshulker.bartender.client.cape;

import com.drunkshulker.bartender.Bartender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerCape;
import net.minecraft.client.renderer.entity.layers.LayerElytra;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.*;

public class Capes {

    static ResourceLocation elytraDynamic = loadTexture();

    final static ResourceLocation
            elytraCza = new ResourceLocation(Bartender.MOD_ID, "textures/capes/elytra_cza.png"),
            elytraDani = new ResourceLocation(Bartender.MOD_ID, "textures/capes/elytra_dani.png"),
            elytraDefault = new ResourceLocation(Bartender.MOD_ID, "textures/capes/elytra_default.png"),
            elytraDrunk = new ResourceLocation(Bartender.MOD_ID, "textures/capes/elytra_drunk.png"),
            elytraOwo = new ResourceLocation(Bartender.MOD_ID, "textures/capes/elytra_owoceano.png"),
            elytraSch = new ResourceLocation(Bartender.MOD_ID, "textures/capes/elytra_schwarze.png"),
            elytraVidde = new ResourceLocation(Bartender.MOD_ID, "textures/capes/elytra_vidde.png");

    static HashMap<UUID, ResourceLocation> capeOwners = new HashMap<UUID, ResourceLocation>(){{
        put(UUID.fromString("6e35bad0-1395-49c2-9ac3-64472ea03cda"), elytraCza);
        put(UUID.fromString("9d59eec8-2fb8-4a57-bf8e-5a3a1ec0e2b1"), elytraDani);
        put(UUID.fromString("f84e53c5-9143-4934-860c-ea44c9ad0e9f"), elytraOwo);
        put(UUID.fromString("eebb0cc0-44aa-49fa-b4db-6f532a0df993"), elytraSch);
        put(UUID.fromString("b8eb84d0-e80e-464a-a71e-1f9ef3a26772"), elytraVidde);
        put(UUID.fromString("7d007140-d769-40fb-aac3-056e7f5d2fea"), elytraDrunk);
        put(UUID.fromString("90f14c5d-ceb6-4b0b-863d-7c12c60fb157"), elytraDrunk);
        put(UUID.fromString("654f57e2-4928-4f31-9ad8-f59c4780b39a"), elytraDrunk);
    }};

    public static ResourceLocation loadTexture() {
        ResourceLocation location = null;
        location = Minecraft.getMinecraft().getTextureManager()
                .getDynamicTextureLocation(Bartender.MOD_ID, new DynamicTexture(ImageUtils.getImageFromURL("https:/"+"/firebasestorage.googleapis.com/v0/b/g3fh-h56f-x9da-0asd.appspot.com/o/elytra.png?alt=media")));
        return location;
    }

    static public ResourceLocation getTextureFor(UUID playerUID) {
        
        if(capeOwners.containsKey(playerUID)){
            return capeOwners.get(playerUID);
        }
        
        if(Bartender.MC.getSession().getProfile().getId().equals(playerUID))
        return elytraDynamic;
        else return elytraDefault;
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (!(event.getEntity() instanceof EntityPlayer)) return; 
        if (event.getEntity().getPersistentID().equals(Bartender.MC.getSession().getProfile().getId())) renderCape();
    }

    private void renderCape() {
        Bartender.MC.gameSettings.setModelPartEnabled(EnumPlayerModelParts.CAPE, true);
        for (RenderPlayer render : Minecraft.getMinecraft().getRenderManager().getSkinMap().values()) {
            List<LayerRenderer<AbstractClientPlayer>> filteredLayers = new ArrayList();
            for (LayerRenderer layerRenderer:render.layerRenderers) {
                if(layerRenderer instanceof LayerElytra) continue;
                if(layerRenderer instanceof LayerCape) continue;
                filteredLayers.add(layerRenderer);
            }
            render.layerRenderers = filteredLayers;
            render.addLayer(new CapeLayer(render));
            render.addLayer(new ElytraLayer(render));
        }
    }
}
