package com.drunkshulker.bartender.util;

import com.drunkshulker.bartender.client.gui.GuiHandler;
import com.drunkshulker.bartender.client.gui.clickgui.ClickGui;
import com.drunkshulker.bartender.client.gui.clickgui.ClickGuiPanel;
import com.drunkshulker.bartender.client.gui.clickgui.ClickGuiSetting;
import com.drunkshulker.bartender.client.input.ChatObserver;
import com.drunkshulker.bartender.client.module.*;
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
			case "HUD":
				GuiHandler.applyPreferences(panel.getContents());
				break;
			case "click command":
				
				break;
			case "chat":
				ChatObserver.applyPreferences(panel.getContents());
				break;
			case "tracers":
				Tracers.applyPreferences(panel.getContents());
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
			case "utils":
				Sprint.applyPreferences(panel.getContents());
				Freecam.applyPreferences(panel.getContents());
				Velocity.applyPreferences(panel.getContents());
				AntiOverlay.applyPreferences(panel.getContents());
				Jesus.applyPreferences(panel.getContents());
				NoSlow.applyPreferences(panel.getContents());
				AutoTool.applyPreferences(panel.getContents());
				AntiHunger.applyPreferences(panel.getContents());
				HideArmor.applyPreferences(panel.getContents());
				FullBright.applyPreferences(panel.getContents());
				PeekPreview.applyPreferences(panel.getContents());
				Nametags.applyPreferences(panel.getContents());
				break;
			case "flight":
				FlightBot.applyPreferences(panel.getContents());
				break;
			case "dupe":
				Dupe.applyPreferences(panel.getContents());
				break;
			case "elytra+":
				ElytraFlight.applyPreferences(panel.getContents());
				break;
			case "new chunks":
				NewChunks.applyPreferences(panel.getContents());
				break;
			case "grief":
				AutoFire.applyPreferences(panel.getContents());
				Scaffold.applyPreferences(panel.getContents());
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
		case "new chunks":
			NewChunks.clickAction(setting.title);
			break;
		case "elytra+":
			ElytraFlight.clickAction(setting.title);
			break;
		case "chat":
			ChatObserver.clickAction(setting.title);
			break;
		case "base finder":
			BaseFinder.clickAction(setting.title);
			break;
		case "grief":
			AutoSpawn.clickAction(setting.title);
			break;
		case "dupe":
			Dupe.clickAction(setting.title);
			break;
		default:
			break;
		}
	}

}
