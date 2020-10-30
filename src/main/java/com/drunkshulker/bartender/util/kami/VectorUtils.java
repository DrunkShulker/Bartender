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

        
        
        
        
        
        
        
        return returnList;
        }


        public static ArrayList<BlockPos> getBlockPosInSphere(Vec3d center, float radius) {
            double squaredRadius =  Math.pow(radius,2);
            ArrayList<BlockPos> posList =new ArrayList<BlockPos>();



            
            
            
                
            
            
            
            

            for (int x = (int) Math.floor(center.x - radius); x < Math.ceil(center.x + radius); x++) {
                for (int y = (int) Math.floor(center.y - radius); y < Math.ceil(center.y + radius); y++) {
                    for (int z = (int) Math.floor(center.z - radius); z < Math.ceil(center.z + radius); z++) {
                        
                        BlockPos blockPos = new BlockPos(x, y, z);
                        if (blockPos.distanceSqToCenter(center.x, center.y, center.z) > squaredRadius) continue;
                        posList.add(blockPos);
                    }
                }
            }

            
            return posList;
        }

        
        
       

    BlockPos toBlockPos(Vec3d v) {
        return new BlockPos(Math.floor(v.x), Math.floor(v.y), Math.floor(v.z));
        }

    Vec3d toVec3d(BlockPos b) {
        return new Vec3d(b).add(0.5, 0.5, 0.5);
        }
        }