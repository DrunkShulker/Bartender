package com.drunkshulker.bartender.client.module;


import com.drunkshulker.bartender.Bartender;
import com.drunkshulker.bartender.client.gui.clickgui.ClickGuiSetting;
import com.drunkshulker.bartender.util.salhack.events.player.EventPlayerUpdate;
import com.drunkshulker.bartender.util.salhack.events.render.RenderEvent;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listenable;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class StorageESP implements Listenable {
    public final boolean EnderChests = true;
    public final boolean Chests = true;
    public final boolean Shulkers = true;

    private Minecraft mc = Bartender.MC;
    public final List<StorageBlockPos> Storages = new ArrayList<>();
    private ICamera camera = new Frustum();
    static boolean enabled = false;

    public static void applyPreferences(ClickGuiSetting[] contents) {
        for (ClickGuiSetting setting : contents) {
            if (setting.title.equals("storageESP")) enabled = setting.value == 1;
        }
    }

    @EventHandler
    private Listener<EventPlayerUpdate> OnPlayerUpdate = new Listener<>(p_Event ->
    {
        if (!enabled)return;
        Storages.clear();

        mc.world.loadedTileEntityList.forEach(p_Tile ->
        {
            if (p_Tile instanceof TileEntityEnderChest && EnderChests)
                Storages.add(new StorageBlockPos(p_Tile.getPos().getX(), p_Tile.getPos().getY(), p_Tile.getPos().getZ(), StorageType.Ender));
            else if (p_Tile instanceof TileEntityChest && Chests)
                Storages.add(new StorageBlockPos(p_Tile.getPos().getX(), p_Tile.getPos().getY(), p_Tile.getPos().getZ(), StorageType.Chest));
            else if (p_Tile instanceof TileEntityShulkerBox && Shulkers)
                Storages.add(new StorageBlockPos(p_Tile.getPos().getX(), p_Tile.getPos().getY(), p_Tile.getPos().getZ(), StorageType.Shulker));
        });
    });

    @EventHandler
    private Listener<RenderEvent> OnRenderEvent = new Listener<>(p_Event ->
    {
        if (!enabled)return;
        if (mc.getRenderManager() == null || mc.getRenderManager().options == null)
            return;

        new ArrayList<StorageBlockPos>(Storages).forEach(p_Pos ->
        {
            final AxisAlignedBB bb = new AxisAlignedBB(p_Pos.getX() - mc.getRenderManager().viewerPosX, p_Pos.getY() - mc.getRenderManager().viewerPosY,
                    p_Pos.getZ() - mc.getRenderManager().viewerPosZ, p_Pos.getX() + 1 - mc.getRenderManager().viewerPosX, p_Pos.getY() + 1 - mc.getRenderManager().viewerPosY,
                    p_Pos.getZ() + 1 - mc.getRenderManager().viewerPosZ);

            camera.setPosition(mc.getRenderViewEntity().posX, mc.getRenderViewEntity().posY, mc.getRenderViewEntity().posZ);

            if (camera.isBoundingBoxInFrustum(new AxisAlignedBB(bb.minX + mc.getRenderManager().viewerPosX, bb.minY + mc.getRenderManager().viewerPosY, bb.minZ + mc.getRenderManager().viewerPosZ,
                    bb.maxX + mc.getRenderManager().viewerPosX, bb.maxY + mc.getRenderManager().viewerPosY, bb.maxZ + mc.getRenderManager().viewerPosZ))) {
                GlStateManager.pushMatrix();
                switch (p_Pos.GetType()) {
                    case Chest:
                        int hex = 0xFF8C00;
                        int r = (hex & 0xFF0000) >> 16;
                        int g = (hex & 0xFF00) >> 8;
                        int b = hex & 0xFF;
                        GlStateManager.color(r / 255F, g / 255F, b / 255F, 1);
                        RenderGlobal.drawBoundingBox(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ, 0.94f, 1.0f, 0f, 0.6f);
                        break;
                    case Ender:
                        hex = 0xFF00DC;
                        r = (hex & 0xFF0000) >> 16;
                        g = (hex & 0xFF00) >> 8;
                        b = hex & 0xFF;
                        GlStateManager.color(r / 255F, g / 255F, b / 255F, 1);
                        RenderGlobal.drawBoundingBox(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ, 0.65f, 0f, 0.93f, 0.6f);
                        break;
                    case Shulker:
                        hex = 0xFF0000;
                        r = (hex & 0xFF0000) >> 16;
                        g = (hex & 0xFF00) >> 8;
                        b = hex & 0xFF;
                        GlStateManager.color(r / 255F, g / 255F, b / 255F, 1);
                        RenderGlobal.drawBoundingBox(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ, 1.0f, 0.0f, 0.59f, 0.6f);
                        break;
                    default:
                        break;
                }

                GlStateManager.popMatrix();
            }
        });
    });

    public enum StorageType {
        Chest,
        Shulker,
        Ender,
    }

    public class StorageBlockPos extends BlockPos {
        public StorageType Type;

        public StorageBlockPos(int x, int y, int z, StorageType p_Type) {
            super(x, y, z);

            Type = p_Type;
        }

        public StorageType GetType() {
            return Type;
        }
    }
}