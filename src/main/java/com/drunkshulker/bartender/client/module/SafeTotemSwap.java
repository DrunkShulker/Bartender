package com.drunkshulker.bartender.client.module;

import java.util.ArrayList;

import com.drunkshulker.bartender.Bartender;
import com.drunkshulker.bartender.client.gui.clickgui.ClickGuiSetting;

import com.drunkshulker.bartender.util.kami.InventoryUtils;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.GameType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class SafeTotemSwap {

    static int swapOn = 8;
    static int stackSize = 64;
    static int nearDeathCount = 8;
    public static Mode state = Mode.CLASSIC;
    public static int totalCount;
    public static boolean runningLowOnStacks = false;
    public static boolean totemsReadyToSwitch = false;
    final static int ALLOWED_MISS_CALC_IN_STACK = 9;
    final public static int FIRST_HOTBAR_SLOT = 0;
    final static int OFFHAND_SLOT = 45;
    public static boolean taskInProgress = false;
    static int operationIntervalMillis = 1500;
    static long lastSwapStamp = operationIntervalMillis;
    public static NDBs NDB;
    public static int totalUselessCount = 0;
    private static int slotBeforeSwap = FIRST_HOTBAR_SLOT;
    private static boolean backToSlotNeeded = false;
    private static boolean rememberSlot = false;
    final static int totID = Item.getIdFromItem(Items.TOTEM_OF_UNDYING);
    private static boolean forceSwapNow = false;

    public static void clickAction(String title) {
        if (title.equals("swap now")) {
            if (state == Mode.CLASSIC) {
                forceSwapNow = true;
            } else {
                Bartender.msg("Manual swap only works with the CLASSIC safe totem mode!");
            }
        }
    }

    public enum Mode {
        OFF,
        CLASSIC,
        YAC
    }

    enum NDBs {
        NONE,
        DISCONNECT,
        SLASH_KILL
    }

    public static boolean enabled() {
        return state != Mode.OFF;
    }

    
    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (Bartender.MC.player == null || Bartender.MC.player.isDead) return;
        if (!enabled()
                || Bartender.MC.playerController.getCurrentGameType() == GameType.CREATIVE
                || Bartender.MC.playerController.getCurrentGameType() == GameType.SPECTATOR
                || Bartender.MC.isSingleplayer()) return;

        if (state == Mode.CLASSIC) {
            
            taskInProgress = InventoryUtils.inProgress || System.currentTimeMillis() - lastSwapStamp < operationIntervalMillis;

            
            if (backToSlotNeeded && rememberSlot && Bartender.MC.player.getHeldItemOffhand().getCount() > swapOn) {
                backToSlotNeeded = false;
                equipItem(slotBeforeSwap);
            }

            try {

                
                totalCount = Bartender.MC.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.TOTEM_OF_UNDYING).mapToInt(ItemStack::getCount).sum();
                if (Item.getIdFromItem(Bartender.MC.player.getHeldItemOffhand().getItem()) == Item.getIdFromItem(Items.TOTEM_OF_UNDYING)) {
                    totalCount += Bartender.MC.player.getHeldItemOffhand().getCount();
                }
                
                updateUselessTotCount();

                
                
                if (totalCount - totalUselessCount <= nearDeathCount && totalCount > 0) {
                    
                    if (Bartender.MC.player.getHealth() == 20) {
                        
                        Bartender.msg("Full HP! NDB canceled.");
                    } else {
                        
                        Bartender.msg("NDB!");
                        if (NDB == NDBs.DISCONNECT) {
                            BaseFinder.logOut("Safe totem NDB");
                        } else if (NDB == NDBs.SLASH_KILL) {
                            Bodyguard.commitSuicide();
                        }
                    }
                }

                
                totemsReadyToSwitch = checkHotbarTots();

                if (Bartender.MC.player.getHeldItemOffhand().isEmpty()
                        || (Bartender.MC.player.getHeldItemOffhand().getCount() <= swapOn && !runningLowOnStacks)
                        || Bartender.MC.player.getHeldItemOffhand().getItem() != Items.TOTEM_OF_UNDYING
                        || forceSwapNow) {
                    if (taskInProgress) return;
                    lastSwapStamp = System.currentTimeMillis();

                    if (!totemsReadyToSwitch) prepareSwap();
                    else swap();
                } else if (!totemsReadyToSwitch) {
                    if (taskInProgress) return;
                    lastSwapStamp = System.currentTimeMillis();
                    prepareSwap();
                }
            } catch (Exception e) {
            } 

        } else if (state == Mode.YAC) {

            try {
                if (Bartender.MC.player == null) return;

                
                totalCount = Bartender.MC.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.TOTEM_OF_UNDYING).mapToInt(ItemStack::getCount).sum();
                if (Item.getIdFromItem(Bartender.MC.player.getHeldItemOffhand().getItem()) == Item.getIdFromItem(Items.TOTEM_OF_UNDYING)) {
                    totalCount += Bartender.MC.player.getHeldItemOffhand().getCount();
                }
                
                updateUselessTotCount();

                
                
                if (totalCount - totalUselessCount <= nearDeathCount && totalCount > 0) {
                    
                    if (Bartender.MC.player.getHealth() == 20) {
                        
                        Bartender.msg("Full HP! NDB canceled.");
                    } else {
                        
                        Bartender.msg("NDB!");
                        if (NDB == NDBs.DISCONNECT) {
                            BaseFinder.logOut("Safe totem NDB");
                        } else if (NDB == NDBs.SLASH_KILL) {
                            Bodyguard.commitSuicide();
                        }
                    }
                }
            } catch (Exception e) {
            }

            taskInProgress = InventoryUtils.inProgress || System.currentTimeMillis() - lastSwapStamp < 400 + operationIntervalMillis;
            ItemStack stack = Bartender.MC.player.getHeldItemOffhand();
            if (stack.getCount() <= swapOn) {
                int slotToSwap = yacPrepareSwap();
                if (slotToSwap >= 0) {
                    InventoryUtils.yacInventorySwap(slotToSwap, OFFHAND_SLOT);
                }
            }
        }
    }

    public static void closeGUIs() {
        if (Bartender.MC.currentScreen != null) {
            Bartender.MC.player.closeScreenAndDropStack();
        }
    }

    private void updateUselessTotCount() {
        
        ArrayList<Integer> slotsWithTots = InventoryUtils.getSlots(9, 45, totID);
        int uselessStackCout = 0;
        for (Integer slot : slotsWithTots) {
            if (slot == FIRST_HOTBAR_SLOT) continue;
            if (slot == OFFHAND_SLOT) continue;
            int stackCount = InventoryUtils.countItem(slot, slot, totID);
            if (stackCount <= swapOn) {
                uselessStackCout += stackCount;
            }
        }
        totalUselessCount = uselessStackCout;
    }

    private int yacPrepareSwap() {
        ArrayList<Integer> slotsWithTots = InventoryUtils.getSlots(9, 45, totID);
        int bringToHotbar = -1;
        if (slotsWithTots == null) {
            Bartender.msg("YAC safe totem prepare swap failure!");
            return -1;
        }
        for (Integer slot : slotsWithTots) {
            if (slot == null || slot == OFFHAND_SLOT) continue;
            if (slot == null || slot == FIRST_HOTBAR_SLOT) continue;
            
            if (InventoryUtils.countItem(slot, slot, totID) >= stackSize - ALLOWED_MISS_CALC_IN_STACK) {
                bringToHotbar = slot;
                break;
            }
        }
        
        if (bringToHotbar == -1) {
            runningLowOnStacks = true;
            
            int slotWithMostTots = -1;
            int uselessStackCout = 0;
            for (Integer slot : slotsWithTots) {
                if (slot == null || slot == OFFHAND_SLOT) continue;
                if (slot == null || slot == FIRST_HOTBAR_SLOT) continue;
                int stackCount = InventoryUtils.countItem(slot, slot, totID);
                if (stackCount <= swapOn) {
                    uselessStackCout += stackCount;
                }
                if (stackCount > slotWithMostTots) {
                    slotWithMostTots = slot;
                }
            }
            bringToHotbar = slotWithMostTots;
            totalUselessCount = uselessStackCout;
        } else runningLowOnStacks = false;

        if (bringToHotbar == -1) {
            Bartender.msg("YAC safe totem could not find tots while preparing next swap!");
        }
        return bringToHotbar;
    }

    private void prepareSwap() {
        
        ArrayList<Integer> slotsWithTots = InventoryUtils.getSlots(9, 45, totID);
        int bringToHotbar = -1;
        for (Integer slot : slotsWithTots) {
            if (slot == FIRST_HOTBAR_SLOT) continue;
            if (slot == OFFHAND_SLOT) continue;
            
            if (InventoryUtils.countItem(slot, slot, totID) >= stackSize - ALLOWED_MISS_CALC_IN_STACK) {
                bringToHotbar = slot;
                break;
            }
        }
        
        if (bringToHotbar == -1) {
            runningLowOnStacks = true;
            
            int slotWithMostTots = -1;
            int uselessStackCout = 0;
            for (Integer slot : slotsWithTots) {
                if (slot == FIRST_HOTBAR_SLOT) continue;
                if (slot == OFFHAND_SLOT) continue;
                int stackCount = InventoryUtils.countItem(slot, slot, totID);
                if (stackCount <= swapOn) {
                    uselessStackCout += stackCount;
                }
                if (stackCount > slotWithMostTots) {
                    slotWithMostTots = slot;
                }
            }
            bringToHotbar = slotWithMostTots;
            totalUselessCount = uselessStackCout;
        } else runningLowOnStacks = false;

        if (bringToHotbar == -1) {
            Bartender.msg("Safe totem could not find tots while preparing next swap!");
            return;
        }
        closeGUIs();
        
        Bartender.INVENTORY_UTILS.quickTotem(bringToHotbar, operationIntervalMillis / 2);
    }

    public static void swap() {
        closeGUIs();
        if (rememberSlot) {
            int currentHeldItemSlot = Bartender.MC.player.inventory.currentItem;
            if (FIRST_HOTBAR_SLOT != currentHeldItemSlot) {
                slotBeforeSwap = currentHeldItemSlot;
            }
            backToSlotNeeded = true;
        }
        forceSwapNow = false;
        equipItem(FIRST_HOTBAR_SLOT);
        KeyBinding.onTick(Bartender.MC.gameSettings.keyBindSwapHands.getKeyCode());
    }

    
    public static boolean checkHotbarTots() {
        if (InventoryUtils.countItem(FIRST_HOTBAR_SLOT, FIRST_HOTBAR_SLOT, Item.getIdFromItem(Items.TOTEM_OF_UNDYING)) > swapOn) {
            return true;
        }
        return false;
    }

    public static void equipItem(int slot) {
        Bartender.MC.player.inventory.currentItem = slot;
    }

    public static void applyPreferences(ClickGuiSetting[] contents) {
        for (ClickGuiSetting setting : contents) {
            switch (setting.title) {
                case "state":
                    if (setting.value == 0) {
                        state = Mode.OFF;
                    } else if (setting.value == 1) {
                        state = Mode.CLASSIC;
                    } else if (setting.value == 2) {
                        state = Mode.YAC;
                    }
                    break;
                case "stacksize":
                    stackSize = Integer.parseInt(setting.values.get(setting.value).getAsString());
                    break;
                case "interval":
                    operationIntervalMillis = Integer.parseInt(setting.values.get(setting.value).getAsString());
                    break;
                case "swap at":
                    swapOn = setting.value + 1;
                    break;
                case "NDB":
                    if (setting.value == 0) NDB = SafeTotemSwap.NDBs.NONE;
                    else if (setting.value == 1) NDB = SafeTotemSwap.NDBs.DISCONNECT;
                    else if (setting.value == 2) NDB = SafeTotemSwap.NDBs.SLASH_KILL;
                    break;
                case "NDB count":
                    nearDeathCount = setting.value + 1;
                    break;
                case "remember slot":
                    rememberSlot = setting.value == 0;
                    break;
                default:
                    break;
            }
        }
    }

}
