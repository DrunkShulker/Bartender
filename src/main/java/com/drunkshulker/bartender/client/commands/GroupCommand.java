package com.drunkshulker.bartender.client.commands;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

import com.drunkshulker.bartender.Bartender;
import com.drunkshulker.bartender.client.gui.GuiConfig;
import com.drunkshulker.bartender.client.social.PlayerGroup;
import com.drunkshulker.bartender.util.Config;
import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class GroupCommand implements ICommand {

	@Override
	public int compareTo(ICommand arg0) {
		return 0;
	}

	@Override
	public String getName() {
		return "group";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/group <action> <player>";
	}

	@Override
	public List<String> getAliases() {
		List<String> aliases = Lists.<String>newArrayList();
		aliases.add("/group");
		return aliases;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {		
		
		
		
		
		
		if(args==null||args.length==0) {
			Bartender.msg((PlayerGroup.toJsonArray().size()==0)
					? "There are no players in your group."
					:PlayerGroup.toJsonArray().toString());
		}
		
		else if(args[0].equalsIgnoreCase("add")) {
			if(args.length==2) {
				if(args[1].contains("~")) Bartender.msg("Never use player's /nick in your group!");
				else PlayerGroup.addPlayer(args[1]);
			}
			else {
				Bartender.msg("Invalid args.");
			}
		}
		
		else if(args[0].equalsIgnoreCase("remove")) {
			if(args.length==2) {
				PlayerGroup.removePlayer(args[1]);
			}
			else {
				Bartender.msg("Invalid args.");
			}
		}
		
		else if(args[0].equalsIgnoreCase("leave")) {
			PlayerGroup.members.clear();
			PlayerGroup.members.add(Minecraft.getMinecraft().player.getDisplayNameString());
			Config.save();
			Bartender.msg("You left the group.");
		}
		
		else if(args[0].equalsIgnoreCase("toggle")) {
			if(args.length==2) {
				if(args[1].equalsIgnoreCase("tpa")) {
					PlayerGroup.groupAcceptTpa = true;
					PlayerGroup.groupAcceptTpaHere = false;
					GuiConfig.save();
					
				}
				else if(args[1].equalsIgnoreCase("here")) {
					PlayerGroup.groupAcceptTpa = false;
					PlayerGroup.groupAcceptTpaHere = true;
					GuiConfig.save();
					
				}
				else if(args[1].equalsIgnoreCase("both")) {
					PlayerGroup.groupAcceptTpaHere = !PlayerGroup.groupAcceptTpaHere;
					PlayerGroup.groupAcceptTpa = !PlayerGroup.groupAcceptTpa;
					GuiConfig.save();
					
					
				}
				else if(args[1].equalsIgnoreCase("off")) {
					PlayerGroup.groupAcceptTpaHere = false;
					PlayerGroup.groupAcceptTpa = false;
					GuiConfig.save();
					
					
				}
				else if(args[1].equalsIgnoreCase("on")) {
					PlayerGroup.groupAcceptTpaHere = true;
					PlayerGroup.groupAcceptTpa = true;
					Config.save();
					
					
				}
				else Bartender.msg("Invalid args.");
			}
			else {
				Bartender.msg("Invalid args.");
			}
	
		}
		
		else if(args[0].equalsIgnoreCase("here")) {
			
			if(PlayerGroup.members.isEmpty()) {
				Bartender.msg("Your group is empty.");
				return;
			}
				
			EntityPlayerSP player = Minecraft.getMinecraft().player;
			ArrayList<String> members = PlayerGroup.members;
			int successCount = -1;
			for (int i = 0, membersSize = members.size(); i < membersSize; i++) {
				String member = members.get(i);
				
				if (member.equalsIgnoreCase(player.getDisplayNameString())) continue;
				
				if (!PlayerGroup.isPlayerOnline(member)) continue;
				successCount++;
				
				Timer timer = new Timer();
				timer.schedule(new TimerTask()
				{
					public void run()
					{
						player.sendChatMessage("/tpahere " + member);
						timer.cancel();
					}
				}, successCount*10000);
			}
		}
		
		else if(args[0].equalsIgnoreCase("main")) {
			if(args.length==2) {
				PlayerGroup.setMainAcc(args[1]);
			}
			else {
				Bartender.msg("Invalid args.");
			}
		}
		
		else {
			Bartender.msg("Usage: " + getUsage(sender));
		}
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return true;
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
		return Collections.emptyList();
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index) {
		return false;
	}
	
	private TextComponentTranslation format(TextFormatting color, String str, Object... args)
    {
        TextComponentTranslation ret = new TextComponentTranslation(str, args);
        ret.getStyle().setColor(color);
        return ret;
    }
}