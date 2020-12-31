package com.drunkshulker.bartender.client.module;

import com.drunkshulker.bartender.Bartender;
import com.drunkshulker.bartender.client.gui.clickgui.ClickGuiSetting;
import com.drunkshulker.bartender.util.salhack.RenderUtil;
import com.drunkshulker.bartender.util.salhack.events.player.EventPlayerUpdate;
import com.drunkshulker.bartender.util.salhack.events.render.RenderEvent;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listenable;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class RoofESP implements Listenable {
    public static boolean enabled = false;
    final int Radius = 8;
    public final List<Hole> holes = new ArrayList<>();
    private ICamera camera = new Frustum();
    Minecraft mc = Bartender.MC;

    private class Hole extends Vec3i {
        private BlockPos blockPos;
        private boolean tall;


        public Hole(int x, int y, int z, final BlockPos pos) {
            super(x, y, z);
            blockPos = pos;
        }

        public Hole(int x, int y, int z, final BlockPos pos, boolean tall) {
            super(x, y, z);
            blockPos = pos;
            this.tall = true;
        }

        public boolean isTall() {
            return tall;
        }

        public BlockPos GetBlockPos() {
            return blockPos;
        }

    }

    public static void applyPreferences(ClickGuiSetting[] contents) {
        for (ClickGuiSetting setting : contents) {
            if (setting.title.equals("roofESP")) {
                enabled = setting.value == 1;
            }
        }
    }

    
    

    final int roofLevel = 127;

    @EventHandler
    private Listener<EventPlayerUpdate> OnPlayerUpdate = new Listener<>(p_Event ->
    {
        
        if (!enabled || Bartender.MC.player == null || Bartender.MC.player.dimension != -1) {
            holes.clear();
            return;
        }
        this.holes.clear();

        final Vec3i playerPos = new Vec3i(Bartender.MC.player.posX, Bartender.MC.player.posY, Bartender.MC.player.posZ);

        for (int x = playerPos.getX() - Radius; x < playerPos.getX() + Radius; x++) {
            for (int z = playerPos.getZ() - Radius; z < playerPos.getZ() + Radius; z++) {
                for (int y = roofLevel; y > roofLevel - 3; y--) {

                    final BlockPos blockPos = new BlockPos(x, y, z);

                    final IBlockState blockState = Bartender.MC.world.getBlockState(blockPos);

                    boolean isBedrock = isBlockValid(blockState, blockPos);
                    if(isBedrock) continue;
                    if(y==roofLevel){}
                    else if(y==126){
                        final BlockPos blockPoss = new BlockPos(x, y-1, z);

                        final IBlockState blockStatee = Bartender.MC.world.getBlockState(blockPoss);

                        isBedrock = isBlockValid(blockStatee, blockPoss);
                    }
                    else if(y==125){
                        final BlockPos blockPosss = new BlockPos(x, y+1, z);

                        final IBlockState blockStateee = Bartender.MC.world.getBlockState(blockPosss);

                        isBedrock = isBlockValid(blockStateee, blockPosss);
                    }

                    if (!isBedrock)
                        this.holes.add(new Hole(blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos));

                }
            }
        }
    });

    private boolean isBlockValid(IBlockState blockState, BlockPos pos) {
        try {
            return blockState.getBlock() == Blocks.BEDROCK;
        } catch (final Throwable e) {
            return false;
        }
    }

    @EventHandler
    private Listener<RenderEvent> OnRenderEvent = new Listener<>(p_Event ->
    {
        if (!enabled || Bartender.MC.player == null || Bartender.MC.player.dimension != -1) return;

        if (Bartender.MC.getRenderManager() == null || Bartender.MC.getRenderManager().options == null)
            return;

            new ArrayList<Hole>(holes).forEach(p_Hole ->
            {
                final AxisAlignedBB bb = new AxisAlignedBB(p_Hole.getX() - mc.getRenderManager().viewerPosX, p_Hole.getY() - mc.getRenderManager().viewerPosY,
                        p_Hole.getZ() - mc.getRenderManager().viewerPosZ, p_Hole.getX() + 1 - mc.getRenderManager().viewerPosX, p_Hole.getY() + (p_Hole.isTall() ? 2 : 1) - mc.getRenderManager().viewerPosY,
                        p_Hole.getZ() + 1 - mc.getRenderManager().viewerPosZ);

                camera.setPosition(mc.getRenderViewEntity().posX, mc.getRenderViewEntity().posY, mc.getRenderViewEntity().posZ);

                if (camera.isBoundingBoxInFrustum(new AxisAlignedBB(bb.minX + mc.getRenderManager().viewerPosX, bb.minY + mc.getRenderManager().viewerPosY, bb.minZ + mc.getRenderManager().viewerPosZ,
                        bb.maxX + mc.getRenderManager().viewerPosX, bb.maxY + mc.getRenderManager().viewerPosY, bb.maxZ + mc.getRenderManager().viewerPosZ)))
                {
                    GlStateManager.pushMatrix();
                    GlStateManager.enableBlend();
                    GlStateManager.disableDepth();
                    GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
                    GlStateManager.disableTexture2D();
                    GlStateManager.depthMask(false);
                    GL11.glEnable(GL11.GL_LINE_SMOOTH);
                    GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
                    GL11.glLineWidth(1.5f);
                    if(p_Hole.getY()==roofLevel){
                        RenderGlobal.drawBoundingBox(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ, 0.823f, 0.160f, 1f, 0.4f);
                        RenderGlobal.renderFilledBox(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ, 0.823f, 0.160f, 1f, 0.4f);

                    } else {
                        RenderGlobal.drawBoundingBox(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ, 1f, 1f, 1f, 0.4f);
                        RenderGlobal.renderFilledBox(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ, 1f, 1f, 1f, 0.4f);

                    }
                    GL11.glDisable(GL11.GL_LINE_SMOOTH);
                    GlStateManager.depthMask(true);
                    GlStateManager.enableDepth();
                    GlStateManager.enableTexture2D();
                    GlStateManager.disableBlend();
                    GlStateManager.popMatrix();
                }
            });

    });
}

