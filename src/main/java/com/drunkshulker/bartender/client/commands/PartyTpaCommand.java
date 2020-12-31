package com.drunkshulker.bartender.client.commands;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.drunkshulker.bartender.Bartender;
import com.drunkshulker.bartender.client.input.ChatObserver;
import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class PartyTpaCommand implements ICommand {

    @Override
    public int compareTo(ICommand arg0) {
        return 0;
    }

    @Override
    public String getName() {
        return "join";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/join";
    }

    @Override
    public List<String> getAliases() {
        List<String> aliases = Lists.<String>newArrayList();
        aliases.add("/join");
        return aliases;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(ChatObserver.lastPartyInvite.equals("")){
            Bartender.msg("No party TPA invites received");
            return;
        }
        Minecraft.getMinecraft().player.sendChatMessage("/tpa "+ChatObserver.lastPartyInvite);
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