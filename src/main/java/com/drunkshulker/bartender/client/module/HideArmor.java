package com.drunkshulker.bartender.client.module;

import com.drunkshulker.bartender.client.gui.clickgui.ClickGuiSetting;
import com.drunkshulker.bartender.util.salhack.events.render.EventRenderArmorLayer;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listenable;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.entity.player.EntityPlayer;

public class HideArmor implements Listenable {
    public static boolean enabled = false;

    public static void applyPreferences(ClickGuiSetting[] contents) {
        for (ClickGuiSetting setting : contents) {
            if(setting.title.equals("hide armor")) {
                enabled = setting.value == 1;
            }
        }
    }

    @EventHandler
    private Listener<EventRenderArmorLayer> OnRenderArmorLayer = new Listener<>(p_Event ->
    {
        if (enabled)
        {
            if (!(p_Event.Entity instanceof EntityPlayer))
                return;

            p_Event.cancel();
        }
    });

}
