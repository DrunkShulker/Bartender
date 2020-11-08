package com.drunkshulker.bartender.util.kami;

import com.drunkshulker.bartender.util.salhack.events.MinecraftEvent;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class AddCollisionBoxToListEvent extends MinecraftEvent {
    public Block block;
    IBlockState state;
    World world;
    public BlockPos pos;
    public AxisAlignedBB entityBox;
    public ArrayList<AxisAlignedBB> collidingBoxes;
    public Entity entity;
    boolean isBool;

    public AddCollisionBoxToListEvent(
            Block block,
            IBlockState state,
            World world,
            BlockPos pos,
            AxisAlignedBB entityBox,
            List<AxisAlignedBB> collidingBoxes,
            Entity entity,
            boolean isBool
    ) {
        super();
        this.block = block;
        this.state = state;
        this.world = world;
        this.pos = pos;
        this.entityBox = entityBox;
        this.collidingBoxes = new ArrayList<>(collidingBoxes);
        this.entity = entity;
        this.isBool = isBool;
    }

}