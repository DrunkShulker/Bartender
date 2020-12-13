package com.drunkshulker.bartender.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import com.drunkshulker.bartender.Bartender;
import com.drunkshulker.bartender.client.input.Keybinds;
import com.drunkshulker.bartender.client.module.BaseFinder;
import com.drunkshulker.bartender.client.module.Cosmetic;
import com.drunkshulker.bartender.client.module.Search;
import com.drunkshulker.bartender.client.social.PlayerFriends;
import com.drunkshulker.bartender.client.social.PlayerGroup;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;

public class Config {

	public static final String FILENAME = "bartender-config.json";
	public static String[] HOTKEY_COMMANDS = new String[9];
	public static String CHAT_POST_FIX;
	
	public static void load()
	{
		
		File f = new File(Bartender.BARTENDER_DIR+"/"+FILENAME);
		if(f.exists() && !f.isDirectory())
		{ 		
		    
			final String json = readFile(f.getAbsolutePath()).replace("\\\"", "");
			JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();
			
			
			String configVersion = jsonObject.get("bartender_version").getAsString();
			if(!configVersion.equals(Bartender.VERSION)) {
				
				System.out.println("Config file was from an older version, using default config!");
				defaults();
				return;
			}
			
			
			JsonArray hotkeyCommands = jsonObject.get("hotkey_commands").getAsJsonArray();
			
			for (int i = 0; i < 9; i++) {
				HOTKEY_COMMANDS[i] = hotkeyCommands.get(i).getAsString();
			}
			
			
			PlayerGroup.fromJsonArray(jsonObject.getAsJsonArray("player_group"));
			PlayerGroup.mainAccount = jsonObject.get("player_group_priority").getAsString();

			
			PlayerFriends.fromJsonArray(jsonObject.getAsJsonArray("player_friends"));
			
			
			CHAT_POST_FIX = jsonObject.get("chat_post_fix").getAsString();

			
			Cosmetic.currentHat = Item.getByNameOrId(jsonObject.get("hat").getAsString());

			
			BaseFinder.customTargetGoal = new BlockPos(
			jsonObject.get("bf_custom_goal_x").getAsInt(),0,
			jsonObject.get("bf_custom_goal_z").getAsInt());

			Search.loadTargets(
				jsonObject.get("bf_targets_nether").getAsString(),
				jsonObject.get("bf_targets_overworld").getAsString(),
				jsonObject.get("bf_targets_end").getAsString()
			);

			
			
		}
		else
		{
			
			System.out.println("bartender-config.json not found. Using default settings");
			defaults();
		}
	}

	public static void save()
	{
		
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("bartender_version", Bartender.VERSION);
		
		
		jsonObject.addProperty("bf_custom_goal_x", BaseFinder.customTargetGoal.getX());
		jsonObject.addProperty("bf_custom_goal_z", BaseFinder.customTargetGoal.getZ());

		jsonObject.addProperty("bf_targets_nether", Search.targetsLists[0]);
		jsonObject.addProperty("bf_targets_overworld", Search.targetsLists[1]);
		jsonObject.addProperty("bf_targets_end", Search.targetsLists[2]);
		
		JsonArray hotkeyCommands = new JsonArray();
		
		for (int i = 0; i < 9; i++) {
			hotkeyCommands.add(HOTKEY_COMMANDS[i]);
		}

		jsonObject.add("hotkey_commands", hotkeyCommands);
		
		
		jsonObject.add("player_group", PlayerGroup.toJsonArray());
		jsonObject.addProperty("player_group_priority", PlayerGroup.mainAccount);
		
		jsonObject.add("player_friends", PlayerFriends.toJsonArray());

		
		jsonObject.addProperty("chat_post_fix", CHAT_POST_FIX);

		
		jsonObject.addProperty("hat", Item.getIdFromItem(Cosmetic.currentHat)+"");

		
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String json = gson.toJson(jsonObject).replace("\\\"", "");
		try {
			writeFile(Bartender.BARTENDER_DIR+"/"+FILENAME, json);
			System.out.println("Saved config.");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public static void defaults()
	{	
		
		for (int i = 0; i < 9; i++) {
			HOTKEY_COMMANDS[i] = Keybinds.HOTKEY_COMMAND_DEFAULTS[i];
		}
		
		
		PlayerGroup.defaultGroup();

		
		CHAT_POST_FIX = getDefaultChatPostFix();
		
		save();
		
	}
	
	public static String readFile(String filePath) 
    {
        StringBuilder contentBuilder = new StringBuilder();
 
        try (Stream<String> stream = Files.lines( Paths.get(filePath), StandardCharsets.UTF_8)) 
        {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        }
        catch (IOException e) 
        {
            e.printStackTrace();
        }
 
        return contentBuilder.toString();
    }
	
	public static void writeFile(String filePath, String text) throws IOException
	{
	    Writer out = null;   
        out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), "UTF-8"));
        out.write(text);
        out.close();    
	}

	public static String getDefaultChatPostFix() {
		JsonObject jsonObject = new AssetLoader().loadJson("bartender-chatfix.json");
		return jsonObject.get("default").getAsString();
	}

}
