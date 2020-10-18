package com.drunkshulker.bartender.client.module;

import java.util.ArrayList;
import java.util.List;

import com.drunkshulker.bartender.client.gui.clickgui.ClickGuiSetting;
import com.drunkshulker.bartender.util.salhack.RenderUtil;


import com.drunkshulker.bartender.util.salhack.events.network.EventNetworkPacketEvent;
import com.drunkshulker.bartender.util.salhack.events.render.RenderEvent;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listenable;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.util.math.AxisAlignedBB;

public class NewChunks implements Listenable {
    public static boolean enabled = false, render = false;
    private static int color = 0xFF9900EE;
    private static float width = 1;
    private ICamera frustum = new Frustum();
    Minecraft mc = Minecraft.getMinecraft();
    public static List<ChunkData> chunkDataList = new ArrayList<>();

    @EventHandler
    private Listener<EventNetworkPacketEvent> PacketEvent = new Listener<>(p_Event ->
    {
        if (!enabled) return;

        if (p_Event.getPacket() instanceof SPacketChunkData) {
            final SPacketChunkData packet = (SPacketChunkData) p_Event.getPacket();
            if (!packet.isFullChunk()) {
                final ChunkData chunk = new ChunkData(packet.getChunkX() * 16, packet.getChunkZ() * 16);

                if (!chunkDataList.contains(chunk)) {
                    chunkDataList.add(chunk);
                }
            }
        }
    });

    @EventHandler
    private Listener<RenderEvent> OnRenderEvent = new Listener<>(p_Event ->
    {
        if (!enabled|| !render) return;

        if (mc.getRenderManager() == null)
            return;

        for (ChunkData chunkData : new ArrayList<ChunkData>(this.chunkDataList)) {
            if (chunkData != null) {
                this.frustum.setPosition(mc.getRenderViewEntity().posX, mc.getRenderViewEntity().posY, mc.getRenderViewEntity().posZ);

                final AxisAlignedBB bb = new AxisAlignedBB(chunkData.x, 0, chunkData.z, chunkData.x + 16, 1, chunkData.z + 16);

                if (frustum.isBoundingBoxInFrustum(bb)) {
                    RenderUtil.drawPlane(chunkData.x - mc.getRenderManager().viewerPosX, -mc.getRenderManager().viewerPosY,
                            chunkData.z - mc.getRenderManager().viewerPosZ, new AxisAlignedBB(0, 0, 0, 16, 1, 16), width, color);
                }
            }
        }
    });

    public static void clickAction(String title) {
        if(title.equals("clear cache")){
            chunkDataList.clear();
        }
    }

    public static class ChunkData {
        private int x;
        private int z;

        public ChunkData(int x, int z) {
            this.x = x;
            this.z = z;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getZ() {
            return z;
        }

        public void setZ(int z) {
            this.z = z;
        }
    }

    public static void applyPreferences(ClickGuiSetting[] contents) {
        for (ClickGuiSetting setting : contents) {
            switch (setting.title) {
                case "state":
                    enabled = setting.value==1;
                    break;
                case "render":
                    render = setting.value==1;
                    break;
                case "color":
                    if(setting.value==0) color = 0xFFFF0000;
                    else color = 0xFF9900EE;
                    break;
                case "width":
                    width = setting.values.get(setting.value).getAsFloat();
                    break;
                default:
                    break;
            }
        }
    }
}
