package com.drunkshulker.bartender.client.commands;

import com.drunkshulker.bartender.Bartender;
import com.drunkshulker.bartender.client.gui.GuiConfig;
import com.drunkshulker.bartender.client.gui.clickgui.ClickGui;
import com.drunkshulker.bartender.client.social.PlayerGroup;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class GuiCommand implements ICommand {

	@Override
	public int compareTo(ICommand arg0) {
		return 0;
	}

	@Override
	public String getName() {
		return "gui";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/gui <action>";
	}

	@Override
	public List<String> getAliases() {
		List<String> aliases = Lists.<String>newArrayList();
		aliases.add("/gui");
		return aliases;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		
		if(args==null||args.length==0) {
			sender.sendMessage(format(TextFormatting.RED, "<Bartender> Please provide args."));
		}
		
		else if(args[0].equalsIgnoreCase("import")) {
			if(Bartender.OFFER_IMPORTS){
				GuiConfig.importSettings();
			} else {
				sender.sendMessage(format(TextFormatting.RED, "<Bartender> No gui backup to import"));
			}
		}
		
		else if(args[0].equalsIgnoreCase("layout")) {
			sender.sendMessage(format(TextFormatting.YELLOW, "<Bartender> Gui layout reset"));
			ClickGui.resetLayout();
		}
		
		else if(args[0].equalsIgnoreCase("defaults")) {
			sender.sendMessage(format(TextFormatting.YELLOW, "<Bartender> All gui settings set to defaults"));
			GuiConfig.defaults();
			
			ClickGui.panels = GuiConfig.getPanels();
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