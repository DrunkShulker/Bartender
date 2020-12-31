package com.drunkshulker.bartender.client.commands;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import com.drunkshulker.bartender.Bartender;
import com.drunkshulker.bartender.client.gui.overlaygui.OverlayGui;
import com.drunkshulker.bartender.client.social.PlayerFriends;
import com.google.common.collect.Lists;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class HudCommand implements ICommand {

    @Override
    public int compareTo(ICommand arg0) {
        return 0;
    }

    @Override
    public String getName() {
        return "hud";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/hud <action> <target> <direction> <value>";
    }

    @Override
    public List<String> getAliases() {
        List<String> aliases = Lists.<String>newArrayList();
        aliases.add("/hud");
        return aliases;
    }

    static public final List<String> targets = new ArrayList<>(Arrays.asList("actions","armor","watermark", "group", "players", "inventory", "coords" , "potions", "status", "target", "numbers"));

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

        
        if (args == null || args.length == 0) {
            Bartender.msg("Please provide args.");
        }
        
        else if (args[0].equalsIgnoreCase("reset")) {
            OverlayGui.resetLayout();
            Bartender.msg("Hud reset.");
        }
        
        else if (args[0].equalsIgnoreCase("help")) {
            Bartender.msg("Usage: "+getUsage(sender));
            Bartender.msg("Use TAB key to autocomplete the options.");
            Bartender.msg("Use '/hud reset' to reset everything back to normal.");
        }
        
        else if (args[0].equalsIgnoreCase("move")) {
            if (args.length == 4) {
                if (!targetExists(args[1])) {
                    Bartender.msg("Invalid move target '"+args[1]+"'");
                    return;
                }
                if (args[2] == null || (dirToInt(args[2])[0] == 0 && dirToInt(args[2])[1] == 0)) {
                    Bartender.msg("Invalid move direction.");
                    return;
                }
                int moveAmount;
                try {
                    moveAmount = Integer.parseInt(args[3]);
                }
                catch(Exception e) {
                    Bartender.msg("Invalid args. Usage: " + getUsage(sender));
                    return;
                }
                int[] direction = dirToInt(args[2]);
                OverlayGui.moveTarget(args[1], moveAmount*direction[0],moveAmount*direction[1]);
            } else {
                Bartender.msg("Invalid args. Usage: " + getUsage(sender));
            }
        } else {
           Bartender.msg("Invalid args. Usage: " + getUsage(sender));
        }
    }

    boolean targetExists(String target) {
        if (target == null) return false;
        for (String s : targets) {
            if(s.equals(target)) return true;
        }
        return false;
    }

    int[] dirToInt(String dir) {
        switch (dir) {
            case "up":
                return new int[]{0, -1};
            case "down":
                return new int[]{0, 1};
            case "left":
                return new int[]{-1, 0};
            case "right":
                return new int[]{1, 0};
        }
        return new int[]{0, 0};
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        if(args.length==3) return new ArrayList<String>(Arrays.asList("left","right","up", "down"));
        if(args.length==2) return new ArrayList<>(targets);
        if(args.length==1) return new ArrayList<String>(Arrays.asList("move","reset","help"));
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