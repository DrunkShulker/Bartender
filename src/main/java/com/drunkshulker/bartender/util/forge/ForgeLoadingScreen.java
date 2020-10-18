package com.drunkshulker.bartender.util.forge;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import com.drunkshulker.bartender.Bartender;

public class ForgeLoadingScreen {
	
	public static void modify(boolean custom) {
		
		final String path = Bartender.MINECRAFT_DIR+"/config/splash.properties";
		File f = new File(path);
		
		if(!f.exists() || f.isDirectory()) {
			System.out.println("config/splash.properties not found");
			return;
		}
		
		try {
			FileInputStream in = new FileInputStream(path);
			Properties props = new Properties();
			props.load(in);
			in.close();
	
			FileOutputStream out = new FileOutputStream(path);
			
			if(custom){
				props.setProperty("background", "0x000000");
				props.setProperty("memoryGood", "0x78CB34");
				props.setProperty("font", "0xFFFFFF");
				props.setProperty("barBackground", "0x57007F");
				props.setProperty("barBorder", "0xB200FF");
				props.setProperty("memoryLow", "0xE42F2F");
				props.setProperty("rotate", "false");
				props.setProperty("memoryWarn", "0xE6E84A");
				props.setProperty("showMemory", "true");
				props.setProperty("bar", "0xCB3D35");
				props.setProperty("resourcePackPath", "resources");
				props.setProperty("logoOffset", "0");
				props.setProperty("forgeTexture", "textures/misc/shadow.png");
				props.setProperty("fontTexture", "textures/font/ascii.png");

			} else {
				props.setProperty("background", "0xFFFFFF");
				props.setProperty("memoryGood", "0x78CB34");
				props.setProperty("font", "0x0");
				props.setProperty("barBackground", "0xFFFFFF");
				props.setProperty("barBorder", "0xC0C0C0");
				props.setProperty("memoryLow", "0xE42F2F");
				props.setProperty("rotate", "false");
				props.setProperty("memoryWarn", "0xE6E84A");
				props.setProperty("showMemory", "true");
				props.setProperty("bar", "0xCB3D35");
				props.setProperty("resourcePackPath", "resources");
				props.setProperty("logoOffset", "0");
				props.setProperty("forgeTexture", "fml\\:textures/gui/forge.png");
				props.setProperty("fontTexture", "textures/font/ascii.png");
			}

			props.store(out, null);
			out.close();
		
		} catch (Exception e) {
			System.out.println("Failed to modify Forge loading screen.");
		}
	}

}
