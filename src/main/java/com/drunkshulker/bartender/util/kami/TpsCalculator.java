package com.drunkshulker.bartender.util.kami;


import com.drunkshulker.bartender.util.salhack.events.network.EventNetworkPacketEvent;
import com.drunkshulker.bartender.util.salhack.events.player.EventPlayerJoin;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listenable;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.network.play.server.SPacketTimeUpdate;
import net.minecraft.util.math.MathHelper;

import java.util.*;

public class TpsCalculator implements Listenable {
    private static float[] tickRates = new float[100];
    private int index = 0;
    private long timeLastTimeUpdate = 0;

    static float tickRate() {
        float numTicks = 0.0f;
        float sumTickRates = 0.0f;
        for (float tickRate : tickRates) {
            if (tickRate > 0.0f) {
                sumTickRates += tickRate;
                numTicks += 1.0f;
            }
        }
        float calcTickRate = MathHelper.clamp(sumTickRates / numTicks, 0.0f, 20.0f);
        if (calcTickRate == 0.0f) return 20.0f;
        else return calcTickRate;
    }

    float adjustTicks() {
        return tickRate() - 20f;
    }


    @EventHandler
    private Listener<EventNetworkPacketEvent> PacketEvent = new Listener<>(it ->
    {
        if (!(it.getPacket() instanceof SPacketTimeUpdate)) return;
        if (timeLastTimeUpdate != -1L) {
            float timeElapsed = (System.currentTimeMillis() - timeLastTimeUpdate) / 1000.0f;
            tickRates[index] = MathHelper.clamp(20.0f / timeElapsed, 0.0f, 20.0f);
            index = (index + 1) % tickRates.length;
        }
        timeLastTimeUpdate = System.currentTimeMillis();
    });

    @EventHandler
    private Listener<EventPlayerJoin> Pa = new Listener<>(it ->
    {
        reset();
    });


    private void reset() {
        index = 0;
        timeLastTimeUpdate = -1L;
        Arrays.fill(tickRates, 0.0f);
    }

}