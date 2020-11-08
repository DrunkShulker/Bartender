package com.drunkshulker.bartender.client.module;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.drunkshulker.bartender.util.kami.ColourConverter;
import com.drunkshulker.bartender.util.kami.ColourUtils;
import com.drunkshulker.bartender.util.kami.CoordUtil;
import com.drunkshulker.bartender.util.kami.Coordinate;
import com.drunkshulker.bartender.util.kami.GeometryMasks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPortal;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ChunkProviderClient;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Search {
	
	private Set<Block> espBlocks;
    public static Map<BlockPos, Tuple<Integer, Integer>> blocksToShow;

    private long startTime = 0;
	final static Map<ChunkPos, Map<BlockPos, Tuple<Integer, Integer>>> mainList = new ConcurrentHashMap<>();
	
	final static int REFRESH_INTERVAL_MILLIS = 2000;
	private static int currentDimCode = 0;
	final static String shulks = "minecraft:white_shulker_box, minecraft:orange_shulker_box, minecraft:magenta_shulker_box, minecraft:light_blue_shulker_box, minecraft:yellow_shulker_box, minecraft:lime_shulker_box, minecraft:pink_shulker_box, minecraft:gray_shulker_box, minecraft:silver_shulker_box, minecraft:cyan_shulker_box, minecraft:purple_shulker_box, minecraft:blue_shulker_box, minecraft:brown_shulker_box, minecraft:green_shulker_box, minecraft:red_shulker_box, minecraft:black_shulker_box, ";
	
    public static final String[] DEFAULT_TARGETS = {
			"minecraft:obsidian, minecraft:end_portal_frame, minecraft:bed, minecraft:trapped_chest, minecraft:item_frame, "+shulks, 
			"minecraft:ender_chest, minecraft:hopper, minecraft:item_frame, minecraft:portal, minecraft:end_portal_frame, minecraft:trapped_chest, "+shulks, 
			"minecraft:dirt, minecraft:end_portal_frame, minecraft:bed, minecraft:trapped_chest, "+shulks, 
	};

    public static String[] targetsLists = DEFAULT_TARGETS;

    public static void loadTargets(String bf_targets_nether, String bf_targets_overworld, String bf_targets_end){
        targetsLists = new String[3];
        targetsLists[0] = bf_targets_nether;
        targetsLists[1] = bf_targets_overworld;
        targetsLists[2] = bf_targets_end;
    }

    public static String espBlockNames;
    
    public static String getTargetsForDimension(int dim) {
		if(Minecraft.getMinecraft().player==null) return "minecraft:end_portal_frame, minecraft:bed";	
		return targetsLists[dim+1];
	}

	

    public void onPlayerChangedDimension() {
        if(Minecraft.getMinecraft().player==null) return;
        currentDimCode=Minecraft.getMinecraft().player.dimension;
        
        mainList.clear();
        if(espBlocks!=null)Bodyguard.localDimensionChanged();
        refreshESPBlocksSet(getTargetsForDimension(Minecraft.getMinecraft().player.dimension));
    }

	public String extGet() {
        return extGetInternal(null);
    }

    private String extGetInternal(Block filter) {
        StringBuilder sb = new StringBuilder();
        boolean notFirst = false;
        for (Block b : espBlocks) {
            if (b == filter)
                continue;
            if (notFirst)
                sb.append(", ");
            notFirst = true;
            sb.append(Block.REGISTRY.getNameForObject(b));
        }
        return sb.toString();
    }

    
    public void extAdd(String s) {
        espBlockNames=(extGetInternal(null) + ", " + s);
    }

    
    public void extRemove(String s) {
        espBlockNames=(extGetInternal(Block.getBlockFromName(s)));
    }

    
    public void extClear() {
        espBlockNames = "";
    }

    
    public void extDefaults() {
        extClear();
        extAdd(getTargetsForDimension(Minecraft.getMinecraft().player.dimension));
    }

    
    public void extSet(String s) {
        extClear();
        extAdd(s);
    }

    Executor exec = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(),
            r -> {
                Thread t = new Thread(r);
                t.setPriority(Thread.NORM_PRIORITY - 2); 
                return t;
            });
    Executor cachedExec = Executors.newCachedThreadPool();
	
	private boolean shouldRun() {
        if (Minecraft.getMinecraft().world == null) return false;
        if (startTime == 0)
            startTime = System.currentTimeMillis();
        if (startTime + REFRESH_INTERVAL_MILLIS <= System.currentTimeMillis()) { 
            startTime = System.currentTimeMillis();
            return true;
        }
        return false;
    }
	
	@SubscribeEvent
	public void playerTick(TickEvent.PlayerTickEvent event){	
        if (Minecraft.getMinecraft().player == null) return;
        if(espBlocks == null||currentDimCode!=Minecraft.getMinecraft().player.dimension){
            onPlayerChangedDimension();
        }

        if (shouldRun() && isEnabled()) reloadChunks();
	}
	
	@SubscribeEvent
	public void load(ChunkEvent.Load event) {
		if(isEnabled()) {
			cachedExec.execute(() -> {
                Chunk chunk = event.getChunk();
                ChunkPos pos = chunk.getPos();
                Map<BlockPos, Tuple<Integer, Integer>> found = findBlocksInChunk(chunk, espBlocks);
                if (!found.isEmpty()) {
                    Map<BlockPos, Tuple<Integer, Integer>> actual = mainList.computeIfAbsent(pos, (p) -> new ConcurrentHashMap<>());
                    actual.clear();
                    actual.putAll(found);
                }
            });
		}
	}
	
	private boolean isEnabled() {
		return BaseFinder.enabled;
	}
	
	@SubscribeEvent
	public void unload(ChunkEvent.Unload event) {
		
		if(!isEnabled()) {
			mainList.clear();
		}
		else mainList.remove(event.getChunk().getPos());	
	}
	
	private void reloadChunks() {
        Coordinate pcoords = CoordUtil.getCurrentCoord();
        int renderdist = Minecraft.getMinecraft().gameSettings.renderDistanceChunks;
        if (renderdist > 8) {
            renderdist = 8;
        }
        ChunkProviderClient providerClient = Minecraft.getMinecraft().world.getChunkProvider();
        for (int x = -renderdist; x < renderdist; x++) {
            for (int z = -renderdist; z < renderdist; z++) {
                Chunk chunk = providerClient.getLoadedChunk((pcoords.x >> 4) + x, (pcoords.z >> 4) + z);
                if (chunk != null)
                    exec.execute(() ->
                            loadChunk(chunk)
                    );
            }
        }
    }
	
	private void loadChunk(Chunk chunk) {
        Map<BlockPos, Tuple<Integer, Integer>> actual = mainList.get(chunk.getPos());
        Map<BlockPos, Tuple<Integer, Integer>> found = findBlocksInChunk(chunk, espBlocks);
        if (!found.isEmpty() || actual != null) {
            actual = mainList.computeIfAbsent(chunk.getPos(), (p) -> new ConcurrentHashMap<>());
            actual.clear();
            actual.putAll(found);
        }
    }
	
	private Tuple<Integer, Integer> getTuple(int side, Block block) {
        int c = ColourConverter.rgbToInt(222, 4, 4);
        if (block instanceof BlockPortal) {
            c = ColourConverter.rgbToInt(82, 49, 153);
        }
        int[] cia = {c >> 16, c >> 8 & 255, c & 255};
        int blockColor = ColourUtils.toRGBA(cia[0], cia[1], cia[2], 150);
        return new Tuple<>(blockColor, side);
    }

	
	private Map<BlockPos, Tuple<Integer, Integer>> findBlocksInChunk(Chunk chunk, Set<Block> blocksToFind) {
        BlockPos pos1 = new BlockPos(chunk.getPos().getXStart(), 0, chunk.getPos().getZStart());
        BlockPos pos2 = new BlockPos(chunk.getPos().getXEnd(), 256, chunk.getPos().getZEnd());
        Iterable<BlockPos> blocks = BlockPos.getAllInBox(pos1, pos2);
        Map<BlockPos, Tuple<Integer, Integer>> foundBlocks = new HashMap<>();
        try {
            for (BlockPos blockPos : blocks) {
                int side = GeometryMasks.Quad.ALL;
                Block block = chunk.getBlockState(blockPos).getBlock();
                if (blocksToFind.contains(block)) {
                    Tuple<Integer, Integer> tuple = getTuple(side, block);
                    foundBlocks.put(blockPos, tuple);
                }
            }
        } catch (NullPointerException ignored) {
        } 
        if(!foundBlocks.isEmpty()) BaseFinder.targetFound(foundBlocks.size(), chunk);
        return foundBlocks;
    }
	
	private void refreshESPBlocksSet(String v) {
        espBlocks = Collections.synchronizedSet(new HashSet<>());
        for (String s : v.split(",")) {
            String s2 = s.trim();
            if (!s2.equals("minecraft:air")) {
                Block block = Block.getBlockFromName(s2);
                if (block != null)
                    espBlocks.add(block);
            }
        }
        mainList.clear();
        startTime = 0;
    }
}
