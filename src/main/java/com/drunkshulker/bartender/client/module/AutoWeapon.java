package com.drunkshulker.bartender.client.module;

import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentMending;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;

public class AutoWeapon {
    static void equipBestWeapon() {
        int bestSlot = -1;
        double maxDamage = 0.0;

        if(Aura.autoWeaponBySharpness){
            for (int i = 0; i<=8;i++) {
                ItemStack stack = Minecraft.getMinecraft().player.inventory.getStackInSlot(i);
                if (stack.isEmpty) continue;

                double damage = EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, stack);
                if (damage > maxDamage) {
                    maxDamage = damage;
                    bestSlot = i;
                }
            }
        } else {
            for (int i = 0; i<=8;i++) {
                ItemStack stack = Minecraft.getMinecraft().player.inventory.getStackInSlot(i);
                if (stack.isEmpty) continue;

                if (stack.getItem() instanceof ItemSword) {
                    double damage = ((ItemSword)stack.getItem()).getAttackDamage() +
                            EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, stack);
                    if (damage > maxDamage) {
                        maxDamage = damage;
                        bestSlot = i;
                    }
                }
                else if (stack.getItem() instanceof ItemTool) {
                    double damage = ((ItemTool)stack.getItem()).attackDamage +
                            EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, stack);
                    if (damage > maxDamage) {
                        maxDamage = damage;
                        bestSlot = i;
                    }
                }

            }
        }
        if (bestSlot != -1) {
            equip(bestSlot);
        }
    }

    public static void equip(int slot) {
        Minecraft.getMinecraft().player.inventory.currentItem = slot;
        
    }
}
