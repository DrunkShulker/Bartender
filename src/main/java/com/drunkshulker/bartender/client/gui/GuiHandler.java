package com.drunkshulker.bartender.client.gui;

import com.drunkshulker.bartender.Bartender;
import com.drunkshulker.bartender.client.gui.clickgui.ClickGui;
import com.drunkshulker.bartender.client.gui.clickgui.ClickGuiSetting;
import com.drunkshulker.bartender.client.gui.clickgui.theme.*;
import com.drunkshulker.bartender.client.gui.overlaygui.MainMenuOverlayGui;
import com.drunkshulker.bartender.client.gui.overlaygui.OverlayGui;
import com.drunkshulker.bartender.client.gui.overlaygui.PauseOverlayGui;

import com.drunkshulker.bartender.client.module.Dupe;
import com.drunkshulker.bartender.client.module.TotemPopCounter;
import com.drunkshulker.bartender.util.forge.ForgeLoadingScreen;
import com.drunkshulker.bartender.util.salhack.events.render.EventRenderGameOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.gui.recipebook.GuiButtonRecipeTab;
import net.minecraft.client.gui.recipebook.GuiRecipeBook;
import net.minecraft.client.gui.recipebook.GuiRecipeOverlay;
import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.IOException;

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
	public static boolean showPotionIcons = false;
	public static boolean showTargetListing = true;
	public static boolean showBindInfo = true;
	public static boolean showInventory = true;
	public static boolean showCoords = true;
	public static boolean showPotions = true;
	public static boolean snapToGrid = true;

	long lastDupeClickStamp = System.currentTimeMillis();
	final long dupeClickIntervalMillis = 800;

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
		Bartender.EVENT_BUS.post(new EventRenderGameOverlay(event.getPartialTicks(), event.getResolution()));
	}
	
	@SubscribeEvent
    public void onRenderGui(RenderGameOverlayEvent event)
    {	
		Minecraft mc = Minecraft.getMinecraft();
		
		if (event.getType() == RenderGameOverlayEvent.ElementType.ARMOR) {
			if(!showAP)
			event.setCanceled(true);
		}

		if (event.getType() == ElementType.POTION_ICONS) {
			if(!showPotionIcons)
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
		Minecraft mc = Minecraft.getMinecraft();
	    if (event.getGui() instanceof GuiMainMenu)
	    {
	        new MainMenuOverlayGui(mc);
	    }
	    else if (event.getGui() instanceof GuiIngameMenu)
	    {
	        new PauseOverlayGui(mc);
	    }
	    else if(event.getGui() instanceof GuiInventory){
	    	
			
			
			
			
			if(Dupe.currentWaitPhase!=Dupe.WaitPhase.WAIT_PICKUP) return;
			if(System.currentTimeMillis()-lastDupeClickStamp<dupeClickIntervalMillis) return;
			lastDupeClickStamp = System.currentTimeMillis();
			lastDupeClickStamp = System.currentTimeMillis();

			GuiRecipeBook recipeBook = ((GuiInventory) event.getGui()).func_194310_f();
			if(recipeBook.isVisible()) {
				int w = (event.getGui().width/2)-138;
				int h = (event.getGui().height/2)-40;
				recipeBook.mouseClicked(w,h,0);
			}

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
			case "pop counter":
				TotemPopCounter.enabled = setting.value == 1;
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
			case "inventory":
				showInventory = setting.value==1;
				break;
			case "safe totem":
				showSafetotem = setting.value==1;
				break;
			case "tooltips":
				showTooltips = setting.value==0;
				break;
			case "potion icons":
				showPotionIcons = setting.value==1;
				break;
			case "potions":
				showPotions = setting.value==1;
				break;
			case "coords":
				showCoords = setting.value==1;
				break;
			case "numbers":
				txtHpAndFood = setting.value==1;
				break;
			case "snap":
				snapToGrid = setting.value==1;
				break;
			case "dimmed":
				showDimmed = setting.value==0;
			case "bind info":
				showBindInfo = setting.value==0;
				break;
			case "forge screen":
				ForgeLoadingScreen.modify(setting.value==1);
				break;
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
