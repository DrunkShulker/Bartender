package com.drunkshulker.bartender.client.module;

import com.drunkshulker.bartender.client.gui.clickgui.ClickGuiSetting;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AutoWalk {

    static boolean enabled = false;

    public static void applyPreferences(ClickGuiSetting[] contents) {
        for (ClickGuiSetting setting : contents) {
            if (setting.title.equals("auto walk")) enabled = setting.value == 1;
        }
    }

    @SubscribeEvent
    public void playerTick(InputUpdateEvent event) {
        if (enabled
                && !BaseFinder.enabled
                && !Bodyguard.enabled) {
            event.getMovementInput().moveForward = 1f;
        }
    }
}