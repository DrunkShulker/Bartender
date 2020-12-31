package com.drunkshulker.bartender.client.commands;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.drunkshulker.bartender.Bartender;
import com.drunkshulker.bartender.client.module.BaseFinder;
import com.drunkshulker.bartender.client.module.BaseFinder.FinderTask;
import com.drunkshulker.bartender.util.Config;
import com.google.common.collect.Lists;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class BasefinderCommand implements ICommand {

	@Override
	public int compareTo(ICommand arg0) {
		return 0;
	}

	@Override
	public String getName() {
		return "bf";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/bf <action> <coordX> <coordZ>";
	}

	@Override
	public List<String> getAliases() {
		List<String> aliases = Lists.<String>newArrayList();
		aliases.add("/bf");
		return aliases;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {		
		
		if(args==null||args.length<1) {
			Bartender.msg("Invalid args");
			return;
		}
		if(args[0].equalsIgnoreCase("goal")) {
			if(args.length==3) {
				int x=0,z=0;
				try {
					x = Integer.parseInt(args[1]);
					z = Integer.parseInt(args[2]);
				} catch (Exception e) {Bartender.msg("Invalid args"); return;}
				
				BaseFinder.setCustomGoal(x,z);
			}
			else Bartender.msg("Invalid args");		
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