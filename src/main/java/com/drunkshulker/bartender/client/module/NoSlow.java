package com.drunkshulker.bartender.client.module;


import com.drunkshulker.bartender.client.gui.clickgui.ClickGuiSetting;
import com.drunkshulker.bartender.util.salhack.events.client.EventClientTick;
import com.drunkshulker.bartender.util.salhack.events.network.EventNetworkPacketEvent;
import com.drunkshulker.bartender.util.salhack.events.network.EventNetworkPostPacketEvent;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listenable;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.*;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerDigging.Action;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class NoSlow implements Listenable {
    public static boolean enabled = false;
    private boolean ncpStrict = true;
    private boolean sneak = true;
    public static boolean soulSand = true;
    public static boolean cobweb = true;
    private boolean slime = true;
    private boolean allItems = false;
    private boolean food = true;
    static Minecraft mc = Minecraft.getMinecraft();
    private boolean bow = true;
    private boolean potion = true;
    private boolean shield = true;

    @EventHandler
    public Listener<InputUpdateEvent> onInput = new Listener<>(it ->
    {
        if (!enabled) return;
        if ((passItemCheck(mc.player.getActiveItemStack().getItem()) || (mc.player.isSneaking() && sneak)) && !mc.player.isRiding()) {
            it.getMovementInput().moveStrafe *= 5f;
            it.getMovementInput().moveForward *= 5f;
        }
    });


    @EventHandler
    private Listener<EventNetworkPostPacketEvent> PacketEvent = new Listener<>(event ->
    {
        if (!enabled) return;
        if (ncpStrict && event.GetPacket() instanceof CPacketPlayer &&
                passItemCheck(mc.player.getActiveItemStack().getItem()) && !mc.player.isRiding()) {
            mc.player.connection.sendPacket(new CPacketPlayerDigging(Action.ABORT_DESTROY_BLOCK, mcPlayerPosFloored(mc), EnumFacing.DOWN));
        }
    });

    static BlockPos mcPlayerPosFloored(Minecraft mc) {
        return new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ));
    }

    public static void applyPreferences(ClickGuiSetting[] contents) {
        for (ClickGuiSetting setting : contents) {
            if (setting.title.equals("no slow")) {
                enabled = setting.value == 1;
                if (!enabled) onDisable();
            }
        }
    }

    @EventHandler
    public Listener<EventClientTick> OnTick = new Listener<>(p_Event ->
    {
        if (!enabled) return;
        if (slime) Blocks.SLIME_BLOCK.setDefaultSlipperiness(0.4945f); 
        else Blocks.SLIME_BLOCK.setDefaultSlipperiness(0.8f);
    });

    static void onDisable() {
        Blocks.SLIME_BLOCK.setDefaultSlipperiness(0.8f);
    }

    private boolean passItemCheck(Item item) {
        if (!mc.player.isHandActive()) return false;
        else return allItems
                || item instanceof ItemFood && food
                || item instanceof ItemBow && bow
                || item instanceof ItemPotion && potion
                || item instanceof ItemShield && shield;
    }
}
