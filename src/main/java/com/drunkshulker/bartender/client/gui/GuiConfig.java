package com.drunkshulker.bartender.client.gui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.drunkshulker.bartender.Bartender;
import com.drunkshulker.bartender.client.gui.clickgui.ClickGui;
import com.drunkshulker.bartender.client.gui.clickgui.ClickGuiPanel;
import com.drunkshulker.bartender.client.gui.clickgui.ClickGuiSetting;
import com.drunkshulker.bartender.client.input.Keybinds;
import com.drunkshulker.bartender.util.AssetLoader;
import com.drunkshulker.bartender.util.Config;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class GuiConfig {
	
	public static JsonObject config;
	final static String FILENAME = "bartender-gui.json";
	public static boolean usingDefaultConfig = false;

	public static HashMap<String, Integer> guiBinds;

	public static void save() {
		
		config = new JsonObject();
		config.addProperty("bartender_version", Bartender.VERSION);
		
		
		JsonArray panelsJson = new JsonArray();
		for (ClickGuiPanel panel : ClickGui.panels) {
			panelsJson.add(panel.toJson());
		}
		config.add("click_gui", panelsJson);
		
		
		Gson gsonBuilder = new GsonBuilder().setPrettyPrinting().create();
		String json = gsonBuilder.toJson(config).replace("\\\"", "");
		try {
			Config.writeFile(Bartender.BARTENDER_DIR+"/"+FILENAME, json);
			System.out.println("Saved GUI config.");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		usingDefaultConfig = false;
	}
	
	public static void defaults() {
		System.out.println("Using default GUI config.");
		config = new AssetLoader().loadJson("bartender-gui-default.json");
		System.out.println("Default GUI config loaded");
		usingDefaultConfig = true;
	}
	
	public static void load() {
		if(guiBinds==null) guiBinds = new HashMap<>();
		guiBinds.clear();

		File f = new File(Bartender.BARTENDER_DIR+"/"+FILENAME);
		
		if(Config.DEBUG_IGNORE_SAVES) {
			
			System.out.println("DEBUG: ignoring bartender-gui.json");
			defaults();
			return;
		}
		else if(f.exists() && !f.isDirectory())
		{ 
		    
			final String json = Config.readFile(f.getAbsolutePath()).replace("\\\"", "");
			JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();
			
			
			String configVersion = jsonObject.get("bartender_version").getAsString();
			if(!configVersion.equals(Bartender.VERSION)) {
				
				System.out.println("GuiConfig file was from an older version, using default GUI config!");
				defaults();
				
			}
			else {
				config = jsonObject;
				System.out.println("GUI config loaded.");
			}
			
		}
		else {
			defaults();
		}
		
		
		ClickGui.panels = GuiConfig.getPanels();
	}

	public static ClickGuiPanel[] getPanels() {	
		if(config==null) load();
		
		JsonArray array = config.get("click_gui").getAsJsonArray();
		List<ClickGuiPanel> temp = new ArrayList<ClickGuiPanel>();
		
		array.forEach((elem) ->
	    {
	        if (elem.isJsonObject())
	        {
	        	final JsonObject obj = elem.getAsJsonObject();
	        	temp.add(ClickGuiPanel.fromJson(obj));
	        }
	    });
		
		ClickGuiPanel[] itemsArray = new ClickGuiPanel[temp.size()];
        return temp.toArray(itemsArray);
	}

	public static JsonObject getPanelByName(String panelName) {
		if(config==null) load();

		JsonArray array = config.get("click_gui").getAsJsonArray();
		List<JsonObject> temp = new ArrayList<>();

		array.forEach((elem) ->
		{
			if (elem.isJsonObject())
			{
				if(elem.getAsJsonObject().get("name").getAsString().equals(panelName)) {
					temp.add(elem.getAsJsonObject());

				}
			}
		});
		if(temp.isEmpty())return null;
		else return temp.get(0);
	}

	public static void bindKey(String currentEditingBind, int keyCode) {
		if(config==null) load();

		GuiConfig.guiBinds.remove(currentEditingBind);
		GuiConfig.guiBinds.put(currentEditingBind, keyCode);

		String panelName;
		String[] l = currentEditingBind.split("->");
		panelName = l[0];

		JsonArray array = config.get("click_gui").getAsJsonArray();
		JsonArray temp = new JsonArray();

		ClickGuiSetting s = ClickGuiSetting.fromString(currentEditingBind);
		if(s!=null) s.keyBind = keyCode;

		array.forEach((elem) ->
		{
			if (elem.isJsonObject())
			{
				if(elem.getAsJsonObject().get("name").getAsString().equals(panelName)) {
					elem.getAsJsonObject().addProperty("bind",keyCode);
				}
			}
			temp.add(elem.getAsJsonObject());
		});

		config.add("click_gui",temp);
		save();
	}

	public static JsonObject getSettingByName(JsonArray array, String name) {
		List<JsonObject> temp = new ArrayList<>();

		array.forEach((elem) ->
		{
			if (elem.isJsonObject())
			{
				if(elem.getAsJsonObject().get("title").getAsString().equals(name)) {
					temp.add(elem.getAsJsonObject());
				}
			}
		});
		if(temp.isEmpty())return null;
		else return temp.get(0);
	}
}
