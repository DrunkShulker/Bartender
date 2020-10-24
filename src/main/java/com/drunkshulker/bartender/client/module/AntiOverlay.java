package com.drunkshulker.bartender.client.module;

import com.drunkshulker.bartender.client.gui.clickgui.ClickGuiSetting;

import com.drunkshulker.bartender.util.salhack.events.render.EventRenderBossHealth;
import com.drunkshulker.bartender.util.salhack.events.render.EventRenderUpdateLightmap;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listenable;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.client.Minecraft;
import net.minecraft.init.MobEffects;

import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.awt.image.renderable.RenderableImageOp;

public class AntiOverlay implements Listenable {

    public static boolean enabled = false;
    private Minecraft mc = Minecraft.getMinecraft();
    private boolean fire = true;
    private boolean water = true;
    private boolean blocks = true;
    private boolean portals = true;
    private boolean blindness = true;
    private boolean nausea = true;
    public static boolean totems = true;
    private boolean vignette = true;
    private boolean helmet = true;

    @EventHandler
    Listener<RenderBlockOverlayEvent> renderBlockOverlayEventListener = new Listener<>(event ->
    {
        if(!enabled) return;
        switch (event.getOverlayType()) {
            case FIRE:
                event.setCanceled(fire);
                break;
            case WATER:
                event.setCanceled(water);
                break;
            case BLOCK:
                event.setCanceled(blocks);
                break;
            default:
                event.setCanceled(false);
                break;
        }

    });

    @EventHandler
    Listener<RenderGameOverlayEvent> renderGameOverlayEventListener = new Listener<>(event ->
    {
        if(!enabled) return;
        switch (event.getType()) {
            case VIGNETTE:
                event.setCanceled(vignette);
                break;
            case PORTAL:
                event.setCanceled(portals);
                break;
            case HELMET:
                event.setCanceled(helmet);
                break;
            default:
                event.setCanceled(false);
        }

    });


    public static void applyPreferences(ClickGuiSetting[] contents) {
        for (ClickGuiSetting setting : contents) {
            if(setting.title.equals("anti overlay")) enabled = setting.value==1;
        }
    }

    @EventHandler
    private Listener<EventRenderBossHealth> OnRenderBossHealth = new Listener<>(p_Event ->
    {
        if (enabled)
            p_Event.cancel();
    });

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if(!enabled) return;
        if (blindness) mc.player.removeActivePotionEffect(MobEffects.BLINDNESS);
        if (nausea) mc.player.removeActivePotionEffect(MobEffects.NAUSEA);
    }
}