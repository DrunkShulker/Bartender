package com.drunkshulker.bartender.util;

import com.drunkshulker.bartender.client.gui.GuiHandler;
import com.drunkshulker.bartender.client.gui.clickgui.ClickGui;
import com.drunkshulker.bartender.client.gui.clickgui.ClickGuiPanel;
import com.drunkshulker.bartender.client.gui.clickgui.ClickGuiSetting;
import com.drunkshulker.bartender.client.input.ChatObserver;
import com.drunkshulker.bartender.client.module.Aura;
import com.drunkshulker.bartender.client.module.AutoBuild;
import com.drunkshulker.bartender.client.module.AutoEat;
import com.drunkshulker.bartender.client.module.BaseFinder;
import com.drunkshulker.bartender.client.module.Bodyguard;
import com.drunkshulker.bartender.client.module.Flight;
import com.drunkshulker.bartender.client.module.PlayerParticles;
import com.drunkshulker.bartender.client.module.SafeTotemSwap;
import com.drunkshulker.bartender.client.social.PlayerGroup;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;

public class Preferences {
	
	public static void apply() {
		
		for (ClickGuiPanel panel : ClickGui.panels) {
			switch (panel.getTitle()) {
			case "particles":
				PlayerParticles.applyPreferences(panel.getContents());
				break;
			case "gui":
				GuiHandler.applyPreferences(panel.getContents());
				break;
			case "click command":
				
				break;
			case "chat":
				ChatObserver.applyPreferences(panel.getContents());
				break;
			case "auto build":
				AutoBuild.applyPreferences(panel.getContents());
				break;
			case "safe totem":
				SafeTotemSwap.applyPreferences(panel.getContents());
				break;
			case "group":
				PlayerGroup.applyPreferences(panel.getContents());
				break;
			case "bodyguard":
				Bodyguard.applyPreferences(panel.getContents());
				break;
			case "base finder":
				BaseFinder.applyPreferences(panel.getContents());
				break;
			case "aura":
				Aura.applyPreferences(panel.getContents());
				break;
			case "auto eat":
				AutoEat.applyPreferences(panel.getContents());
				break;
			case "flight":
				Flight.applyPreferences(panel.getContents());
				break;
			default:
				if(Minecraft.getMinecraft().player!=null)Minecraft.getMinecraft().player.sendMessage(new TextComponentString("<Bartender> ERROR PREFERENCE NOT HANDLED IN: "+panel.getTitle()));
				break;
			}
			
		}
	}
	
	
	public static void execute(ClickGuiSetting setting) {
		switch (setting.panelTitle) {
		case "gui":
			GuiHandler.clickAction(setting.title);
			break;
		case "auto build":
			AutoBuild.clickAction(setting.title);
			break;
		case "group":
			PlayerGroup.clickAction(setting.title);
			break;
		case "chat":
			ChatObserver.clickAction(setting.title);
			break;
		case "base finder":
			BaseFinder.clickAction(setting.title);
			break;
		default:
			break;
		}
	}

}
