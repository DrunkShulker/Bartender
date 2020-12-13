package com.drunkshulker.bartender.client.commands;

import com.drunkshulker.bartender.client.module.Cosmetic;
import com.drunkshulker.bartender.util.Config;
import com.google.common.collect.Lists;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

import java.util.Collections;
import java.util.List;

public class HatCommand implements ICommand {

	@Override
	public int compareTo(ICommand arg0) {
		return 0;
	}

	@Override
	public String getName() {
		return "sethat";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/sethat";
	}

	@Override
	public List<String> getAliases() {
		List<String> aliases = Lists.<String>newArrayList();
		aliases.add("/sethat");
		return aliases;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {		

		if(args.length == 0){
			sender.sendMessage(format(TextFormatting.DARK_RED, "Hat set failed. No item provided"));
			return;
		}

		Item item = Item.getByNameOrId(args[0]);
		if(item == null || item == Items.AIR){
			sender.sendMessage(format(TextFormatting.DARK_RED, "Hat set failed. Unknown item."));
			return;
		}

		Cosmetic.currentHat = item;
		sender.sendMessage(format(TextFormatting.YELLOW, "Hat set success."));
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
	
	private TextComponentTranslation format(TextFormatting color, String str, Object... args)
    {
        TextComponentTranslation ret = new TextComponentTranslation(str, args);
        ret.getStyle().setColor(color);
        return ret;
    }
}