package com.drunkshulker.bartender.util.kami;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;


public class GlStateUtils {

    static boolean colorLock = false;
    static boolean useVbo() {
        return Minecraft.getMinecraft().gameSettings.useVbo;
    }

    static void alpha(boolean state) {
        if (state) {
            GlStateManager.enableAlpha();
        } else {
            GlStateManager.disableAlpha();
        }
    }


    public static void blend(boolean state) {
        if (state) {
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);
        } else {
            GlStateManager.disableBlend();
        }
    }

    static void smooth(boolean state) {
        if (state) {
            GlStateManager.shadeModel(GL_SMOOTH);
        } else {
            GlStateManager.shadeModel(GL_FLAT);
        }
    }

    static void lineSmooth(boolean state) {
        if (state) {
            glEnable(GL_LINE_SMOOTH);
            glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        } else {
            glDisable(GL_LINE_SMOOTH);
        }
    }

    public static void depth(boolean state) {
        if (state) {
            GlStateManager.enableDepth();
        } else {
            GlStateManager.disableDepth();
        }
    }

    static void texture2d(boolean state) {
        if (state) {
            GlStateManager.enableTexture2D();
        } else {
            GlStateManager.disableTexture2D();
        }
    }

    static void cull(boolean state) {
        if (state) {
            GlStateManager.enableCull();
        } else {
            GlStateManager.disableCull();
        }
    }

    static void  lighting(boolean state) {
        if (state) {
            GlStateManager.enableLighting();
        } else {
            GlStateManager.disableLighting();
        }
    }

    static void  colorLock(boolean state) {
        colorLock = state;
    }

    static void resetTexParam() {
        GlStateManager.bindTexture(0);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAX_LEVEL, 1000);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAX_LOD, 1000);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_LOD, -1000);
    }

    static void  rescaleActual() {
        rescale(Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
    }


    static void rescaleMc() {
        ScaledResolution resolution = new ScaledResolution(Minecraft.getMinecraft());
        rescale(resolution.getScaledWidth_double(), resolution.getScaledHeight_double());
    }

    static void  rescale(double width, double height) {
        glClear(256);
        glMatrixMode(5889);
        glLoadIdentity();
        glOrtho(0.0, width, height, 0.0, 1000.0, 3000.0);
        glMatrixMode(5888);
        glLoadIdentity();
        glTranslated(0.0, 0.0, -2000.0);
    }
}