package com.drunkshulker.bartender.client.module;

import com.drunkshulker.bartender.client.gui.clickgui.ClickGuiSetting;

import me.zero.alpine.fork.listener.Listenable;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.world.storage.MapData;

public final class PeekPreview implements Listenable
{
    private static Minecraft mc = Minecraft.getMinecraft();
    public static boolean enabled = true;

    public static void applyPreferences(ClickGuiSetting[] contents) {
        for (ClickGuiSetting setting : contents) {
            if (setting.title.equals("peek preview")) enabled = setting.value == 1;
        }
    }

    public static MapData getMapData(ItemStack itemStack) {
        return ((ItemMap)itemStack.getItem()).getMapData(itemStack, mc.world);
    }
}