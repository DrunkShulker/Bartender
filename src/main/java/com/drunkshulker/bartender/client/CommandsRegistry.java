package com.drunkshulker.bartender.client;

import com.drunkshulker.bartender.client.commands.*;

import net.minecraftforge.client.ClientCommandHandler;

public class CommandsRegistry {
	
	public static void registerAll() {
		ClientCommandHandler.instance.registerCommand(new DateCommand());
        ClientCommandHandler.instance.registerCommand(new BindHotkeyCommand());
        ClientCommandHandler.instance.registerCommand(new GroupCommand());
        ClientCommandHandler.instance.registerCommand(new FriendsCommand());
        ClientCommandHandler.instance.registerCommand(new ChatPostFixCommand());
        ClientCommandHandler.instance.registerCommand(new BasefinderCommand());
        ClientCommandHandler.instance.registerCommand(new GuiCommand());
	}
}
