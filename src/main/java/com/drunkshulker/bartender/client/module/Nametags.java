package com.drunkshulker.bartender.client.module;

import com.drunkshulker.bartender.Bartender;
import com.drunkshulker.bartender.client.gui.clickgui.ClickGuiSetting;
import com.drunkshulker.bartender.client.social.PlayerFriends;
import com.drunkshulker.bartender.client.social.PlayerGroup;
import com.drunkshulker.bartender.util.kami.EntityUtils;
import com.drunkshulker.bartender.util.salhack.FontManager;
import com.drunkshulker.bartender.util.salhack.GLUProjection;
import com.drunkshulker.bartender.util.salhack.MathUtil;
import com.drunkshulker.bartender.util.salhack.RenderUtil;
import com.drunkshulker.bartender.util.salhack.events.render.EventRenderEntityName;
import com.drunkshulker.bartender.util.salhack.events.render.EventRenderGameOverlay;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listenable;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import com.google.common.collect.Lists;
import com.mojang.realmsclient.gui.ChatFormatting;

public class Nametags implements Listenable
{
    public final boolean Armor = true;
    public final boolean Durability = false;
    public final boolean ItemName = false;
    public final boolean Health = false;
    public final boolean Invisibles = true;
    public final boolean EntityID = false;
    public final boolean GameMode = false;
    public final boolean Ping = false;
    private final static Minecraft mc = Bartender.MC;
    private ICamera camera = new Frustum();
    public static boolean enabled = false;

    public static void applyPreferences(ClickGuiSetting[] contents) {
        for (ClickGuiSetting setting : contents) {
            if (setting.title.equals("nametags")) enabled = setting.value == 1;
        }
    }

    @EventHandler
    private Listener<EventRenderGameOverlay> OnRenderGameOverlay = new Listener<>(p_Event ->
    {
        if(!enabled||mc.player==null) return;
        mc.world.loadedEntityList.stream().filter(EntityUtils::isLiving).filter(entity -> !EntityUtils.isFakeLocalPlayer(entity)).filter(entity -> (entity instanceof EntityPlayer && mc.player != entity)).forEach(e ->
        {
            
            RenderNameTagFor((EntityPlayer)e, p_Event);
        });
    });


    private void RenderNameTagFor(EntityPlayer e, EventRenderGameOverlay p_Event)
    {
        final float[] bounds = this.convertBounds(e, p_Event.getPartialTicks(),
                p_Event.getScaledResolution().getScaledWidth(),
                p_Event.getScaledResolution().getScaledHeight());

        if (bounds != null)
        {
            String name = StringUtils.stripControlCodes(e.getName());

            int color = -1;

            final String friend = e.getDisplayNameString();

            if (PlayerFriends.impactFriends.contains(friend)||PlayerFriends.friends.contains(friend)|| PlayerGroup.members.contains(friend))
            {
                name = friend;
                color = 0x00FF21;
            } else if(PlayerGroup.DEFAULTS.contains(name)){
                name = friend;
                color = 0xFF00DC;
            }

            final EntityPlayer player = (EntityPlayer) e;
            int responseTime = -1;

            if (Ping)
            {
                try
                {
                    responseTime = (int) MathUtil.clamp(
                            mc.getConnection().getPlayerInfo(player.getUniqueID()).getResponseTime(), 0,
                            300);
                }
                catch (NullPointerException np)
                {}
            }

            
            String l_Name = name;
                    RenderUtil.drawStringWithShadow(l_Name,
                    bounds[0] + (bounds[2] - bounds[0]) / 2 - RenderUtil.getStringWidth(l_Name) / 2,
                    bounds[1] + (bounds[3] - bounds[1]) - 8 - 1, color);

            if (Armor)
            {
                final Iterator<ItemStack> items = e.getArmorInventoryList().iterator();
                final ArrayList<ItemStack> stacks = new ArrayList<>();


                stacks.add(e.getHeldItemOffhand());

                while (items.hasNext())
                {
                    final ItemStack stack = items.next();
                    if (stack != null && stack.getItem() != Items.AIR)
                    {
                        stacks.add(stack);
                    }
                }
                stacks.add(e.getHeldItemMainhand());

                Collections.reverse(stacks);

                int x = 0;

                

                for (ItemStack stack : stacks)
                {
                    if (stack != null)
                    {
                        final Item item = stack.getItem();
                        if (item != Items.AIR)
                        {
                            GlStateManager.pushMatrix();
                            GlStateManager.enableBlend();
                            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                            RenderHelper.enableGUIStandardItemLighting();
                            GlStateManager.translate(
                                    bounds[0] + (bounds[2] - bounds[0]) / 2 + x - (16 * stacks.size() / 2),
                                    bounds[1] + (bounds[3] - bounds[1]) - mc.fontRenderer.FONT_HEIGHT - 19,
                                    0);
                            mc.getRenderItem().renderItemAndEffectIntoGUI(stack, 0, 0);
                            mc.getRenderItem().renderItemOverlays(mc.fontRenderer, stack, 0, 0);
                            RenderHelper.disableStandardItemLighting();
                            GlStateManager.disableBlend();
                            GlStateManager.color(1, 1, 1, 1);
                            GlStateManager.popMatrix();
                            x += 16;

                            if (this.Durability)
                            {
                                final List<String> stringsToDraw = Lists.newArrayList();

                                if (stack.isItemDamaged())
                                {
                                    float l_ArmorPct = ((float)(stack.getMaxDamage()-stack.getItemDamage()) /  (float)stack.getMaxDamage())*100.0f;
                                    float l_ArmorBarPct = Math.min(l_ArmorPct, 100.0f);

                                    stringsToDraw.add(String.format("%s", (int)l_ArmorBarPct + "%"));
                                }
                                int y = 0;

                                if (stack.getEnchantmentTagList() != null)
                                {
                                    final NBTTagList tags = stack.getEnchantmentTagList();
                                    for (int i = 0; i < tags.tagCount(); i++)
                                    {
                                        final NBTTagCompound tagCompound = tags.getCompoundTagAt(i);
                                        if (tagCompound != null && Enchantment
                                                .getEnchantmentByID(tagCompound.getByte("id")) != null)
                                        {
                                            final Enchantment enchantment = Enchantment
                                                    .getEnchantmentByID(tagCompound.getShort("id"));
                                            final short lvl = tagCompound.getShort("lvl");
                                            if (enchantment != null)
                                            {
                                                String ench = "";
                                                if (enchantment.isCurse())
                                                {
                                                    ench = ChatFormatting.RED
                                                            + enchantment.getTranslatedName(lvl)
                                                            .substring(11).substring(0, 2)
                                                            + ChatFormatting.GRAY + lvl;
                                                }
                                                else
                                                {
                                                    ench = enchantment.getTranslatedName(lvl).substring(0,
                                                            2) + lvl;
                                                }
                                                stringsToDraw.add(ench);
                                            }
                                        }
                                    }
                                }

                                
                                

                                for (String string : stringsToDraw)
                                {
                                    GlStateManager.pushMatrix();
                                    GlStateManager.disableDepth();
                                    GlStateManager
                                            .translate(
                                                    bounds[0] + (bounds[2] - bounds[0]) / 2 + x
                                                            - ((16.0f * stacks.size()) / 2.0f)
                                                            - (16.0f / 2.0f)
                                                            - (RenderUtil.getStringWidth(string)
                                                            / 4.0f),
                                                    bounds[1] + (bounds[3] - bounds[1])
                                                            - mc.fontRenderer.FONT_HEIGHT - 23 - y,
                                                    0);
                                    GlStateManager.scale(0.5f, 0.5f, 0.5f);
                                    RenderUtil.drawStringWithShadow(string, 0, 0, -1);
                                    GlStateManager.scale(2, 2, 2);
                                    GlStateManager.enableDepth();
                                    GlStateManager.popMatrix();
                                    y += 4;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    private Listener<EventRenderEntityName> OnRenderEntityName = new Listener<>(p_Event ->
    {
        if(!enabled) return;
        p_Event.cancel();
    });

    private float[] convertBounds(Entity e, float partialTicks, int width, int height) {
        float x = -1;
        float y = -1;
        float w = width + 1;
        float h = height + 1;

        final Vec3d pos = MathUtil.interpolateEntity(e, partialTicks);

        if (pos == null) {
            return null;
        }

        AxisAlignedBB bb = e.getEntityBoundingBox();

        if (e instanceof EntityEnderCrystal) {
            bb = new AxisAlignedBB(bb.minX + 0.3f, bb.minY + 0.2f, bb.minZ + 0.3f, bb.maxX - 0.3f, bb.maxY, bb.maxZ - 0.3f);
        }

        if (e instanceof EntityItem) {
            bb = new AxisAlignedBB(bb.minX, bb.minY + 0.7f, bb.minZ, bb.maxX, bb.maxY, bb.maxZ);
        }

        bb = bb.expand(0.15f, 0.1f, 0.15f);

        camera.setPosition(Minecraft.getMinecraft().getRenderViewEntity().posX, Minecraft.getMinecraft().getRenderViewEntity().posY, Minecraft.getMinecraft().getRenderViewEntity().posZ);

        if (!camera.isBoundingBoxInFrustum(bb)) {
            return null;
        }

        final Vec3d corners[] = {
                new Vec3d(bb.minX - bb.maxX + e.width / 2, 0, bb.minZ - bb.maxZ + e.width / 2),
                new Vec3d(bb.maxX - bb.minX - e.width / 2, 0, bb.minZ - bb.maxZ + e.width / 2),
                new Vec3d(bb.minX - bb.maxX + e.width / 2, 0, bb.maxZ - bb.minZ - e.width / 2),
                new Vec3d(bb.maxX - bb.minX - e.width / 2, 0, bb.maxZ - bb.minZ - e.width / 2),

                new Vec3d(bb.minX - bb.maxX + e.width / 2, bb.maxY - bb.minY, bb.minZ - bb.maxZ + e.width / 2),
                new Vec3d(bb.maxX - bb.minX - e.width / 2, bb.maxY - bb.minY, bb.minZ - bb.maxZ + e.width / 2),
                new Vec3d(bb.minX - bb.maxX + e.width / 2, bb.maxY - bb.minY, bb.maxZ - bb.minZ - e.width / 2),
                new Vec3d(bb.maxX - bb.minX - e.width / 2, bb.maxY - bb.minY, bb.maxZ - bb.minZ - e.width / 2)
        };

        for (Vec3d vec : corners) {
            final GLUProjection.Projection projection = GLUProjection.getInstance().project(pos.x + vec.x - Minecraft.getMinecraft().getRenderManager().viewerPosX, pos.y + vec.y - Minecraft.getMinecraft().getRenderManager().viewerPosY, pos.z + vec.z - Minecraft.getMinecraft().getRenderManager().viewerPosZ, GLUProjection.ClampMode.NONE, false);

            if (projection == null) {
                return null;
            }

            x = Math.max(x, (float) projection.getX());
            y = Math.max(y, (float) projection.getY());

            w = Math.min(w, (float) projection.getX());
            h = Math.min(h, (float) projection.getY());
        }

        if (x != -1 && y != -1 && w != width + 1 && h != height + 1) {
            return new float[]{x, y, w, h};
        }

        return null;
    }
}