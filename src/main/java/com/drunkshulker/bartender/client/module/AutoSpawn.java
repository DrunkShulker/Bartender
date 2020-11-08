package com.drunkshulker.bartender.client.module;

import com.drunkshulker.bartender.Bartender;
import com.drunkshulker.bartender.util.kami.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDeadBush;
import net.minecraft.block.BlockSoulSand;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.GameType;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;

public class AutoSpawn{
    private static UseMode useMode = UseMode.SINGLE;
    private static boolean party = false;
    private static boolean partyWithers = true;
    private static EntityMode entityMode = EntityMode.WITHER;
    private static float placeRange = 3.5f;
    private static int delay =5000;
    private static boolean rotate = true;
    private static boolean debug = true;

    private static final Minecraft mc = Minecraft.getMinecraft();

    private enum UseMode {
        SINGLE, SPAM
    }

    private enum EntityMode {
        SNOW, IRON, WITHER
    }

    public  static boolean enabled = false;
        private static TickTimer timer = new TickTimer(TimerUtils.TimeUnit.TICKS);
        private static BlockPos placeTarget = null;
        private static boolean rotationPlaceableX = false;
        private static boolean rotationPlaceableZ = false;
        private static int bodySlot = -1;
        private static int headSlot = -1;
        private static boolean isSneaking = false;
        private static Stage buildStage = Stage.PRE;

    private enum Stage {
        PRE, BODY, HEAD, DELAY
    }

    static void onEnable() {
        enabled = true;
        if (mc.player == null) {
           onDisable();
        }
    }

    static void onDisable() {
        enabled = false;
        placeTarget = null;
        rotationPlaceableX = false;
        rotationPlaceableZ = false;
        bodySlot = -1;
        isSneaking = false;
        buildStage = Stage.PRE;
    }

    @SubscribeEvent
    public void onTick(InputUpdateEvent event) {
        if(!enabled||mc.player==null) return;

        switch (buildStage){
            case PRE :
            isSneaking = false;
            rotationPlaceableX = false;
            rotationPlaceableZ = false;

            

            if (!checkBlocksInHotbar()) {
                if (!party) {
                    if (debug) {
                        Bartender.msg("Missing blocks to perform spawn");
                    }
                    onDisable();
                }
                Bartender.msg("Missing blocks to perform spawn");
                return;
            }
            ArrayList<BlockPos> blockPosList = VectorUtils.getBlockPosInSphere(mc.player.getPositionVector(), placeRange);
            boolean noPositionInArea = true;

            for (BlockPos pos : blockPosList) {
                placeTarget = pos;
                if(placeTarget==null) {
                    Bartender.msg("Place target invalid!");
                    return;
                }
                if (testStructure()) {
                    noPositionInArea = false;
                    break;
                }
            }

            if (noPositionInArea) {
                if (useMode == UseMode.SINGLE) {
                    if (debug) Bartender.msg("Invalid position for spawn");
                    onDisable();
                    return;
                }
            }

            buildStage = Stage.BODY;
            break;

            case BODY :
                InventoryUtils.swapSlot(bodySlot);
                for (BlockPos pos : BodyParts.bodyBase) placeBlock(placeTarget.add(pos), rotate);
                if (entityMode == EntityMode.WITHER || entityMode== EntityMode.IRON) {
                    if (rotationPlaceableX) {
                    for (BlockPos pos : BodyParts.ArmsX) {
                        placeBlock(placeTarget.add(pos), rotate);
                    }
                    } else if (rotationPlaceableZ) {
                        for (BlockPos pos : BodyParts.ArmsZ) {
                             placeBlock(placeTarget.add(pos), rotate);
                        }
                    }
                }

                buildStage = Stage.HEAD;
                break;

            case HEAD :
                Bartender.INVENTORY_UTILS.swapSlot(headSlot);

                if (entityMode == EntityMode.IRON || entityMode == EntityMode.SNOW) {
                    for (BlockPos pos : BodyParts.head) placeBlock(placeTarget.add(pos), rotate);
                }

                if (entityMode == EntityMode.WITHER) {
                    if (rotationPlaceableX) {
                        for (BlockPos pos : BodyParts.headsX) {
                            placeBlock(placeTarget.add(pos), rotate);
                        }
                    } else if (rotationPlaceableZ) {
                        for (BlockPos pos : BodyParts.headsZ) {
                            placeBlock(placeTarget.add(pos), rotate);
                        }
                    }
                }

                if (isSneaking) {
                    mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                    isSneaking = false;
                }

                if (useMode == UseMode.SINGLE) onDisable();

                
                timer.reset();
                break;

            case  DELAY :
                if (timer.tick(delay)) buildStage = Stage.PRE;
                break;

            }
        }


        
        
       

private boolean checkBlocksInHotbar() {
        headSlot = -1;
        bodySlot = -1;
    for(int slotIndex = 0; slotIndex <= 8; slotIndex++) {
        ItemStack stack = mc.player.inventory.getStackInSlot(slotIndex);
        if(stack==null) continue;
        if (stack.isEmpty) continue;

        switch ((EntityMode)entityMode) {
        case SNOW :
        if (stack.getItem() instanceof ItemBlock) {
            Block block = ((ItemBlock)stack.getItem()).getBlock();
            if (block == Blocks.LIT_PUMPKIN || block == Blocks.PUMPKIN) {
            if (checkItemStackSize(stack, 1)) headSlot = slotIndex;
            }
            if (block == Blocks.SNOW) {
            if (checkItemStackSize(stack, 2)) bodySlot = slotIndex;
            }
        }break;

        case IRON :
        if (stack.getItem() instanceof ItemBlock) {
            Block block = ((ItemBlock)stack.getItem()).getBlock();
            if (block == Blocks.LIT_PUMPKIN || block == Blocks.PUMPKIN) {
            if (checkItemStackSize(stack, 1)) headSlot = slotIndex;
            }
            if (block == Blocks.IRON_BLOCK) {
            if (checkItemStackSize(stack, 4)) bodySlot = slotIndex;
            }
        }break;

        case WITHER :
            if (stack.getItem() instanceof ItemSkull && stack.getItemDamage() == 1) {
            if (checkItemStackSize(stack, 3)) headSlot = slotIndex;
            } else if (stack.getItem() instanceof ItemBlock) {
            Block block = ((ItemBlock)stack.getItem()).getBlock();
            if (block instanceof BlockSoulSand) {
            if (checkItemStackSize(stack, 4)) bodySlot = slotIndex;
            }
            }
        break;
        }
        }
        return bodySlot != -1 && headSlot != -1;
        }

private boolean checkItemStackSize(ItemStack stack, int target) {
        return mc.playerController.currentGameType== GameType.CREATIVE && stack.stackSize >= 1 || stack.stackSize >= target;
        }

private boolean testStructure(){
    if(placeTarget==null) return false;
        BlockPos it = new BlockPos(placeTarget);

        rotationPlaceableX = true;
        rotationPlaceableZ = true;

        
        Block block = mc.world.getBlockState(it).getBlock();
        if (block instanceof BlockTallGrass || block instanceof BlockDeadBush) return false;
        if (getPlaceableSide(it) == null) return false;

        for (BlockPos pos : BodyParts.bodyBase) {
        if (placingIsBlocked(it.add(pos))) return false;
        }

        if (entityMode == EntityMode.SNOW || entityMode == EntityMode.IRON) {
            for (BlockPos pos : BodyParts.head) {
                if (placingIsBlocked(it.add(pos))) return false;
            }
        }

        if (entityMode == EntityMode.IRON || entityMode == EntityMode.WITHER) {
            for (BlockPos pos : BodyParts.ArmsX) {
            if (placingIsBlocked(it.add(pos))) rotationPlaceableX = false;
            }
            for (BlockPos pos : BodyParts.ArmsZ) {
            if (placingIsBlocked(it.add(pos))) rotationPlaceableZ = false;
            }
        }

        if (entityMode == EntityMode.WITHER) {
            for (BlockPos pos : BodyParts.headsX) {
                if (placingIsBlocked(it.add(pos))) rotationPlaceableX = false;
            }
            for (BlockPos pos : BodyParts.headsZ) {
                if (placingIsBlocked(it.add(pos))) rotationPlaceableZ = false;
            }
        }

            return rotationPlaceableX || rotationPlaceableZ;
        }


private static class BodyParts {
        static BlockPos[] bodyBase = new BlockPos[]{
        new BlockPos(0, 1, 0),
                new BlockPos(0, 2, 0)};
    static BlockPos[] ArmsX = new BlockPos[]{
                new BlockPos(-1, 2, 0),
                new BlockPos(1, 2, 0)
    };
    static BlockPos[] ArmsZ = new BlockPos[]{
                new BlockPos(0, 2, -1),
                new BlockPos(0, 2, 1)
    };
    static BlockPos[] headsX = new BlockPos[]{
                new BlockPos(0, 3, 0),
                new BlockPos(-1, 3, 0),
                new BlockPos(1, 3, 0)
    };
    static BlockPos[] headsZ = new BlockPos[]{
                new BlockPos(0, 3, 0),
                new BlockPos(0, 3, -1),
                new BlockPos(0, 3, 1)
    };
    static BlockPos[] head = new BlockPos[]{
                new BlockPos(0, 3, 0)
    };
        }

    private boolean placingIsBlocked(BlockPos pos) {
        return !mc.world.isAirBlock(pos) || !mc.world.checkNoEntityCollision(new AxisAlignedBB(pos));
        }

    private void placeBlock(BlockPos pos, boolean rotate) {
            EnumFacing side = getPlaceableSide(pos);
            if(side==null) return;
            BlockPos neighbour = pos.offset(side);
            EnumFacing opposite = side.getOpposite();
            Vec3d hitVec = new Vec3d(neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
            Block neighbourBlock = mc.world.getBlockState(neighbour).getBlock();
            if (!isSneaking && (BlockUtils.blackList.contains(neighbourBlock) || BlockUtils.shulkerList.contains(neighbourBlock))) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
            isSneaking = true;
            }
            if (rotate) BlockUtils.faceVectorPacketInstant(hitVec);
            mc.playerController.processRightClickBlock(mc.player, mc.world, neighbour, opposite, hitVec, EnumHand.MAIN_HAND);
            mc.player.swingArm(EnumHand.MAIN_HAND);
        }

    private EnumFacing getPlaceableSide(BlockPos pos) {
        for (EnumFacing side : EnumFacing.values()) {
            BlockPos neighbour = pos.offset(side);
            if (!mc.world.getBlockState(neighbour).getBlock().canCollideCheck(mc.world.getBlockState(neighbour), false)) {
                continue;
            }
            IBlockState blockState = mc.world.getBlockState(neighbour);
            if (!blockState.getMaterial().isReplaceable() && !(blockState.getBlock() instanceof BlockTallGrass) && !(blockState.getBlock() instanceof BlockDeadBush)) {
                 return side;
            }
        }
        return null;
        }

    public static void clickAction(String action) {

        switch (action) {
            case "spawn wither":
                onEnable();
                break;

                default:
                    break;
            }
        }

}

