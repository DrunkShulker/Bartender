package com.drunkshulker.bartender.client.module;

import java.io.File;
import java.io.IOException;

import com.drunkshulker.bartender.Bartender;
import com.drunkshulker.bartender.client.gui.clickgui.ClickGuiSetting;
import com.drunkshulker.bartender.util.AssetLoader;

import baritone.api.BaritoneAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.chunk.Chunk;

public class AutoBuild {

	private static boolean mapartMode=false;
	private static boolean layerMode=false;

	public static void buildSchematic(String schematic) {
		updateBaritoneSettings();
		if(ElytraFlight.enabled) ClickGuiSetting.handleClick(ClickGuiSetting.fromString("elytra+->state"), false); 
		stop();

		ResourceLocation portalSchematic = new ResourceLocation(Bartender.NAME, "schematics/"+schematic);
		BlockPos playerPos = Minecraft.getMinecraft().player.getPosition();
		File schematicFile = null;

		try {
			schematicFile = new AssetLoader().extractSchematic(schematic);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Bartender failed to extract schematic");
		}

		if(schematicFile==null) {
			Minecraft.getMinecraft().player.sendMessage(new TextComponentString("<"+Bartender.NAME+"> Failed to extract schematic"));
			return;
		}

		BaritoneAPI.getProvider().getPrimaryBaritone().getBuilderProcess().build("Nether portal", schematicFile, playerPos);

	}
	
	public static void buildOpenSchematic() {
		updateBaritoneSettings();
		if(ElytraFlight.enabled) ClickGuiSetting.handleClick(ClickGuiSetting.fromString("elytra+->state"), false); 
		stop();
		BaritoneAPI.getProvider().getPrimaryBaritone().getBuilderProcess().buildOpenSchematic();
	}
	
	public static void stop() {
		if(BaritoneAPI.getProvider().getPrimaryBaritone().getBuilderProcess().isActive())
		BaritoneAPI.getProvider().getPrimaryBaritone().getBuilderProcess().pause();
	}

	public static void clickAction(String action) {
		updateBaritoneSettings();
		switch (action) {
		case "stop":
			stop();
			break;
		case "current":
			buildOpenSchematic();
			break;
		case "nether portal":
			buildSchematic("nether_portal.schematic");
			break;
		case "tombstone":
			buildSchematic("tomb.schematic");
			break;
		case "nomad hut":
			buildSchematic("nomad.schematic");
			break;
		case "swastika":
			buildSchematic("heil_vertical.schematic");
			break;
		case "youtube ad":
			buildSchematic("drunk_yt.schematic");
			break;
		case "bartender ad":
			buildSchematic("bartender_logo.schematic");
			break;
		case "clear chunk":
			clearChunk();
			break;
		default:
			break;
		}
	}

	private static void clearChunk() {
		stop();
		if(ElytraFlight.enabled) ClickGuiSetting.handleClick(ClickGuiSetting.fromString("elytra+->state"), false); 
		Chunk chunk = Minecraft.getMinecraft().world.getChunk(Minecraft.getMinecraft().player.getPosition());
		BaritoneAPI.getProvider().getPrimaryBaritone().getBuilderProcess().clearArea(new BlockPos(chunk.getPos().getXStart(), 0, chunk.getPos().getZStart()), new BlockPos(chunk.getPos().getXEnd(), 256, chunk.getPos().getZEnd()));
	}

	public static void prepareTpa() {
		stop();
	}

	private static void updateBaritoneSettings(){
		BaritoneAPI.getSettings().mapArtMode.value=mapartMode;
		BaritoneAPI.getSettings().buildInLayers.value=layerMode;
	}

	public static void applyPreferences(ClickGuiSetting[] contents) {
		for (ClickGuiSetting setting : contents) {
			switch (setting.title) {
				case "mapart mode":
					mapartMode=setting.value == 1;
					break;
				case "layer mode":
					layerMode=setting.value == 1;
					break;
				default:
					break;
			}
		}
	}
}