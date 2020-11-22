package com.drunkshulker.bartender.client.module;

import com.drunkshulker.bartender.client.gui.clickgui.ClickGuiSetting;

import com.drunkshulker.bartender.util.salhack.events.MinecraftEvent;
import com.drunkshulker.bartender.util.salhack.events.player.EventPlayerMotionUpdate;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listenable;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.client.Minecraft;

public final class Sprint implements Listenable {

    public static boolean enabled = false;
    public final Modes Mode =  Modes.Rage;
    Minecraft mc = Minecraft.getMinecraft();

    public static void applyPreferences(ClickGuiSetting[] contents) {
        for (ClickGuiSetting setting : contents) {
            if(setting.title.equals("sprint")) enabled = setting.value==1;
        }
    }

    enum Modes {
        Rage,
        Legit
    }

    public void onDisable() {
        if (mc.world != null) {
            mc.player.setSprinting(false);
        }
    }


    @EventHandler
    private Listener<EventPlayerMotionUpdate> OnPlayerUpdate = new Listener<>(p_Event ->
    {
        if(!enabled) return;
        if(mc.player.isCreative() || mc.player.isSpectator()) return;
        if (p_Event.getEra() != MinecraftEvent.Era.PRE) return;

        switch (this.Mode) {
            case Rage:
                if ((mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown() || mc.gameSettings.keyBindLeft.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown())
                        && !(mc.player.isSneaking()) && !(mc.player.collidedHorizontally) && !(mc.player.getFoodStats().getFoodLevel() <= 6f)) {
                    mc.player.setSprinting(true);
                }
                break;
            case Legit:
                if ((mc.gameSettings.keyBindForward.isKeyDown()) && !(mc.player.isSneaking()) && !(mc.player.isHandActive()) && !(mc.player.collidedHorizontally) && mc.currentScreen == null
                        && !(mc.player.getFoodStats().getFoodLevel() <= 6f)) {
                    mc.player.setSprinting(true);
                }
                break;
        }
    });

}