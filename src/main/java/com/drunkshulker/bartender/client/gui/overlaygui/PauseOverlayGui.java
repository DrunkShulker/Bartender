package com.drunkshulker.bartender.client.gui.overlaygui;

import com.drunkshulker.bartender.client.gui.GuiHandler;
import com.drunkshulker.bartender.client.input.Keybinds;
import com.drunkshulker.bartender.client.module.SafeTotemSwap;
import com.drunkshulker.bartender.util.Config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

public class PauseOverlayGui extends GuiScreen
{
    public PauseOverlayGui(Minecraft mc)
    {
    	if(GuiHandler.showBinds) {
	    	drawString(mc.fontRenderer,
					"Binds:", 
					4, OverlayGui.groupListBottom+13, 
					Integer.parseInt("62A2C4", 16));
	    	
	        for (int i = 0; i < 9; i++) {
				drawString(mc.fontRenderer,Keybinds.hotkeyCommand[i].getDisplayName() + " | " + Config.HOTKEY_COMMANDS[i],
						4, OverlayGui.groupListBottom+27+(i*10),
						Integer.parseInt("FFFFFF", 16));
			}
        }
    }
}
