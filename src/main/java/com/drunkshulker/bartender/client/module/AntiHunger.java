package com.drunkshulker.bartender.client.module;

import com.drunkshulker.bartender.client.gui.clickgui.ClickGuiSetting;
import com.drunkshulker.bartender.util.salhack.events.network.EventNetworkPacketEvent;
import me.zero.alpine.fork.listener.Listenable;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;

import static net.minecraft.network.play.client.CPacketEntityAction.Action.START_SPRINTING;
import static net.minecraft.network.play.client.CPacketEntityAction.Action.STOP_SPRINTING;


import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;

public final class AntiHunger implements Listenable {
    public final boolean Sprint = false;
    public final boolean Ground = true;
    private Minecraft mc = Minecraft.getMinecraft();
    public static boolean enabled = false;

    public static void applyPreferences(ClickGuiSetting[] contents) {
        for (ClickGuiSetting setting : contents) {
            if(setting.title.equals("anti hunger")) {
                enabled = setting.value == 1;
            }
        }
    }


    @EventHandler
    private Listener<EventNetworkPacketEvent> PacketEvent = new Listener<>(p_Event ->
    {
        if(!enabled) return;
        if (p_Event.getPacket() instanceof CPacketPlayer && Ground && !mc.player.isElytraFlying()) {
            final CPacketPlayer l_Packet = (CPacketPlayer) p_Event.getPacket();
            if (mc.player.fallDistance > 0 || mc.playerController.isHittingBlock) {
                l_Packet.onGround = true;
            } else {
                l_Packet.onGround = false;
            }
        }

        if (p_Event.getPacket() instanceof CPacketEntityAction && Sprint) {
            final CPacketEntityAction l_Packet = (CPacketEntityAction) p_Event.getPacket();
            if (l_Packet.getAction() == START_SPRINTING || l_Packet.getAction() == STOP_SPRINTING) {
                p_Event.cancel();
            }
        }
    });

}
