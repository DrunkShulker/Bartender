package com.drunkshulker.bartender.client.module;


import com.drunkshulker.bartender.client.gui.clickgui.ClickGuiSetting;
import com.drunkshulker.bartender.util.salhack.Timer;
import com.drunkshulker.bartender.util.salhack.events.network.EventNetworkPacketEvent;
import com.drunkshulker.bartender.util.salhack.events.player.EventPlayerDamageBlock;
import com.drunkshulker.bartender.util.salhack.events.player.EventPlayerUpdate;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listenable;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Enchantments;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import java.nio.charset.MalformedInputException;

public final class AutoTool implements Listenable {

    public final boolean silent = true;
    public final boolean GoBack = true;

    private boolean send;
    public BlockPos position;
    public EnumFacing facing;
    private int _previousSlot = -1;
    private Timer _timer = new Timer();
    private Minecraft mc = Minecraft.getMinecraft();
    public static boolean enabled = false;

    public static void applyPreferences(ClickGuiSetting[] contents) {
        for (ClickGuiSetting setting : contents) {
            if(setting.title.equals("auto tool")) {
                enabled = setting.value == 1;
            }
        }
    }

    private float blockStrength(BlockPos pos, ItemStack stack) {
        float hardness = mc.world.getBlockState(pos).getBlockHardness(mc.world, pos);

        if (hardness < 0.0F) {
            return 0.0F;
        }

        if (!canHarvestBlock(mc.world.getBlockState(pos).getBlock(), pos, stack)) {
            return getDigSpeed(mc.world.getBlockState(pos), pos, stack) / hardness / 100F;
        } else {
            return getDigSpeed(mc.world.getBlockState(pos), pos, stack) / hardness / 30F;
        }
    }

    private boolean canHarvestBlock(Block block, BlockPos pos, ItemStack stack) {
        IBlockState state = mc.world.getBlockState(pos);
        state = state.getBlock().getActualState(state, mc.world, pos);

        if (state.getMaterial().isToolNotRequired()) {
            return true;
        }

        String tool = block.getHarvestTool(state);

        if (stack.isEmpty() || tool == null) {
            return mc.player.canHarvestBlock(state);
        }

        final int toolLevel = stack.getItem().getHarvestLevel(stack, tool, mc.player, state);

        if (toolLevel < 0) {
            return mc.player.canHarvestBlock(state);
        }

        return toolLevel >= block.getHarvestLevel(state);
    }

    private float getDestroySpeed(IBlockState state, ItemStack stack) {
        float f = 1.0F;

        f *= stack.getDestroySpeed(state);

        return f;
    }

    private float getDigSpeed(IBlockState state, BlockPos pos, ItemStack stack) {
        float f = getDestroySpeed(state, stack);

        if (f > 1.0F) {
            int i = EnchantmentHelper.getEfficiencyModifier(mc.player);

            if (i > 0 && !stack.isEmpty()) {
                f += (float) (i * i + 1);
            }
        }

        if (mc.player.isPotionActive(MobEffects.HASTE)) {
            f *= 1.0F + (float) (mc.player.getActivePotionEffect(MobEffects.HASTE).getAmplifier() + 1) * 0.2F;
        }

        if (mc.player.isPotionActive(MobEffects.MINING_FATIGUE)) {
            float f1;

            switch (mc.player.getActivePotionEffect(MobEffects.MINING_FATIGUE).getAmplifier()) {
                case 0:
                    f1 = 0.3F;
                    break;
                case 1:
                    f1 = 0.09F;
                    break;
                case 2:
                    f1 = 0.0027F;
                    break;
                case 3:
                default:
                    f1 = 8.1E-4F;
            }

            f *= f1;
        }

        if (mc.player.isInsideOfMaterial(Material.WATER) && !EnchantmentHelper.getAquaAffinityModifier(mc.player)) {
            f /= 5.0F;
        }

        if (!mc.player.onGround) {
            f /= 5.0F;
        }

        f = net.minecraftforge.event.ForgeEventFactory.getBreakSpeed(mc.player, state, f, pos);
        return (f < 0 ? 0 : f);
    }

    @EventHandler
    private Listener<EventPlayerDamageBlock> DamageBlock = new Listener<>(p_Event ->
    {
        if(!enabled) return;
        if (this.silent) {
            final int slot = getToolInventory(p_Event.getPos());
            if (slot != -1) {
                mc.playerController.curBlockDamageMP += blockStrength(p_Event.getPos(), mc.player.inventoryContainer.getSlot(slot).getStack());
            } else {
                final int hotbar = getToolHotbar(p_Event.getPos());
                if (hotbar != -1) {
                    mc.playerController.curBlockDamageMP += blockStrength(p_Event.getPos(), mc.player.inventory.getStackInSlot(hotbar));
                }
            }
        } else {
            _timer.reset();
            final int slot = getToolHotbar(p_Event.getPos());
            if (slot != -1 && mc.player.inventory.currentItem != slot) {
                if (_previousSlot != slot)
                    _previousSlot = mc.player.inventory.currentItem;

                mc.player.inventory.currentItem = slot;
                mc.playerController.updateController();
            }
        }
    });

    @EventHandler
    private Listener<EventPlayerUpdate> PlayerUpdate = new Listener<>(event ->
    {
        if(!enabled) return;
        if (_previousSlot != -1 && _timer.passed(250) && !mc.playerController.isHittingBlock) {
            _timer.reset();
            if (GoBack) {
                mc.player.inventory.currentItem = _previousSlot;
                mc.playerController.updateController();
            }
            _previousSlot = -1;
        }
    });

    @EventHandler
    private Listener<EventNetworkPacketEvent> PacketEvent = new Listener<>(p_Event ->
    {
        if(!enabled) return;
        if (this.send) {
            this.send = false;
            return;
        }
        if (p_Event.getPacket() instanceof CPacketPlayerDigging && this.silent) {
            final CPacketPlayerDigging packet = (CPacketPlayerDigging) p_Event.getPacket();
            if (packet.getAction() == CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK) {
                this.position = packet.getPosition();
                this.facing = packet.getFacing();

                if (this.position != null && this.facing != null) {
                    final int slot = getToolInventory(packet.getPosition());
                    if (slot != -1) {
                        p_Event.cancel();
                        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, slot,
                                mc.player.inventory.currentItem, ClickType.SWAP, mc.player);
                        send = true;
                        mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, this.position, this.facing));
                        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, slot,
                                mc.player.inventory.currentItem, ClickType.SWAP, mc.player);
                    } else {
                        final int hotbar = getToolHotbar(packet.getPosition());
                        if (hotbar != -1) {
                            p_Event.cancel();
                            mc.playerController.windowClick(mc.player.inventoryContainer.windowId, hotbar,
                                    mc.player.inventory.currentItem, ClickType.SWAP, mc.player);
                            send = true;
                            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, this.position, this.facing));
                            mc.playerController.windowClick(mc.player.inventoryContainer.windowId, hotbar,
                                    mc.player.inventory.currentItem, ClickType.SWAP, mc.player);
                        }
                    }
                }
            }
        }
    });

    private int getToolInventory(BlockPos pos) {
        int index = -1;

        float speed = 1.0f;

        for (int i = 9; i < 36; i++) {
            final ItemStack stack = mc.player.inventoryContainer.getSlot(i).getStack();
            if (stack != null && stack != ItemStack.EMPTY) {
                final float digSpeed = EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, stack);
                final float destroySpeed = stack.getDestroySpeed(mc.world.getBlockState(pos));

                if ((digSpeed + destroySpeed) > speed) {
                    speed = (digSpeed + destroySpeed);
                    index = i;
                }
            }
        }

        return index;
    }

    private int getToolHotbar(BlockPos pos) {
        int index = -1;

        float speed = 1.0f;

        for (int i = 0; i <= 9; i++) {
            final ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack != null && stack != ItemStack.EMPTY) {
                final float digSpeed = EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, stack);
                final float destroySpeed = stack.getDestroySpeed(mc.world.getBlockState(pos));

                if ((digSpeed + destroySpeed) > speed) {
                    speed = (digSpeed + destroySpeed);
                    index = i;
                }
            }
        }

        return index;
    }

}
