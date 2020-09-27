package com.drunkshulker.bartender.client.gui.clickgui;

import java.util.ArrayList;

import com.drunkshulker.bartender.client.gui.GuiConfig;
import com.drunkshulker.bartender.client.gui.overlaygui.OverlayGui;
import com.drunkshulker.bartender.client.module.BaseFinder;
import com.drunkshulker.bartender.util.Config;
import com.drunkshulker.bartender.util.Preferences;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

public class ClickGuiSetting {

	enum SettingType {
			CLICK, 
			CLICK_COMMAND, 
			TEXT, 
	}
	
	public String title;
	public String[] desc;
	public SettingType type;
	public int value;
	public JsonArray values;
	public boolean closeOnClick;
	public int keyBind = 0;
	
	
	public int renderMinX=0, renderMinY=0, renderMaxX=0, renderMaxY=0;
	
	public String panelTitle;
	
	public static ClickGuiSetting[] settingsFromJson(JsonArray json, String pTitle) {
		ArrayList<ClickGuiSetting> settings = new ArrayList<ClickGuiSetting>();
		
		json.forEach((elem) ->
	    {
	        if (elem.isJsonObject())
	        {
	        	settings.add(settingFromJson(elem.getAsJsonObject(), pTitle));
	        }
	    });
		
		ClickGuiSetting[] i = new ClickGuiSetting[settings.size()];
        return settings.toArray(i);
	}

	public static String getBindingCode(ClickGuiSetting setting, String title) {
		if(setting.type == SettingType.CLICK_COMMAND) return null;
		return title+"->"+setting.title;
	}


	private static ClickGuiSetting settingFromJson(JsonObject json, String pTitle) {
		ClickGuiSetting setting = new ClickGuiSetting();
		setting.title = json.get("title").getAsString();
		setting.panelTitle = pTitle;
		ArrayList<String> descs = new ArrayList<String>();	
		json.get("desc").getAsJsonArray().forEach((elem) ->
	    {  
	        descs.add(elem.getAsString());  
	    });
		String[] i = new String[descs.size()];
		setting.desc = descs.toArray(i);

		switch (json.get("type").getAsString()) {
		case "text":
			setting.type = SettingType.TEXT;
			if(json.has("bind")){
				setting.keyBind = json.get("bind").getAsInt();
				GuiConfig.guiBinds.put(pTitle+"->"+setting.title, setting.keyBind);
			}
			break;
		case "click":	
			setting.type = SettingType.CLICK;
			setting.closeOnClick = json.get("closeOnClick").getAsBoolean();
			if(json.has("bind")){
				setting.keyBind = json.get("bind").getAsInt();
				GuiConfig.guiBinds.put(pTitle+"->"+setting.title, setting.keyBind);
			}
			break;
		case "clickCommand":	
			setting.type = SettingType.CLICK_COMMAND;
			setting.closeOnClick = json.get("closeOnClick").getAsBoolean();
			break;
		default:		
			break;
		}

		if(setting.type==SettingType.TEXT) {
			setting.value = json.get("value").getAsInt();
			setting.values = json.get("values").getAsJsonArray();
		}
		
		return setting;
	}

	public JsonObject toJson() {
		JsonObject obj = new JsonObject();
		
		obj.addProperty("title", title);
		obj.addProperty("bind", keyBind);

		JsonArray descs = new JsonArray();
		for (String s : desc) {
			descs.add(s);
		}
		obj.add("desc", descs);
		
		switch (type) {
		case TEXT:
			obj.addProperty("type", "text");
			obj.addProperty("value", value);
			obj.add("values", values);
			break;
		case CLICK:
			obj.addProperty("type", "click");
			obj.addProperty("closeOnClick", closeOnClick);
			break;
		case CLICK_COMMAND:
			obj.addProperty("type", "clickCommand");
			obj.addProperty("closeOnClick", closeOnClick);
			break;
		default:
			break;
		}
		
		return obj;
	}

	public static void handleClick(ClickGuiSetting setting, boolean middleClick) {
		if(setting==null) return;
		switch (setting.type) {
		case TEXT:
			
			
			if(BaseFinder.enabled&&setting.panelTitle.equals("base finder")&&setting.title.equals("travel")){
				ClickGuiSetting.handleClick(ClickGuiSetting.fromString("base finder->state"), false);
			}
			
			if(middleClick){
				if(setting.value<=0) setting.value = setting.values.size()-1;
				else setting.value--;
			}else {
				if(setting.value>=setting.values.size()-1) setting.value = 0;
				else setting.value++;
			}
			
			Preferences.apply();
			break;
		case CLICK:
			
			if(!middleClick) Preferences.execute(setting);

			break;
		case CLICK_COMMAND:
			if(!middleClick) Minecraft.getMinecraft().player.sendChatMessage(Config.HOTKEY_COMMANDS[Integer.parseInt(setting.title)]);

			break;
		default:
			System.out.println("handleClick() unexpected default case!");
			return;
		}

		if(setting.type==SettingType.TEXT) {
			OverlayGui.lastGuiAction = setting.panelTitle+"->"+setting.title+": "+setting.values.get(setting.value).getAsString();
			OverlayGui.lastGuiActionStamp = System.currentTimeMillis();
		} else {
			OverlayGui.lastGuiAction = setting.panelTitle+"->"+setting.title;
			OverlayGui.lastGuiActionStamp = System.currentTimeMillis();
		}

		if(setting.type!=SettingType.TEXT){
			
			if(setting.closeOnClick&&!middleClick) {
				Minecraft.getMinecraft().displayGuiScreen((GuiScreen)null);
			}
		}

		
		GuiConfig.save();
	}

	public static ClickGuiSetting fromString(String key) {
		String panelName, settingName;
		String[] l = key.split("->");
		panelName = l[0];
		settingName = l[1];

		for (ClickGuiPanel panel:ClickGui.panels) {
			if(panelName.equals(panel.title)){
				for (ClickGuiSetting setting:panel.contents) {
					if(setting.title.equals(settingName)){
						return setting;
					}
				}
			}
		}
		return null;
	}

}
