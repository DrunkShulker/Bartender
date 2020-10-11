package com.drunkshulker.bartender.client.social;

import java.util.*;
import java.util.stream.Collectors;

import com.drunkshulker.bartender.client.commands.GroupCommand;
import com.drunkshulker.bartender.client.gui.clickgui.ClickGuiSetting;
import com.drunkshulker.bartender.client.module.AutoBuild;
import com.drunkshulker.bartender.client.module.BaseFinder;
import com.drunkshulker.bartender.client.module.Bodyguard;
import com.drunkshulker.bartender.util.Config;
import com.google.gson.JsonArray;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.init.Blocks;
import net.minecraft.util.text.TextComponentString;

public class PlayerGroup {
	
	public static ArrayList<String> members;
	public static final ArrayList<String> DEFAULT_MEMBERS = new ArrayList<>(Arrays.asList("DrunkShulker", "DrunkShuIker", "DrunkShu1ker"));
	public static boolean groupAcceptTpa = false;
	public static boolean groupAcceptTpaHere = false;
	public static String mainAccount;
	public static boolean useClickEntity = false;

	public static boolean isPlayerOnline(String username){
		Minecraft minecraft = Minecraft.getMinecraft();
		final NetHandlerPlayClient netHandlerPlayClient = minecraft.getConnection();

		if (netHandlerPlayClient != null) {
			final Collection<NetworkPlayerInfo> playerInfoMap = netHandlerPlayClient.getPlayerInfoMap();
			final GuiPlayerTabOverlay tabOverlay = minecraft.ingameGUI.getTabList();

			final String outputText = playerInfoMap.stream()
					.map(tabOverlay::getPlayerName)
					.collect(Collectors.joining(", "));
			
			List<String> items = Arrays.asList(outputText.split("\\s*,\\s*"));
			if(items.contains(username)) return true;
		}

		return false;
	}
	
	public static void addPlayer(String player) {
		if(player.equalsIgnoreCase(Minecraft.getMinecraft().player.getDisplayNameString())) {
			Minecraft.getMinecraft().player.sendMessage(new TextComponentString("You can't add yourself to a group."));
		}
		else if(!members.contains(player)) {
			members.add(player);
			Config.save();
			Minecraft.getMinecraft().player.sendMessage(new TextComponentString(player + " is added to your group!"));
		} else {
			Minecraft.getMinecraft().player.sendMessage(new TextComponentString(player + " is already in your group!"));
		}
		Collections.sort(members);
	}
	
	public static void removePlayer(String player) {
		if(player.equalsIgnoreCase(Minecraft.getMinecraft().player.getDisplayNameString())) {
			Minecraft.getMinecraft().player.sendMessage(new TextComponentString("You can't remove yourself from a group."));
		}
		else if(members.contains(player)) {
			members.remove(player);
			Config.save();
			Minecraft.getMinecraft().player.sendMessage(new TextComponentString(player + " was removed from your group."));
		} else {
			Minecraft.getMinecraft().player.sendMessage(new TextComponentString(player + " is not in your group"));
		}
		Collections.sort(members);
	}
	
	public static JsonArray toJsonArray() {	
		JsonArray json = new JsonArray();
		for (String member : members) {
			json.add(member);
		}
		return json;
	}
	
	public static void fromJsonArray(JsonArray json) {	
		members = new ArrayList<>();
		for (int i = 0; i < json.size(); i++) {
			members.add(json.get(i).getAsString());
		}
		Collections.sort(members);
	}

	public static void defaultGroup() {
		members = new ArrayList<>();
		mainAccount = "na";
		Collections.sort(members);
	}

	public static void tpaccept(String message) {
		
		String toThem = "has requested that you teleport to them.";
		if(groupAcceptTpaHere && message.contains(toThem)) {
			for (String member : PlayerGroup.members) {
				
				if((member).equalsIgnoreCase(Minecraft.getMinecraft().player.getDisplayNameString())) continue;
				
				String pre = member + " " + toThem;
				if(message.startsWith(pre)) {
					
					BaseFinder.prepareTpa();
					AutoBuild.prepareTpa();
					Bodyguard.prepareTpa();
					
					Minecraft.getMinecraft().player.sendMessage(new TextComponentString("<Bartender> Accepting /tpahere to: " + member));
					Minecraft.getMinecraft().player.sendChatMessage("/tpaccept");
					return;
				}
			}
			Minecraft.getMinecraft().player.sendMessage(new TextComponentString("<Bartender> Ignoring /tpahere to: " + message.split("\\s+")[0]));
		}
		
		
		String toYou = "has requested to teleport to you.";
		if(groupAcceptTpa && message.contains(toYou)) {
			for (String member : PlayerGroup.members) {
				
				if((member).equalsIgnoreCase(Minecraft.getMinecraft().player.getDisplayNameString())) continue;
				
				String pre = member + " " + toYou;
				System.out.println("RECEIVED: "+message.substring(0, pre.length()));
				System.out.println("PRE: "+pre);
				if(message.startsWith(pre)) {
					
					Minecraft.getMinecraft().player.sendMessage(new TextComponentString("Accepting /tpa to: " + member));
					Minecraft.getMinecraft().player.sendChatMessage("/tpaccept");
					return;
				}
			}
			Minecraft.getMinecraft().player.sendMessage(new TextComponentString("Ignoring /tpa to: " + message.split("\\s+")[0]));
		}
	}

	public static void applyPreferences(ClickGuiSetting[] settings) {
		for (ClickGuiSetting setting : settings) {
			if(setting.title.equals("tpaccept")) {

				if(setting.value==0) {
					groupAcceptTpa = false;
					groupAcceptTpaHere = false;
				}
				
				if(setting.value==1) {
					groupAcceptTpa = false;
					groupAcceptTpaHere = true;
				}
				
				if(setting.value==2) {
					groupAcceptTpa = true;
					groupAcceptTpaHere = false;
				} 
				
				if(setting.value==3) {
					groupAcceptTpa = true;
					groupAcceptTpaHere = true;
				} 
			}
			else if(setting.title.equals("friends")){
					if(setting.value == 0) {
						Bodyguard.friendly = Bodyguard.Friendly.IMPACT_FRIENDS;
					}else if(setting.value == 1) {
						Bodyguard.friendly = Bodyguard.Friendly.FRIENDS_LIST;
					}else if(setting.value == 2) {
						Bodyguard.friendly = Bodyguard.Friendly.BOTH;
					}else if(setting.value == 3) {
						Bodyguard.friendly = Bodyguard.Friendly.NONE;
					}
			}
			else if(setting.title.equals("click player")){
				useClickEntity = setting.value == 1;
			}
		}
	}

	public static void clickAction(String action) {
		EntityPlayerSP p = Minecraft.getMinecraft().player;
		if(p==null) return;
		switch (action) {
		case "here":
			
			try {
				new GroupCommand().execute(null, Minecraft.getMinecraft().player, new String[]{"here"});
			}catch (Exception e){}
			break;
		case "leave group":
			
			try {
				new GroupCommand().execute(null, Minecraft.getMinecraft().player, new String[]{"leave"});
			}catch (Exception e){}
			break;
		case "retreat":
			Bodyguard.fallBack(true);
			break;
		case "chorus":
			Bodyguard.eatChorus(true);
			break;
		case "end tasks":
			Bodyguard.endAllTasks(true);
			break;
		case "stand here":
			Bodyguard.sendGoToCommand();
			break;
		case "obby grief":
			Bodyguard.startMineNearbyBlocks(Blocks.OBSIDIAN, true);
			break;
		case "ground level":
			Bodyguard.sendGoToGroundCommand();
			break;
		case "takeoff":
			Bodyguard.sendTakeOffCommand();
			break;
		default:
			break;
		}
	}

	public static void setMainAcc(String name) {
		mainAccount = name;
		Config.save();
		Minecraft.getMinecraft().player.sendMessage(new TextComponentString("<Bartender> " + name + " is now priority group member!"));
	}
}
