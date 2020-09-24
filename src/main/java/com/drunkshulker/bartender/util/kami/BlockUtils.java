package com.drunkshulker.bartender.util.kami;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;


public class BlockUtils {

    public static Block[] blackList = new Block[]{
            Blocks.ENDER_CHEST,
            Blocks.CHEST,
            Blocks.TRAPPED_CHEST,
            Blocks.CRAFTING_TABLE,
            Blocks.ANVIL,
            Blocks.BREWING_STAND,
            Blocks.HOPPER,
            Blocks.DROPPER,
            Blocks.DISPENSER,
            Blocks.TRAPDOOR,
            Blocks.ENCHANTING_TABLE
    };

    public static Block[] shulkerList = new Block[]{
            Blocks.WHITE_SHULKER_BOX,
            Blocks.ORANGE_SHULKER_BOX,
            Blocks.MAGENTA_SHULKER_BOX,
            Blocks.LIGHT_BLUE_SHULKER_BOX,
            Blocks.YELLOW_SHULKER_BOX,
            Blocks.LIME_SHULKER_BOX,
            Blocks.PINK_SHULKER_BOX,
            Blocks.GRAY_SHULKER_BOX,
            Blocks.SILVER_SHULKER_BOX,
            Blocks.CYAN_SHULKER_BOX,
            Blocks.PURPLE_SHULKER_BOX,
            Blocks.BLUE_SHULKER_BOX,
            Blocks.BROWN_SHULKER_BOX,
            Blocks.GREEN_SHULKER_BOX,
            Blocks.RED_SHULKER_BOX,
            Blocks.BLACK_SHULKER_BOX
    };


    public static BlockPos[] surroundOffset = new BlockPos[]{
            new BlockPos(0, -1, 0),  
            new BlockPos(0, 0, -1),  
            new BlockPos(1, 0, 0),  
            new BlockPos(0, 0, 1),  
            new BlockPos(-1, 0, 0) 
};

private static Minecraft mc = Minecraft.getMinecraft();

        public static void placeBlockScaffold(BlockPos pos) {
        Vec3d eyesPos = new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ);
        for (EnumFacing side : EnumFacing.values()) {
        BlockPos neighbor = pos.offset(side);
        EnumFacing side2 = side.getOpposite();

        
        if (!canBeClicked(neighbor)) {
        continue;
        }
        Vec3d hitVec = new Vec3d(neighbor).add(0.5, 0.5, 0.5).add(new Vec3d(side2.getDirectionVec()).scale(0.5));

        
        if (eyesPos.squareDistanceTo(hitVec) > 18.0625) {
        continue;
        }

        
        faceVectorPacketInstant(hitVec);
        processRightClickBlock(neighbor, side2, hitVec);
        mc.player.swingArm(EnumHand.MAIN_HAND);
        mc.rightClickDelayTimer = 4;
        return;
        }
        }
    private static Vec3d eyesPos(){ return new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ);}
private static float[] getLegitRotations(Vec3d vec) {
        Vec3d eyesPos = eyesPos();
    double diffX = vec.x - eyesPos.x;
    double diffY = vec.y - eyesPos.y;
    double diffZ = vec.z - eyesPos.z;
    double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        double yaw = Math.toDegrees(Math.atan2(diffZ, diffX)) - 90f;
        double pitch = (-Math.toDegrees(Math.atan2(diffY, diffXZ)));
        return new float[]{(float) (mc.player.rotationYaw + MathHelper.wrapDegrees(yaw - mc.player.rotationYaw)), (float) (mc.player.rotationPitch + MathHelper.wrapDegrees(pitch - mc.player.rotationPitch))};
        }



public static void faceVectorPacketInstant(Vec3d vec) {
            float[] rotations = getLegitRotations(vec);
            mc.player.connection.sendPacket(new CPacketPlayer.Rotation(rotations[0],
            rotations[1], mc.player.onGround));
            }

public static void processRightClickBlock(BlockPos pos, EnumFacing side, Vec3d hitVec) {
        mc.playerController.processRightClickBlock(mc.player,
        mc.world, pos, side, hitVec, EnumHand.MAIN_HAND);
        }

    public static boolean  canBeClicked(BlockPos pos) {
            return getBlock(pos).canCollideCheck(getState(pos), false);
            }

public static Block getBlock(BlockPos pos) {
        return getState(pos).getBlock();
        }

private static IBlockState getState(BlockPos pos) {
        return mc.world.getBlockState(pos);
        }

        public static boolean checkForNeighbours(BlockPos blockPos) {
        
        if (!hasNeighbour(blockPos)) {
        
        for (EnumFacing side : EnumFacing.values()) {
        BlockPos neighbour = blockPos.offset(side);
        if (hasNeighbour(neighbour)) {
        return true;
        }
        }
        return false;
        }
        return true;
        }

        static boolean hasNeighbour(BlockPos blockPos) {
        for (EnumFacing side : EnumFacing.values()) {
        BlockPos neighbour = blockPos.offset(side);
        if (!mc.world.getBlockState(neighbour).getMaterial().isReplaceable()) {
        return true;
        }
        }
        return false;
        }


        boolean checkForLiquid() {
        return getGroundPosY(true) == -999.0;
        }

 
        double getGroundPosY(boolean checkLiquid) {
        AxisAlignedBB boundingBox = mc.player.boundingBox;
        double yOffset = mc.player.posY - boundingBox.minY;
            int[]xArray = new int[]{(int) Math.floor(boundingBox.minX), (int) Math.floor(boundingBox.maxX)};
            int[] zArray = new int[]{(int) Math.floor(boundingBox.minZ), (int) Math.floor(boundingBox.maxZ)};
        while (!mc.world.collidesWithAnyBlock(boundingBox.offset(0.0, yOffset, 0.0))) {
        if (checkLiquid) {

            for (int x = 0; x <= 1; x++) {
                for (int z = 0; z <= 1; z++) {
                    BlockPos blockPos = new BlockPos(xArray[x], (int)(mc.player.posY + yOffset), zArray[z]);
                    if (isLiquid(blockPos)) return -999.0;
                }
            }

        
        }
        yOffset -= 0.05;
        if (mc.player.posY + yOffset < 0.0f) return -999.0;
        }
        return boundingBox.offset(0.0, yOffset + 0.05, 0.0).minY;
        }

        boolean isLiquid( BlockPos pos){
        return mc.world.getBlockState(pos).getBlock().material.isLiquid();
        }

        boolean isWater( BlockPos pos){
        return mc.world.getBlockState(pos).getBlock() == Blocks.WATER;
        }

 
        boolean isPlaceable(BlockPos pos) {
        AxisAlignedBB bBox = mc.player.boundingBox;
            int[] xArray = new int[]{(int) Math.floor(bBox.minX), (int) Math.floor(bBox.maxX)};
            int[] yArray = new int[]{(int) Math.floor(bBox.minY), (int) Math.floor(bBox.maxY)};
        int[] zArray = new int[]{(int) Math.floor(bBox.minZ), (int) Math.floor(bBox.maxZ)};
        

            for (int x = 0; x <= 1; x++) {
                for (int y = 0; y <= 1; y++) {
                    for (int z = 0; z <= 1; z++) {
                        if (pos == new BlockPos(xArray[x], yArray[y], zArray[z])) return false;
                    }
                }
            }

        return mc.world.isAirBlock(pos) && !mc.world.isAirBlock(pos.down());
        }

   
        boolean isPlaceableForChest(BlockPos pos) {
        return isPlaceable(pos) && mc.world.isAirBlock(pos.up());
        }
        }