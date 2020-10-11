package com.drunkshulker.bartender.util.forge;

import java.io.File;

import com.drunkshulker.bartender.Bartender;

public class ForgeModsHelper {
	
	public static boolean impactInstalled() {
		if(Bartender.MINECRAFT_DIR==null) return false;
		return new File(Bartender.MINECRAFT_DIR+"/Impact").exists();
	}
	
	public static boolean kamiBlueInstalled() {
		if(Bartender.MINECRAFT_DIR==null) return false;
		return new File(Bartender.MINECRAFT_DIR+"/KAMIBlueConfig.json").exists();
	}
}
