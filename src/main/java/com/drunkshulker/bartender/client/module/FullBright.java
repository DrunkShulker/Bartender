package com.drunkshulker.bartender.client.module;

import com.drunkshulker.bartender.client.gui.clickgui.ClickGuiSetting;
import com.drunkshulker.bartender.util.salhack.Timer;
import com.drunkshulker.bartender.util.salhack.events.network.EventNetworkPacketEvent;
import com.drunkshulker.bartender.util.salhack.events.player.EventPlayerUpdate;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listenable;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.client.Minecraft;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.server.SPacketEntityEffect;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public final class FullBright implements Listenable
{
    static public final Mode mode = Mode.Gamma;
    static public boolean enabled = true;
    static private Minecraft mc = Minecraft.getMinecraft();
    private enum Mode
    {
        Gamma,
        Potion,
        Table
    }

    static public final boolean effects = true;

    private static float lastGamma;

    private World world;
    private Timer timer = new Timer();

    public static void applyPreferences(ClickGuiSetting[] contents) {
        for (ClickGuiSetting setting : contents) {
            if(setting.title.equals("full bright")) {

                if(enabled&&setting.value==0){
                    onDisable();
                }
                if(!enabled&&setting.value==1){
                    onEnable();
                }
                enabled = setting.value == 1;
            }
        }
    }

    public static void onEnable()
    {
        if (mode == Mode.Gamma)
        {
            lastGamma = mc.gameSettings.gammaSetting;
        }
    }


    public static void onDisable()
    {

        if (mode== Mode.Gamma)
        {
            mc.gameSettings.gammaSetting = lastGamma;
        }

        if (mode == Mode.Potion && mc.player != null)
        {
            mc.player.removePotionEffect(MobEffects.NIGHT_VISION);
        }

        if (mode == Mode.Table)
        {
            if (mc.world != null)
            {
                float f = 0.0F;

                for (int i = 0; i <= 15; ++i)
                {
                    float f1 = 1.0F - (float) i / 15.0F;
                    mc.world.provider.getLightBrightnessTable()[i] = (1.0F - f1) / (f1 * 3.0F + 1.0F) * 1.0F + 0.0F;
                }
            }
        }
    }

    @EventHandler
    private Listener<EventPlayerUpdate> OnPlayerUpdate = new Listener<>(p_Event ->
    {
        if(!enabled) return;
        switch (mode)
        {
            case Gamma:
                

                mc.gameSettings.gammaSetting = 1000;
                mc.player.removePotionEffect(MobEffects.NIGHT_VISION);
                break;
            case Potion:
                mc.gameSettings.gammaSetting = 1.0f;
                mc.player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 5210));
                break;
            case Table:
                if (this.world != mc.world)
                {
                    if (mc.world != null)
                    {
                        for (int i = 0; i <= 15; ++i)
                        {
                            mc.world.provider.getLightBrightnessTable()[i] = 1.0f;
                        }
                    }
                    this.world = mc.world;
                }
                break;
        }
    });

    @EventHandler
    private Listener<EventNetworkPacketEvent> PacketEvent = new Listener<>(p_Event ->
    {
        if(!enabled) return;
        if (p_Event.GetPacket() instanceof SPacketEntityEffect)
        {
            if (this.effects)
            {
                final SPacketEntityEffect packet = (SPacketEntityEffect) p_Event.GetPacket();
                if (mc.player != null && packet.getEntityId() == mc.player.getEntityId())
                {
                    if (packet.getEffectId() == 9 || packet.getEffectId() == 15)
                    {
                        p_Event.cancel();
                    }
                }
            }
        }
    });

}