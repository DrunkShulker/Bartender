package com.drunkshulker.bartender.client.cape;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelElytra;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

public class ElytraLayer implements LayerRenderer<AbstractClientPlayer> {

    private final RenderPlayer renderPlayer;
    public ElytraLayer(RenderPlayer playerRendererIn) {
        this.renderPlayer = playerRendererIn;
    }
    private final ModelElytra modelElytra = new ModelElytra();

    @Override
    public void doRenderLayer(AbstractClientPlayer clientPlayer, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        ItemStack chestStack = clientPlayer.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        if (chestStack.getItem() == Items.ELYTRA) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableBlend();
            renderPlayer.bindTexture(Capes.getTextureFor(clientPlayer.getUniqueID()));
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0F, 0.0F, 0.125F);
            modelElytra.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, clientPlayer);
            modelElytra.render(clientPlayer, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            if (chestStack.isItemEnchanted())
                LayerArmorBase.renderEnchantedGlint(this.renderPlayer, clientPlayer, this.modelElytra, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
            GlStateManager.popMatrix();
        }
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}
