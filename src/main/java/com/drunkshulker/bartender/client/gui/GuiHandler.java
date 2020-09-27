package com.drunkshulker.bartender.client.gui;

import com.drunkshulker.bartender.client.gui.clickgui.ClickGui;
import com.drunkshulker.bartender.client.gui.clickgui.ClickGuiSetting;
import com.drunkshulker.bartender.client.gui.clickgui.theme.*;
import com.drunkshulker.bartender.client.gui.overlaygui.MainMenuOverlayGui;
import com.drunkshulker.bartender.client.gui.overlaygui.OverlayGui;
import com.drunkshulker.bartender.client.gui.overlaygui.PauseOverlayGui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class GuiHandler {
	
	public static boolean showAP = false;
	public static boolean showHP = false;
	public static boolean showSafetotem = true;
	public static boolean showBinds = true;
	public static boolean showGroup = true;
	public static boolean showDimmed = true;
	public static boolean showPlayer = true;
	public static boolean menuWaterMark = true;
	public static boolean ingameWaterMark = true;
	public static boolean showTooltips = true;
	public static boolean txtHpAndFood = false;
	public static boolean showTargetListing = true;
	public static boolean showBindInfo = true;

	public static int currentTheme = 0;

	public static GuiTheme[] themes = {
			new GuiThemeDrunk(),
			new GuiThemeFaraday(),
			new GuiThemeInfinitum(),
			new GuiThemeDesertBunny(),
			new GuiThemeBait()
	};

	@SubscribeEvent public void onRenderGui(RenderGameOverlayEvent.Post event){
		Minecraft mc = Minecraft.getMinecraft();
		if (event.getType() != ElementType.EXPERIENCE) return;
		new OverlayGui(mc);
	}
	
	@SubscribeEvent
    public void onRenderGui(RenderGameOverlayEvent event)
    {	
		Minecraft mc = Minecraft.getMinecraft();
		
		if (event.getType() == RenderGameOverlayEvent.ElementType.ARMOR) {
			if(!showAP)
			event.setCanceled(true);
		}
		
		else if (event.getType() == RenderGameOverlayEvent.ElementType.HEALTH) {
			if(mc.player.getHealth()==20&&!showHP)
			event.setCanceled(true);
		}
    }
	
	@SubscribeEvent
	public void onScreenDrawing(DrawScreenEvent.Post event)
	{
	    if (event.getGui() instanceof GuiMainMenu)
	    {
	    	Minecraft mc = Minecraft.getMinecraft();
	        new MainMenuOverlayGui(mc);
	    }
	    else if (event.getGui() instanceof GuiIngameMenu)
	    {
	    	Minecraft mc = Minecraft.getMinecraft();
	        new PauseOverlayGui(mc);
	    }
	}

	public static void clickAction(String action) {
		switch (action) {
		case "reset layout":
			ClickGui.resetLayout();
			break;

		default:
			break;
		}
	}

	public static void applyPreferences(ClickGuiSetting[] contents) {
		for (ClickGuiSetting setting : contents) {
			switch (setting.title) {
			case "AP overlay":
				showAP = setting.value == 1;
				break;
			case "HP overlay":
				showHP = setting.value == 1;			
				break;
			case "theme":
				currentTheme = setting.value;
				break;
			case "keybinds":
				showBinds = setting.value==0;
				break;
			case "target list":
				showTargetListing = setting.value == 1;
				if(!showTargetListing&&OverlayGui.targetGuiActive) OverlayGui.targetGUIToggle();
				break;
			case "group":
				showGroup = setting.value==0;
				break;
			case "draw player":
				showPlayer = setting.value==0;
				break;
			case "safe totem":
				showSafetotem = setting.value==1;
				break;
			case "tooltips":
				showTooltips = setting.value==0;
				break;
			case "numbers":
				txtHpAndFood = setting.value==1;
				break;
			case "dimmed":
				showDimmed = setting.value==0;
			case "bind info":
				showBindInfo = setting.value==0;
			case "watermark":
				String val = setting.values.get(setting.value).getAsString();
				if(val.equals("show")) {
					ingameWaterMark = true;
					menuWaterMark = true;
				}
				else if(val.equals("ingame")) {
					ingameWaterMark = true;
					menuWaterMark = false;
				}
				else if(val.equals("menu")) {
					ingameWaterMark = false;
					menuWaterMark = true;
				}
				else  {
					ingameWaterMark = false;
					menuWaterMark = false;
				}
				break;
			default:
				break;
			}
		}	
	}
}
