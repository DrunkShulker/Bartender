package com.drunkshulker.bartender.client.commands;

import java.util.Collections;
import java.util.List;

import com.drunkshulker.bartender.Bartender;
import com.drunkshulker.bartender.client.social.PlayerFriends;
import com.drunkshulker.bartender.client.social.PlayerGroup;
import com.drunkshulker.bartender.util.Config;
import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class FriendsCommand implements ICommand {

	@Override
	public int compareTo(ICommand arg0) {
		return 0;
	}

	@Override
	public String getName() {
		return "friend";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/friend <action> <player>";
	}

	@Override
	public List<String> getAliases() {
		List<String> aliases = Lists.<String>newArrayList();
		aliases.add("/friend");
		return aliases;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {		
		
		
		
		
		if(args==null||args.length==0) {
			Bartender.msg( (PlayerFriends.toJsonArray().size()==0)
					? "You have no friends."
					:PlayerFriends.toJsonArray().toString());
		}
		
		else if(args[0].equalsIgnoreCase("add")) {
			if(args.length==2) {
				if(args[1].contains("~")) Bartender.msg("Please use the player's real name, not /nick.");
				else PlayerFriends.addFriend(args[1]);
			}
			else {
				Bartender.msg("Invalid args.");
			}
		}
		
		else if(args[0].equalsIgnoreCase("remove")) {
			if(args.length==2) {
				PlayerFriends.removeFriend(args[1]);
			}
			else {
				Bartender.msg("Invalid args.");
			}
		}
		
		else if(args[0].equalsIgnoreCase("refresh")) {
			PlayerFriends.loadImpactFriends();
			Bartender.msg("Impact friends loaded:" + PlayerFriends.impactFriends.toString());
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
	
	private TextComponentTranslation format(TextFormatting color, String str, Object... args){
        TextComponentTranslation ret = new TextComponentTranslation(str, args);
        ret.getStyle().setColor(color);
        return ret;
    }
}