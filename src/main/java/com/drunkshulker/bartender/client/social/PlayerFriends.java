package com.drunkshulker.bartender.client.social;

import java.io.File;
import java.util.ArrayList;

import com.drunkshulker.bartender.Bartender;
import com.drunkshulker.bartender.util.Config;
import com.google.gson.JsonArray;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentKeybind;

public class PlayerFriends {
	public static ArrayList<String> impactFriends = new ArrayList<String>();
	public static ArrayList<String> friends = new ArrayList<String>();
	
	
	public static void save() {
		Config.save();
	}
	
	public static void loadImpactFriends() {
		if(Bartender.IMPACT_INSTALLED) {
			String friendsFilePath = Bartender.MINECRAFT_DIR+"/Impact/friends.cfg";
			if(new File(friendsFilePath).exists()) {
				String contents = Config.readFile(friendsFilePath);
				System.out.println("Impact friends loaded:");
				impactFriends.clear();
				for (String string : contents.split("\n")) {
					System.out.println(string.split(":")[0]);
					impactFriends.add(string.split(":")[0]);
				}
			}
		}else {
			String msg = "Impact is not installed.";
			System.out.println(msg);
			if(Minecraft.getMinecraft().player!=null) {
				Bartender.msg(msg);
			}
		}		
	}
	
	public static void removeFriend(String name) {
		if(friends.contains(name)) {
			friends.remove(name);
			Bartender.msg(""+name+" was removed from friends!");
		}
		save();
	}

	public static void addFriend(String name) {
		if(!friends.contains(name)) {
			friends.add(name);
			Bartender.msg(""+name+" was added as friend!");
		}
		save();
	}
	
	public static JsonArray toJsonArray() {	
		JsonArray json = new JsonArray();
		for (int i = 0; i < friends.size(); i++) {
			json.add(friends.get(i));
		}
		return json;
	}
	
	public static void fromJsonArray(JsonArray json) {	
		friends = new ArrayList<String>();	
		for (int i = 0; i < json.size(); i++) {
			friends.add(json.get(i).getAsString());
		}
	}
}
