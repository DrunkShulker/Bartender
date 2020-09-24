package com.drunkshulker.bartender.client.input;

import com.drunkshulker.bartender.client.gui.GuiConfig;
import com.drunkshulker.bartender.client.gui.clickgui.ClickGui;
import com.drunkshulker.bartender.client.gui.clickgui.ClickGuiSetting;
import com.drunkshulker.bartender.client.gui.overlaygui.OverlayGui;
import com.drunkshulker.bartender.client.module.BaseFinder;
import com.drunkshulker.bartender.client.module.Bodyguard;
import com.drunkshulker.bartender.util.Config;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

import java.util.HashMap;
import java.util.Map;

public class KeyInputHandler
{
	public static boolean guiMouseHold = false;

	private final Minecraft mc = Minecraft.getMinecraft();

	@SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {

		for(Map.Entry<String, Integer> entry : GuiConfig.guiBinds.entrySet()) {
			String key = entry.getKey();
			Integer value = entry.getValue();

			if(Keyboard.isKeyDown(value)){
				ClickGuiSetting.handleClick(ClickGuiSetting.fromString(key), false);
			}
		}

		if(Keybinds.enemyGuiMark.isPressed()) {
			OverlayGui.targetConfirm();
			return;
		}
		if(Keybinds.enemyGuiNavigateDown.isPressed()) {
			OverlayGui.targetSelect(1);
			return;
		}
		if(Keybinds.enemyGuiNavigateUp.isPressed()) {
			OverlayGui.targetSelect(-1);
			return;
		}

		if(Keybinds.toggleEnemyGui.isPressed()) {
			OverlayGui.targetGUIToggle();
			return;
		}

		if(Keybinds.baseFinderToggle.isPressed()) {
			BaseFinder.pauseOrContinue();
			return;
		}

		if(Keybinds.toggleGui.isPressed()) {
			mc.displayGuiScreen(new ClickGui());
			return;
		}

		if(Keybinds.chorus.isPressed()) {
			Bodyguard.eatChorus(true);
			return;
		}

		if(Keybinds.takeoff.isPressed()) {
			Bodyguard.takeOff();
			return;
		}

		if(Keybinds.standHere.isPressed()) {
			Bodyguard.sendGoToCommand();
			return;
		}

		if(Keybinds.endTasks.isPressed()) {
			Bodyguard.endAllTasks(true);
			return;
		}

		for (int i = 0; i < 9; i++) {
			if(Keybinds.hotkeyCommand[i].isPressed())
	        {
	        	mc.player.sendChatMessage(Config.HOTKEY_COMMANDS[i]);
	        	System.out.println("Execute hotkey command: "+Config.HOTKEY_COMMANDS[i]);
	        	break;
	        }
		}
    }
}