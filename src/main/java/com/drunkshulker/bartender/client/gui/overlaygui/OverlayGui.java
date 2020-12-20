package com.drunkshulker.bartender.client.gui.overlaygui;

import com.drunkshulker.bartender.client.module.*;
import com.drunkshulker.bartender.util.Config;
import com.drunkshulker.bartender.util.kami.*;
import com.google.gson.*;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import org.lwjgl.opengl.GL11;

import com.drunkshulker.bartender.Bartender;
import com.drunkshulker.bartender.client.gui.GuiHandler;
import com.drunkshulker.bartender.client.gui.clickgui.BeveledBox;
import com.drunkshulker.bartender.client.input.ChatObserver;
import com.drunkshulker.bartender.client.social.PlayerGroup;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.GameType;

import java.io.File;
import java.util.*;

public class OverlayGui extends Gui {
    public static int groupListBottom = 4 + 48;
    public static boolean targetGuiActive = false;
    public static ArrayList<String> availableTargets = new ArrayList<>();
    public static int currentSelectedTargetIndex = 0;
    static long lastPopupMessage = 0;
    static String lastTargetedEnemy = "";

    static JsonObject transforms = new JsonObject();
    static int[] anchorWatermark = new int[]{0, 0};
    static int[] anchorGroup = new int[]{0, 0};
    static int[] anchorPlayers = new int[]{0, 0};
    static int[] anchorInventory = new int[]{0, 0};
    static int[] anchorCoords = new int[]{0, 0};
    static int[] anchorPotions = new int[]{0, 0};
    static int[] anchorStatus = new int[]{0, 0};
    static int[] anchorTarget = new int[]{0, 0};
    static int[] anchorNumbers = new int[]{0, 0};
    static int[] anchorActions = new int[]{0, 0};
    static int[] anchorAP = new int[]{0, 0};

    public static final ResourceLocation texture = new ResourceLocation(Bartender.MOD_ID, "textures/gui/overlay.png");
    static final int textureWidth = 22;
    static final int textureHeight = 22;

    public static String lastGuiAction = "";
    public static long lastGuiActionStamp = System.currentTimeMillis();

    public static void saveLayout() {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson(transforms);
            Config.writeFile(Bartender.BARTENDER_DIR + "/bartender-hud.json", json);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to save bartender hud config");
        }
        updateTransforms();
    }

    public static void loadLayout() {
        try {
            String path = Bartender.BARTENDER_DIR + "/bartender-hud.json";
            if (new File(path).exists()) {
                String config = Config.readFile(path);
                transforms = new JsonParser().parse(config).getAsJsonObject();
            } else {
                System.out.println("No bartender hud config found");
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to load bartender hud config");
        }
        updateTransforms();
    }

    public static void updateTransforms() {
        String n = "watermark";
        if (transforms.get(n) != null && transforms.get(n).isJsonArray()) {
            anchorWatermark = new int[]{transforms.get(n).getAsJsonArray().get(0).getAsInt(), transforms.get(n).getAsJsonArray().get(1).getAsInt()};
        } else anchorWatermark = new int[]{0, 0};
        n = "group";
        if (transforms.get(n) != null && transforms.get(n).isJsonArray()) {
            anchorGroup = new int[]{transforms.get(n).getAsJsonArray().get(0).getAsInt(), transforms.get(n).getAsJsonArray().get(1).getAsInt()};
        } else anchorGroup = new int[]{0, 0};
        n = "players";
        if (transforms.get(n) != null && transforms.get(n).isJsonArray()) {
            anchorPlayers = new int[]{transforms.get(n).getAsJsonArray().get(0).getAsInt(), transforms.get(n).getAsJsonArray().get(1).getAsInt()};
        } else anchorPlayers = new int[]{0, 0};
        n = "inventory";
        if (transforms.get(n) != null && transforms.get(n).isJsonArray()) {
            anchorInventory = new int[]{transforms.get(n).getAsJsonArray().get(0).getAsInt(), transforms.get(n).getAsJsonArray().get(1).getAsInt()};
        } else anchorInventory = new int[]{0, 0};
        n = "coords";
        if (transforms.get(n) != null && transforms.get(n).isJsonArray()) {
            anchorCoords = new int[]{transforms.get(n).getAsJsonArray().get(0).getAsInt(), transforms.get(n).getAsJsonArray().get(1).getAsInt()};
        } else anchorCoords = new int[]{0, 0};
        n = "potions";
        if (transforms.get(n) != null && transforms.get(n).isJsonArray()) {
            anchorPotions = new int[]{transforms.get(n).getAsJsonArray().get(0).getAsInt(), transforms.get(n).getAsJsonArray().get(1).getAsInt()};
        } else anchorPotions = new int[]{0, 0};
        n = "status";
        if (transforms.get(n) != null && transforms.get(n).isJsonArray()) {
            anchorStatus = new int[]{transforms.get(n).getAsJsonArray().get(0).getAsInt(), transforms.get(n).getAsJsonArray().get(1).getAsInt()};
        } else anchorStatus = new int[]{0, 0};
        n = "target";
        if (transforms.get(n) != null && transforms.get(n).isJsonArray()) {
            anchorTarget = new int[]{transforms.get(n).getAsJsonArray().get(0).getAsInt(), transforms.get(n).getAsJsonArray().get(1).getAsInt()};
        } else anchorTarget = new int[]{0, 0};
        n = "numbers";
        if (transforms.get(n) != null && transforms.get(n).isJsonArray()) {
            anchorNumbers = new int[]{transforms.get(n).getAsJsonArray().get(0).getAsInt(), transforms.get(n).getAsJsonArray().get(1).getAsInt()};
        } else anchorNumbers = new int[]{0, 0};
        n = "actions";
        if (transforms.get(n) != null && transforms.get(n).isJsonArray()) {
            anchorActions = new int[]{transforms.get(n).getAsJsonArray().get(0).getAsInt(), transforms.get(n).getAsJsonArray().get(1).getAsInt()};
        } else anchorActions = new int[]{0, 0};
        n = "armor";
        if (transforms.get(n) != null && transforms.get(n).isJsonArray()) {
            anchorAP = new int[]{transforms.get(n).getAsJsonArray().get(0).getAsInt(), transforms.get(n).getAsJsonArray().get(1).getAsInt()};
        } else anchorAP = new int[]{0, 0};
    }

    public static void resetLayout() {
        transforms = new JsonObject();
        saveLayout();
    }

    public static void moveTarget(String target, int valueX, int valueY) {
        int prevX = 0, prevY = 0;
        if (transforms.get(target) != null && !transforms.get(target).isJsonNull()) {
            prevX = transforms.get(target).getAsJsonArray().get(0).getAsInt();
            prevY = transforms.get(target).getAsJsonArray().get(1).getAsInt();
        }
        JsonArray newValue = new JsonArray();
        newValue.add(prevX + valueX);
        newValue.add(prevY + valueY);
        transforms.add(target, newValue);

        saveLayout();
    }

    public OverlayGui(Minecraft mc) {
        ScaledResolution scaled = new ScaledResolution(mc);
        int width = scaled.getScaledWidth();
        int height = scaled.getScaledHeight();

        
        if (mc.currentScreen == null && GuiHandler.showBindInfo && System.currentTimeMillis() - lastGuiActionStamp < 1700)
            drawCenteredString(mc.fontRenderer, lastGuiAction, (width / 2) + anchorActions[0], ((height / 2) + 36) + anchorActions[1], Integer.parseInt("FFFFFF", 16));

        
        if (GuiHandler.showInventory) {
            List items = mc.player.inventory.mainInventory.subList(9, 36);
            for (int i = 0; i < items.size(); i++) {
                ItemStack itemStack = (ItemStack) items.get(i);
                int slotX = (width - (i % 3 * 18 + 1) - 19) + anchorInventory[0];
                int slotY = (2 + (i / 3 * 18 + 1)) + anchorInventory[1];

                GlStateUtils.blend(true);
                GlStateUtils.depth(true);
                RenderHelper.enableGUIStandardItemLighting();
                mc.renderItem.zLevel = 0.0f;
                mc.renderItem.renderItemAndEffectIntoGUI(itemStack, slotX, slotY);
                mc.renderItem.renderItemOverlays(mc.fontRenderer, itemStack, slotX, slotY);
                mc.renderItem.zLevel = 0.0f;
                RenderHelper.disableStandardItemLighting();
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                GlStateUtils.depth(false);
            }
        }

        
        if(!GuiHandler.showAP&&!mc.player.isCreative()&&!mc.player.isSpectator()){
            List items = mc.player.inventory.armorInventory;
            for (int i = 0; i < items.size(); i++) {
                ItemStack itemStack = (ItemStack) items.get(i);
                int slotX = ((width/2) - (i % 4 * 22 + 1) + 75) + anchorAP[0];
                int slotY = (height-56 + (i / 4 * 18 + 1)) + anchorAP[1];

                GlStateUtils.blend(true);
                GlStateUtils.depth(true);
                RenderHelper.enableGUIStandardItemLighting();
                mc.renderItem.zLevel = 0.0f;
                mc.renderItem.renderItemAndEffectIntoGUI(itemStack, slotX, slotY);
                mc.renderItem.renderItemOverlays(mc.fontRenderer, itemStack, slotX, slotY);
                mc.renderItem.zLevel = 0.0f;
                RenderHelper.disableStandardItemLighting();
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                GlStateUtils.depth(false);
            }
        }

        
        if (ElytraFlight.enabled
                && !mc.player.isSpectator()
                && !(mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() != Items.ELYTRA && mc.player.isCreative())) {
            if (ElytraFlight.easyTakeoff) {
                renderCustomTexture((width / 2) - 147, height - textureHeight, textureWidth * 2, 0, textureWidth, textureHeight, texture, 1f);
            } else if (ElytraFlight.mode == ElytraFlight.Mode.BOOST) {
                renderCustomTexture((width / 2) - 147, height - textureHeight, 0, 0, textureWidth, textureHeight, texture, 1f);
            } else {
                renderCustomTexture((width / 2) - 147, height - textureHeight, textureWidth, 0, textureWidth, textureHeight, texture, 1f);
            }
        }

        
        if (GuiHandler.showCoords) {
            String[] coords = CoordUtil.getCurrentCoordsDesc();
            drawString(mc.fontRenderer, coords[1], 4 + anchorCoords[0], (height - mc.fontRenderer.FONT_HEIGHT - 4) + anchorCoords[1], Integer.parseInt("FFFFFF", 16));
            drawString(mc.fontRenderer, coords[0], 4 + anchorCoords[0], (height - (mc.fontRenderer.FONT_HEIGHT * 2) - 6) + anchorCoords[1], Integer.parseInt("FFFFFF", 16));
        }

        
        if (GuiHandler.showPotions) {
            if (mc.player == null) return;

            ArrayList<PotionInfo> potions = new ArrayList<PotionInfo>();
            for (Map.Entry<Potion, PotionEffect> entry : mc.player.getActivePotionMap().entrySet()) {
                potions.add(new PotionInfo(entry.getValue(), I18n.format(entry.getKey().getName()), entry.getValue().getAmplifier()));
            }

            int textHeight = mc.fontRenderer.FONT_HEIGHT + 4;
            int posY = height - textHeight;

            for (PotionInfo potion : potions) {
                int color = potion.potionEffect.getPotion().getLiquidColor();
                String text = potion.formattedTimeLeft();
                int lineWidth = mc.fontRenderer.getStringWidth(text);
                mc.fontRenderer.drawString(text, (width - 4 - lineWidth) + anchorPotions[0], posY + anchorPotions[1], 0xffffff, true);
                text = potion.formattedName();
                mc.fontRenderer.drawString(text, (width - 4 - lineWidth - mc.fontRenderer.getStringWidth(text) - 3) + anchorPotions[0], posY + anchorPotions[1], color, true);
                posY -= mc.fontRenderer.FONT_HEIGHT + 2;
            }
        }

        
        int statusX = (width / 2) + anchorStatus[0];
        if (Bodyguard.enabled && BaseFinder.enabled) {
            drawCenteredString(mc.fontRenderer, "YOU CANT HAVE BASEFINDER AND BODYGUARD ENABLED AT THE SAME TIME", width / 2, (height / 2) - 25, Integer.parseInt("FF0000", 16));
        } else if (ChatObserver.partyTPA)
            drawCenteredString(mc.fontRenderer, "Party TPA enabled", statusX, 25 + anchorStatus[1], Integer.parseInt("FF0000", 16));
        else if (Bodyguard.enabled) {
            drawCenteredString(mc.fontRenderer, Bodyguard.getStatusString(), statusX, 25 + anchorStatus[1], Integer.parseInt("FFAA00", 16));
        } else if (BaseFinder.enabled) {
            drawCenteredString(mc.fontRenderer, BaseFinder.getStatusString(), statusX, 25 + anchorStatus[1], Integer.parseInt("FFAA00", 16));
        }

        
        if (targetGuiActive) {
            drawCenteredString(mc.fontRenderer, "Select target", (width / 2) + anchorTarget[0], ((height / 2) + 25) + anchorTarget[1], Integer.parseInt("62A2C4", 16));
        } else {
            
            if (System.currentTimeMillis() - lastPopupMessage < 2500) {
                drawCenteredString(mc.fontRenderer, "Eliminate " + lastTargetedEnemy, (width / 2) + anchorTarget[0], ((height / 2) + 25) + anchorTarget[1], Integer.parseInt("FF0000", 16));
            }
        }

        
        if (GuiHandler.ingameWaterMark)
            drawString(mc.fontRenderer,
                    (((Bartender.IMPACT_INSTALLED) ? "               + " : "") + Bartender.NAME + " " + Bartender.VERSION),
                    16 + anchorWatermark[0], 4 + anchorWatermark[1], Integer.parseInt("AAAAAA", 16));

        
        if (!mc.isSingleplayer() && GuiHandler.showGroup && PlayerGroup.members.size() > 0) {
            int x = 4 + anchorGroup[0];
            drawString(mc.fontRenderer,
                    ("Group: " + ((PlayerGroup.groupAcceptTpa) ? "/tpa " : "") + ((PlayerGroup.groupAcceptTpaHere) ? "/tpahere" : "")),
                    x, 38 + anchorGroup[1],
                    Integer.parseInt((PlayerGroup.groupAcceptTpa)
                            ? "FF0000"
                            : ((PlayerGroup.groupAcceptTpaHere)
                            ? "FFD800"
                            : "62A2C4"), 16));

            for (int i = 0; i < PlayerGroup.members.size(); i++) {
                String selfSelector = "> ";
                String mainDetector = " <";
                String popCount = "";
                if(TotemPopCounter.enabled) popCount += TotemPopCounter.getDisplay(PlayerGroup.members.get(i));

                
                if (mc.player.getDisplayNameString().equals(PlayerGroup.members.get(i))) {
                    drawString(mc.fontRenderer, selfSelector + PlayerGroup.members.get(i) + ((PlayerGroup.mainAccount.equals(PlayerGroup.members.get(i))) ? mainDetector : "") + popCount, x-1, (4 + 48 + (i * 10)) + anchorGroup[1],
                            Integer.parseInt((!PlayerGroup.isPlayerOnline(PlayerGroup.members.get(i))) ? "AAAAAA" : "FFFFFF", 16));
                }
                
                else {
                    String outOfRange = " ?";
                    if (Bodyguard.enabled) {
                        if (Bodyguard.nearbyGroup.contains(PlayerGroup.members.get(i))) outOfRange = "";
                    } else {
                        if (EntityRadar.nearbyGroupMembers().contains(PlayerGroup.members.get(i))) outOfRange = "";
                    }

                    drawString(mc.fontRenderer, "  " + PlayerGroup.members.get(i) + ((PlayerGroup.mainAccount.equals(PlayerGroup.members.get(i))) ? mainDetector : "") + outOfRange + popCount, x + 1, (4 + 48 + (i * 10)) + anchorGroup[1],
                            Integer.parseInt((!PlayerGroup.isPlayerOnline(PlayerGroup.members.get(i))) ? "AAAAAA" : "FFFFFF", 16));
                }

                groupListBottom = (4 + 48 + (i * 10)) + anchorGroup[1];
            }
        }

        
        if (GuiHandler.showTargetListing && !mc.isSingleplayer()) {
            
            int x = 4 + anchorPlayers[0];
            drawString(mc.fontRenderer,
                    "Hostile: " + Bodyguard.currentEnemies.size(),
                    x, groupListBottom + 13 + anchorPlayers[1],
                    Integer.parseInt((Bodyguard.currentEnemies.isEmpty()) ? "62A2C4" : "FF0000", 16));

            
            if (targetGuiActive) {
                for (int i = 0; i < availableTargets.size(); i++) {
                    String prefix = "  ", postFix = "";
                    int extraPixel = 2;
                    if (i == currentSelectedTargetIndex) {
                        prefix = "> ";
                        extraPixel = 1;
                    }

                    if (EntityRadar.getEntityPlayer(availableTargets.get(i)) == null) {
                        postFix = " ?";
                    }

                    drawString(mc.fontRenderer, prefix + availableTargets.get(i) + postFix,
                            extraPixel + 4 + anchorPlayers[0], (groupListBottom + 27 + (i * 10)) + anchorPlayers[1],
                            Integer.parseInt((i == currentSelectedTargetIndex) ? "FFFFFF" : ((Bodyguard.currentEnemies.contains(availableTargets.get(i))) ? "FF0000" : "AAAAAA"), 16));
                }
            }
            
            else {
                
                ArrayList<String> combined = new ArrayList<>(EntityRadar.nearbyPotentialEnemiesToBodyGuard());
                for (String enemy : Bodyguard.currentEnemies) {
                    if (!combined.contains(enemy)) combined.add(enemy);
                }
                for (int i = 0; i < combined.size(); i++) {
                    String postFix = "";
                    EntityPlayer ranged = EntityRadar.getEntityPlayer(combined.get(i));
                    if (ranged == null) {
                        postFix = " ?";
                    } else {
                        renderFaceTexture(4 + anchorPlayers[0], (groupListBottom + 27 + (i * 10)) + anchorPlayers[1], ((EntityOtherPlayerMP) ranged).getLocationSkin());
                    }

                    if(TotemPopCounter.enabled) postFix += TotemPopCounter.getDisplay(combined.get(i));

                    drawString(mc.fontRenderer, "  " + combined.get(i) + postFix,
                            9 + anchorPlayers[0], (groupListBottom + 27 + (i * 10)) + anchorPlayers[1],
                            Integer.parseInt((Bodyguard.currentEnemies.contains(combined.get(i))) ? "FF0000" : "AAAAAA", 16));
                }

            }
        }

        boolean survivalMode = mc.playerController.getCurrentGameType() != GameType.CREATIVE && mc.playerController.getCurrentGameType() != GameType.SPECTATOR;

        
        if (SafeTotemSwap.enabled() && GuiHandler.showSafetotem && survivalMode && SafeTotemSwap.totalCount > 0) {
            
            if(SafeTotemSwap.state == SafeTotemSwap.Mode.CLASSIC){
                final int ss = 20;
                int color = 0xFFFF0000;
                if (SafeTotemSwap.totemsReadyToSwitch) color = 0xFF00FF00; 
                BeveledBox.drawBeveledBox(width / 2 - (4 * ss) - (ss / 2), height - ss - 1, width / 2 - (3 * ss) - (ss / 2), height - 1, 2, color, color, 0x44B200FF);
            }
            
            if (SafeTotemSwap.totalCount - SafeTotemSwap.totalUselessCount == SafeTotemSwap.totalCount) {
                drawCenteredString(mc.fontRenderer, SafeTotemSwap.totalCount + "", (width / 2) - 109, height - 33, Integer.parseInt("FFFFFF", 16));

            } else {
                drawCenteredString(mc.fontRenderer, SafeTotemSwap.totalCount - SafeTotemSwap.totalUselessCount + "", (width / 2) - 109, height - 43, Integer.parseInt("FFFFFF", 16));
                drawCenteredString(mc.fontRenderer, "(" + SafeTotemSwap.totalCount + ")", (width / 2) - 109, height - 33, Integer.parseInt("FFFFFF", 16));
            }
        }


        
        if (GuiHandler.txtHpAndFood && survivalMode) {
            int food = mc.player.getFoodStats().getFoodLevel();
            float hp = mc.player.getHealth();

            drawString(mc.fontRenderer, hp + " hp", (width / 2) - 90 + anchorNumbers[0], height - 50 + anchorNumbers[1], Integer.parseInt("FFFFFF", 16));
            drawString(mc.fontRenderer, food + " food", (width / 2) - 90 + anchorNumbers[0], height - 39 + anchorNumbers[1], Integer.parseInt("FFFFFF", 16));
        }
    }


    
    
    
    
    
    
    
    
    
    
    
    public static void renderCustomTexture(int x, int y, int u, int v, int width, int height, ResourceLocation resourceLocation, float scale) {
        Minecraft mc = Minecraft.getMinecraft();
        
        

        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glColor4f(1, 1, 1, 1);
        GL11.glScalef(scale, scale, scale);

        if (resourceLocation != null) {
            mc.getTextureManager().bindTexture(resourceLocation);
        }

        mc.ingameGUI.drawTexturedModalRect(x, y, u, v, width, height);

        GL11.glPopMatrix();
    }

    public static void renderFaceTexture(int x, int y, ResourceLocation resourceLocation) {
        if (resourceLocation == null) return;
        Minecraft mc = Minecraft.getMinecraft();
        
        
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glColor4f(1, 1, 1, 1);
        mc.getTextureManager().bindTexture(resourceLocation);
        
        
        
        drawModalRectWithCustomSizedTexture(x, y, 8, 8, 8, 8, 64, 64);
        GL11.glPopMatrix();
    }

    
    public static void targetConfirm() {
        if (!allowTargetInput()) return;
        if (!targetGuiActive) return;
        if (availableTargets.isEmpty()) return;

        String targetName = availableTargets.get(currentSelectedTargetIndex);
        if (Bodyguard.currentEnemies.contains(targetName)) {
            
            Bodyguard.removeEnemy(targetName, true);
        } else {
            
            Bodyguard.addEnemy(targetName, true);
            lastTargetedEnemy = targetName;
            lastPopupMessage = System.currentTimeMillis();
        }
        
        targetGUIToggle();
    }

    public static void targetSelect(int direction) {
        if (!allowTargetInput()) return;
        if (!targetGuiActive) return;
        currentSelectedTargetIndex = currentSelectedTargetIndex + direction;
        if (currentSelectedTargetIndex < 0) {
            currentSelectedTargetIndex = availableTargets.size() - 1;
        } else if (currentSelectedTargetIndex > availableTargets.size() - 1) {
            currentSelectedTargetIndex = 0;
        }
    }

    public static void targetGUIToggle() {
        if (!allowTargetInput()) return;
        availableTargets.clear();
        currentSelectedTargetIndex = 0;
        targetGuiActive = !targetGuiActive;
        if (targetGuiActive) {
            
            availableTargets = new ArrayList<>(EntityRadar.nearbyPotentialEnemiesToBodyGuard());
            for (String enemy : Bodyguard.currentEnemies) {
                if (!availableTargets.contains(enemy)) availableTargets.add(enemy);
            }
        }
    }

    private static boolean allowTargetInput() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player == null) return false;
        if (mc.isSingleplayer()) return false;
        if (!GuiHandler.showTargetListing) return false;
        return true;
    }
}

