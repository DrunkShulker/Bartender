package com.drunkshulker.bartender.client.input;

import org.lwjgl.input.Keyboard;

import com.drunkshulker.bartender.Bartender;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class Keybinds
{
    public static KeyBinding toggleGui, toggleEnemyGui;
    public static KeyBinding enemyGuiNavigateUp, enemyGuiNavigateDown, enemyGuiMark;
    public static KeyBinding baseFinderToggle, takeoff, chorus, standHere, endTasks;

    public static KeyBinding[] hotkeyCommand = new KeyBinding[9];
    public static final String[] HOTKEY_COMMAND_DEFAULTS = {
    		"/ec",
    		"/back",
    		"@sex",
    		"/tpdeny",
    		"/help",
    		"/tpaccept",
    		"!joke",
    		"!nword",
    		"!pt",
    };
    
    public static void register()
    {
    	toggleGui = new KeyBinding("Open GUI", Keyboard.KEY_RCONTROL, Bartender.NAME);
        ClientRegistry.registerKeyBinding(toggleGui);

        enemyGuiNavigateUp = new KeyBinding("Target up", Keyboard.KEY_UP, Bartender.NAME);
        ClientRegistry.registerKeyBinding(enemyGuiNavigateUp);

        enemyGuiNavigateDown = new KeyBinding("Target down", Keyboard.KEY_DOWN, Bartender.NAME);
        ClientRegistry.registerKeyBinding(enemyGuiNavigateDown);

        enemyGuiMark = new KeyBinding("Target confirm", Keyboard.KEY_RIGHT, Bartender.NAME);
        ClientRegistry.registerKeyBinding(enemyGuiMark);

        toggleEnemyGui = new KeyBinding("Target GUI focus", Keyboard.KEY_LEFT, Bartender.NAME);
        ClientRegistry.registerKeyBinding(toggleEnemyGui);

        baseFinderToggle = new KeyBinding("Basefinder pause", Keyboard.KEY_PAUSE, Bartender.NAME);
        ClientRegistry.registerKeyBinding(baseFinderToggle);

        takeoff= new KeyBinding("Takeoff command", Keyboard.KEY_PRIOR, Bartender.NAME);
        ClientRegistry.registerKeyBinding(takeoff);

        chorus = new KeyBinding("Chorus command", Keyboard.KEY_NEXT, Bartender.NAME);
        ClientRegistry.registerKeyBinding(chorus);

        standHere = new KeyBinding("Stand here command", Keyboard.KEY_NUMPADENTER, Bartender.NAME);
        ClientRegistry.registerKeyBinding(standHere);

        endTasks = new KeyBinding("End all tasks", Keyboard.KEY_END, Bartender.NAME);
        ClientRegistry.registerKeyBinding(endTasks);

        hotkeyCommand[0] = new KeyBinding("Command 1", Keyboard.KEY_NUMPAD1, Bartender.NAME);
        hotkeyCommand[1] = new KeyBinding("Command 2", Keyboard.KEY_NUMPAD2, Bartender.NAME);
        hotkeyCommand[2] = new KeyBinding("Command 3", Keyboard.KEY_NUMPAD3, Bartender.NAME);
        hotkeyCommand[3] = new KeyBinding("Command 4", Keyboard.KEY_NUMPAD4, Bartender.NAME);
        hotkeyCommand[4] = new KeyBinding("Command 5", Keyboard.KEY_NUMPAD5, Bartender.NAME);   
        hotkeyCommand[5] = new KeyBinding("Command 6", Keyboard.KEY_NUMPAD6, Bartender.NAME);
        hotkeyCommand[6] = new KeyBinding("Command 7", Keyboard.KEY_NUMPAD7, Bartender.NAME);
        hotkeyCommand[7] = new KeyBinding("Command 8", Keyboard.KEY_NUMPAD8, Bartender.NAME);
        hotkeyCommand[8] = new KeyBinding("Command 9", Keyboard.KEY_NUMPAD9, Bartender.NAME);
        
        for (int i = 0; i < 9; i++) {
        	ClientRegistry.registerKeyBinding(hotkeyCommand[i]);
		}
    }
}