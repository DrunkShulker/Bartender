package com.drunkshulker.bartender.util.kami;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;


public class VectorUtils {

        double getDistance(Vec3d vecA,Vec3d vecB) {

            return Math.sqrt(
                    Math.pow(
                            (vecA.x - vecB.x),2.0) +
                            Math.pow(     (vecA.y - vecB.y),2.0) +
                            Math.pow(  (vecA.z - vecB.z),2.0));

        }


/*
    ArrayList<Vec3d> extendVec(Vec3d startVec ,Vec3d destinationVec,int steps) {
        ArrayList<Vec3d> returnList = new ArrayList<Vec3d>(steps + 1);
        double stepDistance = getDistance(startVec, destinationVec) / steps;
        for (i in 0 until max(steps, 1) + 1) {
        returnList.add(advanceVec(startVec, destinationVec, stepDistance * i));
        }
        return returnList;
        }


        Vec3d advanceVec(Vec3d startVec,Vec3d destinationVec,double distance) {
        Vec3d advanceDirection = destinationVec.subtract(startVec).normalize();
        if (destinationVec.distanceTo(startVec) < distance)return  destinationVec;
        else return  advanceDirection.scale(distance);
        }
*/

    List<BlockPos> getBlockPositionsInArea(Vec3d pos1 ,Vec3d pos2) {
        int minX = (int) Math.min(pos1.x, pos2.x);
        int  maxX = (int) Math.max(pos1.x, pos2.x);
        int  minY = (int) Math.min(pos1.y, pos2.y);
        int  maxY = (int) Math.max(pos1.y, pos2.y);
        int  minZ = (int) Math.min(pos1.z, pos2.z);
        int  maxZ = (int) Math.max(pos1.z, pos2.z);
        return getBlockPos(minX, maxX, minY, maxY, minZ, maxZ);
        }


    List<BlockPos> getBlockPositionsInArea(BlockPos pos1 ,BlockPos pos2)
    {
        int  minX = Math.min(pos1.x, pos2.x);
        int  maxX = Math.max(pos1.x, pos2.x);
        int  minY = Math.min(pos1.y, pos2.y);
        int  maxY = Math.max(pos1.y, pos2.y);
        int  minZ = Math.min(pos1.z, pos2.z);
        int  maxZ = Math.max(pos1.z, pos2.z);
        return getBlockPos(minX, maxX, minY, maxY, minZ, maxZ);
        }

        BlockPos getHighestTerrainPos(BlockPos pos) {
            if (Minecraft.getMinecraft().world==null) return new BlockPos(0,0,0);
           // for (i in pos.y downTo 0) {
        for (int i=pos.y; i>=0;i--) {
        Block block = Minecraft.getMinecraft().world.getBlockState(new BlockPos(pos.getX(), i, pos.getZ())).getBlock();
        boolean replaceable = Minecraft.getMinecraft().world.getBlockState(new BlockPos(pos.getX(), i, pos.getZ())).getMaterial().isReplaceable();
        if (!(block instanceof BlockAir) && !replaceable) {
        return new BlockPos(pos.getX(), i, pos.getZ());
        }
        }
        return new BlockPos(pos.getX(), 0, pos.getZ());
        }

private ArrayList<BlockPos> getBlockPos(int minX,int maxX,int minY,int maxY,int minZ,int maxZ) {
    ArrayList<BlockPos> returnList = new ArrayList<BlockPos>();

    for (int x = minX; x < maxX; x++) {
        for (int z = minZ; z < maxZ; z++) {
            for (int y = minY; y < maxY; y++) {
                returnList.add(new BlockPos(x, y, z));
            }
        }
    }

        //for (x in minX..maxX) {
        //for (z in minZ..maxZ) {
        //for (y in minY..maxY) {
        //returnList.add(BlockPos(x, y, z));
        //}
        //}
        //}
        return returnList;
        }


        public static ArrayList<BlockPos> getBlockPosInSphere(Vec3d center, float radius) {
            double squaredRadius =  Math.pow(radius,2);
            ArrayList<BlockPos> posList =new ArrayList<BlockPos>();



            // for (x in getAxisRange(center.x, radius))
            //     for (y in getAxisRange(center.y, radius))
            //         for (z in getAxisRange(center.z, radius)) {
                // Valid position check
            //    BlockPos blockPos = new BlockPos(x, y, z);
            //    if (blockPos.distanceSqToCenter(center.x, center.y, center.z) > squaredRadius) continue;
            //    posList.add(blockPos);
            //  }

            for (int x = (int) Math.floor(center.x - radius); x < Math.ceil(center.x + radius); x++) {
                for (int y = (int) Math.floor(center.y - radius); y < Math.ceil(center.y + radius); y++) {
                    for (int z = (int) Math.floor(center.z - radius); z < Math.ceil(center.z + radius); z++) {
                        // Valid position check
                        BlockPos blockPos = new BlockPos(x, y, z);
                        if (blockPos.distanceSqToCenter(center.x, center.y, center.z) > squaredRadius) continue;
                        posList.add(blockPos);
                    }
                }
            }

            
            return posList;
        }

        //private IntRange getAxisRange(double d1,float d2) {
        //    return new IntRange(Math.floor(d1 - d2), Math.ceil(d1 + d2));
       // }

    BlockPos toBlockPos(Vec3d v) {
        return new BlockPos(Math.floor(v.x), Math.floor(v.y), Math.floor(v.z));
        }

    Vec3d toVec3d(BlockPos b) {
        return new Vec3d(b).add(0.5, 0.5, 0.5);
        }
        }