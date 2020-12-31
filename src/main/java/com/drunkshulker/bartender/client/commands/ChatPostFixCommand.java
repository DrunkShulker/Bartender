package com.drunkshulker.bartender.client.commands;

import java.util.Collections;
import java.util.List;

import com.drunkshulker.bartender.Bartender;
import com.drunkshulker.bartender.util.Config;
import com.google.common.collect.Lists;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class ChatPostFixCommand implements ICommand {

	@Override
	public int compareTo(ICommand arg0) {
		return 0;
	}

	@Override
	public String getName() {
		return "chatfix";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/chatfix <action> <value>";
	}

	@Override
	public List<String> getAliases() {
		List<String> aliases = Lists.<String>newArrayList();
		aliases.add("/chatfix");
		return aliases;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {		
		
		
		
		
		if(args==null||args.length==0) {
			Bartender.msg("Current chatfix is: "+Config.CHAT_POST_FIX);
		}
		
		else if(args[0].equalsIgnoreCase("set")) {
			if(args.length==2) {
				Config.CHAT_POST_FIX = args[1];
				Config.save();
				Bartender.msg("Chatfix set: "+Config.CHAT_POST_FIX);
			}
			else {
				Bartender.msg("Invalid args.");
			}
		}
		
		else if(args[0].equalsIgnoreCase("clear")) {	
			Config.CHAT_POST_FIX = "";
			Config.save();
			Bartender.msg("Chatfix cleared. ");
		}
		else if(args[0].equalsIgnoreCase("default")) {	
			Config.CHAT_POST_FIX = Config.getDefaultChatPostFix();
			Config.save();
			Bartender.msg("Default chatfix set. ");
		}
		
		else {
			Bartender.msg("Invalid args.");
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