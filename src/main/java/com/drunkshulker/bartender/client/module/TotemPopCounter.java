package com.drunkshulker.bartender.client.module;

import com.drunkshulker.bartender.Bartender;
import com.drunkshulker.bartender.client.social.PlayerGroup;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.HashMap;
import java.util.Map;

public class TotemPopCounter {

    public static Map<String, PopData> popDataMap = new HashMap<>();
    private Minecraft mc = Bartender.MC;
    public static boolean enabled = true;

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if(mc.player == null || !enabled) return;
        if(Bodyguard.currentEnemies==null||Bodyguard.currentEnemies.isEmpty()){
            if(!popDataMap.isEmpty()) popDataMap.clear();
        }
        else {
            checkPlayer(mc.player.getDisplayNameString());
            for (String player:EntityRadar.nearbyPlayers()) {
                checkPlayer(player);
            }
        }
    }

    void checkPlayer(String player){
        if(PlayerGroup.members.contains(player)||Bodyguard.currentEnemies.contains(player)){
            if(mc.player.getDisplayNameString().equals(player)){
                processPlayer(mc.player);
            } else {
                processPlayer(EntityRadar.getEntityPlayer(player));
            }
        }
    }

    private void processPlayer(EntityPlayer entityPlayer) {
        if(entityPlayer==null) return;
        PopData popData;
        int totsInHand = 0;
        if(!entityPlayer.getHeldItemOffhand().isEmpty()&&entityPlayer.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING) totsInHand = entityPlayer.getHeldItemOffhand().getCount();
        if(popDataMap.containsKey(entityPlayer.getDisplayNameString())) popData = popDataMap.get(entityPlayer.getDisplayNameString());
        else {
            popData = new PopData(totsInHand);
            popDataMap.put(entityPlayer.getDisplayNameString(),popData);
        }
        int diff = popData.lastCount-totsInHand;
        if(diff>0){
            popData.pops += diff;
        } else if(diff<0){
            popData.swaps++;
        }

        popData.lastCount = totsInHand;
    }

    public static String getDisplay(String player){
        PopData popData = null;

        if(popDataMap.containsKey(player)) popData = popDataMap.get(player);
        if(popData==null) return "";

        return " | swaps: "+popData.swaps+" | pops: "+popData.pops;
    }
}

class PopData{
    int pops;
    int swaps;
    int lastCount;

    PopData(int startCount){
        this.pops = 0;
        this.swaps = 0;
        this.lastCount = startCount;
    }
}
