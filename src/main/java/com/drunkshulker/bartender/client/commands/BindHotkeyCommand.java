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

public class BindHotkeyCommand implements ICommand {

	@Override
	public int compareTo(ICommand arg0) {
		return 0;
	}

	@Override
	public String getName() {
		return "bind";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/bind <command_or_message> <slot>";
	}

	@Override
	public List<String> getAliases() {
		List<String> aliases = Lists.<String>newArrayList();
		aliases.add("/bind");
		return aliases;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		
		
		
		
		
		
		
		if(args==null||args.length<2) {
			Bartender.msg("Invalid args");
			return;
		}
		
		
		int slot;
		try {
			slot = Integer.parseInt(args[1]);
		}
		catch(Exception e) {
			Bartender.msg("Invalid args");
			return;
		}
		
		slot--;
		if(slot>=0&&slot<=8) {
			Config.HOTKEY_COMMANDS[slot] = args[0].replace("_", " ");
		}
		else {
			Bartender.msg("Invalid slot. Available slots are: 1, 2, 3, 4, 5, 6, 7, 8 and 9");
			return;
		}

		Bartender.msg(args[0].replace("_", " ") + " is now binded to hotkey " + (slot+1));
		Config.save();
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
	
	private TextComponentTranslation format(TextFormatting color, String str, Object... args) {
        TextComponentTranslation ret = new TextComponentTranslation(str, args);
        ret.getStyle().setColor(color);
        return ret;
    }
}