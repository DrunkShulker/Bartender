package com.drunkshulker.bartender.client.gui.clickgui;

import com.drunkshulker.bartender.client.gui.GuiConfig;
import com.drunkshulker.bartender.client.gui.clickgui.theme.GuiTheme;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.drunkshulker.bartender.client.gui.GuiHandler;
import com.drunkshulker.bartender.util.Config;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.client.Minecraft;

public class ClickGuiPanel extends ClickGui{
	
	
	int x = 0;
	int y = 0;
	
	
	int w = 120;
	int h = 17;
	
	
	int sx = 0;
	int sy = 0;
	
	String title;
	String[] desc;
	ClickGuiSetting[] contents;

	boolean expanded = false;

	public ClickGuiPanel(String id) {
		title = id;
	}
	
	public String getTitle(){
		return title;
	}	
	
	public ClickGuiSetting[] getContents() {
		return contents;
	}
	
	private Minecraft mc = Minecraft.getMinecraft();
	
	
	public boolean listen(int mouseX, int mouseY, boolean hovered, boolean altPressed, boolean shiftPressed) {
		mc = Minecraft.getMinecraft();
		if(ClickGui.currentDraggable==this||(ClickGui.drag&&mouseOver(mouseX, mouseY)&&ClickGui.currentDraggable==null)) {
			
			
			if(ClickGui.currentDraggable==null) {
				sx = x;
				sy = y;
				ClickGui.bringToFront(this);
			}
			
			
			x = sx+mouseX-ClickGui.dragBeginX;
			y = sy+mouseY-ClickGui.dragBeginY;
			drawDropSlot();

			ClickGui.currentDraggable = this;
		}
		else {
			
			x = roundX(x);
			y = roundY(y);
		}
		
		
		
		if(ClickGui.click&&mouseOverHeader(mouseX, mouseY)) {
			expanded = !expanded;
			ClickGui.click = false;
		}
		
		
		if(mouseOverHeader(mouseX, mouseY)&&!hovered&&!expanded&&ClickGui.currentDraggable==null) {	
			
			if(GuiHandler.showTooltips) {
				boolean mouseOnRight = x>w*2;
				GL11.glPushMatrix();
				GL11.glScalef(0.5f, 0.5f, 0.5f);
				GL11.glTranslatef(0, 0, 1000);
				int highStringLength = 0;
				for (int j = 0; j < desc.length; j++) {
					if(highStringLength<mc.fontRenderer.getStringWidth(desc[j])) highStringLength = mc.fontRenderer.getStringWidth(desc[j]);
					if(!mouseOnRight)BeveledBox.drawBeveledBox(2*mouseX+20, 2*mouseY+20+mc.fontRenderer.FONT_HEIGHT, 2*mouseX+highStringLength+20,(2*mouseY+mc.fontRenderer.FONT_HEIGHT)+(mc.fontRenderer.FONT_HEIGHT*(j+1))+20, 1, 0xDD000000, 0xDD000000, 0xFF000000);
					else BeveledBox.drawBeveledBox(2*mouseX-20-highStringLength, 2*mouseY+20+mc.fontRenderer.FONT_HEIGHT, 2*mouseX-20,(2*mouseY+mc.fontRenderer.FONT_HEIGHT)+(mc.fontRenderer.FONT_HEIGHT*(j+1))+20, 1, 0xDD000000, 0xDD000000, 0xFF000000);
				}
				for (int j = 0; j < desc.length; j++) {
					if(!mouseOnRight)drawString(mc.fontRenderer,desc[j], 2*mouseX+20, (2*mouseY+mc.fontRenderer.FONT_HEIGHT)+(mc.fontRenderer.FONT_HEIGHT*j)+21, Integer.parseInt("FFFFFF", 16));
					else drawString(mc.fontRenderer,desc[j], 2*mouseX-20-highStringLength, (2*mouseY+mc.fontRenderer.FONT_HEIGHT)+(mc.fontRenderer.FONT_HEIGHT*j)+21, Integer.parseInt("FFFFFF", 16));
				}


				GL11.glPopMatrix();
			}
			hovered = true;
		}
		
		
		
		if(expanded&&contents!=null&&contents.length>0&&!hovered) {
			for (int i = 0; i < contents.length; i++) {
				ClickGuiSetting setting = contents[i];
				int ly = i*17+17;
				
				if(mouseOverSetting(mouseX, mouseY, ly)) {
					
					if(ClickGui.click) {
						ClickGui.click = false;
						ClickGuiSetting.handleClick(setting, false);
					}else if(ClickGui.middleClick){
						ClickGui.middleClick = false;
						if(altPressed){
							ClickGui.currentEditingBind = ClickGuiSetting.getBindingCode(setting, getTitle());
						}
						else if(shiftPressed){
							ClickGuiSetting.resetToDefault(setting, getTitle());
						}
						else {
							ClickGuiSetting.handleClick(setting, true);
						}
					}
					hovered = true;
					BeveledBox.drawBeveledBox(setting.renderMinX, setting.renderMinY, setting.renderMaxX, setting.renderMaxY, 1, 0x11FFFFFF, 0x11FFFFFF, 0x44FFA11E);
					
					if(GuiHandler.showTooltips) {
						boolean mouseOnRight = x>w*2;
						GL11.glPushMatrix();
						GL11.glScalef(0.5f, 0.5f, 0.5f);
						GL11.glTranslatef(0, 0, 1000);
						int highStringLength = 0;
						for (int j = 0; j < setting.desc.length; j++) {
							if(highStringLength<mc.fontRenderer.getStringWidth(setting.desc[j])) highStringLength = mc.fontRenderer.getStringWidth(setting.desc[j]);
							if(!mouseOnRight)BeveledBox.drawBeveledBox(2*mouseX+20, 2*mouseY+20+mc.fontRenderer.FONT_HEIGHT, 2*mouseX+highStringLength+20,(2*mouseY+mc.fontRenderer.FONT_HEIGHT)+(mc.fontRenderer.FONT_HEIGHT*(j+1+2))+22, 1, 0xDD000000, 0xDD000000, 0xFF000000);
							else BeveledBox.drawBeveledBox(2*mouseX-20-highStringLength, 2*mouseY+20+mc.fontRenderer.FONT_HEIGHT, 2*mouseX-20,(2*mouseY+mc.fontRenderer.FONT_HEIGHT)+(mc.fontRenderer.FONT_HEIGHT*(j+1+2))+22, 1, 0xDD000000, 0xDD000000, 0xFF000000);
						}
						
						for (int j = 0; j < setting.desc.length; j++) {
							if(!mouseOnRight)drawString(mc.fontRenderer,setting.desc[j], 2*mouseX+20, (2*mouseY+mc.fontRenderer.FONT_HEIGHT)+(mc.fontRenderer.FONT_HEIGHT*j)+21, Integer.parseInt("FFFFFF", 16));
							else drawString(mc.fontRenderer,setting.desc[j], 2*mouseX-20-highStringLength, (2*mouseY+mc.fontRenderer.FONT_HEIGHT)+(mc.fontRenderer.FONT_HEIGHT*j)+21, Integer.parseInt("FFFFFF", 16));
						}
						
						if(setting.type== ClickGuiSetting.SettingType.CLICK_COMMAND){
							if (!mouseOnRight)
								drawString(mc.fontRenderer, "[YOU CANNOT ADD KEYBIND FOR THIS ONE]", 2 * mouseX + 20, (2 * mouseY + mc.fontRenderer.FONT_HEIGHT) + (mc.fontRenderer.FONT_HEIGHT * setting.desc.length) + 32, Integer.parseInt("FFFFFF", 16));
							else
								drawString(mc.fontRenderer, "[YOU CANNOT ADD KEYBIND FOR THIS ONE]", 2 * mouseX - 20 - highStringLength, (2 * mouseY + mc.fontRenderer.FONT_HEIGHT) + (mc.fontRenderer.FONT_HEIGHT * setting.desc.length) + 32, Integer.parseInt("FFFFFF", 16));

						}else {
							int bindInfo = 0;
							if (GuiConfig.guiBinds.containsKey(setting.panelTitle + "->" + setting.title))
								bindInfo = GuiConfig.guiBinds.get(setting.panelTitle + "->" + setting.title);
							if (!mouseOnRight)
								drawString(mc.fontRenderer, "[" + Keyboard.getKeyName(bindInfo) + "]", 2 * mouseX + 20, (2 * mouseY + mc.fontRenderer.FONT_HEIGHT) + (mc.fontRenderer.FONT_HEIGHT * setting.desc.length) + 32, Integer.parseInt("FFFFFF", 16));
							else
								drawString(mc.fontRenderer, "[" + Keyboard.getKeyName(bindInfo) + "]", 2 * mouseX - 20 - highStringLength, (2 * mouseY + mc.fontRenderer.FONT_HEIGHT) + (mc.fontRenderer.FONT_HEIGHT * setting.desc.length) + 32, Integer.parseInt("FFFFFF", 16));
						}
						GL11.glPopMatrix();
					}
				}
			}
		}
		
		return hovered;
	}
	
	
	public void draw(int mouseX, int mouseY) {
		mc = Minecraft.getMinecraft();
		
		GL11.glPushMatrix();
		GL11.glTranslated(0, 0, 500); 
		
		
		if(!expanded||contents==null||contents.length==0) h = 17;
		else h = contents.length*17+17;
		GuiTheme theme = GuiHandler.themes[GuiHandler.currentTheme];
		BeveledBox.drawBeveledBox(x, y, x+w, y+h, 1,
				theme.bgBorder(), theme.bgBorder(), theme.bgFill());

		if(mouseOverHeader(mouseX, mouseY)) {
			
			BeveledBox.drawBeveledBox(x, y, x+w, y+17, 1, 0x00000000, 0x00000000, theme.headerHover());
			drawCenteredString(mc.fontRenderer,title, x+(w/2), y+5, Integer.parseInt(theme.headerText(), 16));
		}
		else {
			
			BeveledBox.drawBeveledBox(x, y, x+w, y+17, 1, 0x00000000, 0x00000000, theme.headerNormal());
			drawCenteredString(mc.fontRenderer,title, x+(w/2), y+5, Integer.parseInt(theme.headerText(), 16));
		}
		
		
		if(expanded&&contents!=null&&contents.length>0) {
			for (int i = 0; i < contents.length; i++) {
				ClickGuiSetting setting = contents[i];
				int ly = i*17+17;
				
				setting.renderMinX = x;
				setting.renderMinY = y+ly;
				setting.renderMaxX = x + w;
				setting.renderMaxY = y+17+ly;
				
				switch (setting.type) {
				case TEXT:
					drawTextSetting(setting, ly, mouseOverSetting(mouseX, mouseY, ly));
					break;
				case CLICK_COMMAND:
					drawClickCommandSetting(setting, ly, mouseOverSetting(mouseX, mouseY, ly));
					break;
				case CLICK:
					drawClickSetting(setting, ly, mouseOverSetting(mouseX, mouseY, ly));
					break;
				default:
					break;
				}
				
			}
		}
		
		GL11.glPopMatrix();
	}

	private void drawClickSetting(ClickGuiSetting setting, int ly, boolean hover) {
		GuiTheme theme = GuiHandler.themes[GuiHandler.currentTheme];
		BeveledBox.drawBeveledBox(setting.renderMinX, setting.renderMinY, setting.renderMaxX, setting.renderMaxY, 1, 0x00000000, 0x00000000, theme.hoverFill());
		drawCenteredString(mc.fontRenderer,setting.title, x+(w/2), y+5+ly, Integer.parseInt((hover)?theme.textHover():theme.textNormal(), 16));
	}
	
	private void drawClickCommandSetting(ClickGuiSetting setting, int ly, boolean hover) {
		GuiTheme theme = GuiHandler.themes[GuiHandler.currentTheme];
		BeveledBox.drawBeveledBox(setting.renderMinX, setting.renderMinY, setting.renderMaxX, setting.renderMaxY, 1, 0x00000000, 0x00000000, theme.hoverFill());
		drawCenteredString(mc.fontRenderer,Config.HOTKEY_COMMANDS[Integer.parseInt(setting.title)], x+(w/2), y+5+ly, Integer.parseInt((hover)?theme.textHover():theme.textNormal(), 16));
	}

	private void drawTextSetting(ClickGuiSetting setting, int ly, boolean hover) {
		GuiTheme theme = GuiHandler.themes[GuiHandler.currentTheme];
		BeveledBox.drawBeveledBox(setting.renderMinX, setting.renderMinY, setting.renderMaxX, setting.renderMaxY, 1, 0x00000000, 0x00000000, theme.hoverFill());
		drawCenteredString(mc.fontRenderer,setting.title+": "+setting.values.get(setting.value).getAsString(), x+(w/2), y+5+ly, Integer.parseInt((hover)?theme.textHover():theme.textNormal(), 16));
	}

	private void drawDropSlot() {
		GuiTheme theme = GuiHandler.themes[GuiHandler.currentTheme];
		GL11.glPushMatrix();
		GL11.glTranslated(0, 0, 499); 
		BeveledBox.drawBeveledBox(roundX(x), roundY(y), roundX(x)+w, roundY(y)+h, 1, theme.dropSlot(), theme.dropSlot(), 0x00000000);
		GL11.glPopMatrix();
	}

	private boolean mouseOverSetting(int mouseX, int mouseY, int ly) {
		return (mouseX>x&&mouseX<x+w)&&(mouseY>y+ly&&mouseY<y+17+ly);
	}
	
	private boolean mouseOverHeader(int mouseX, int mouseY) {
		return (mouseX>x&&mouseX<x+w)&&(mouseY>y&&mouseY<y+17);
	}
	
	private boolean mouseOver(int mouseX, int mouseY) {
		return (mouseX>x&&mouseX<x+w)&&(mouseY>y&&mouseY<y+h);
	}
	
	int roundX(int x) {
		int r = (w+3)*(Math.round((x+w/2)/(w+3)));
		if(r<0) r = 0;
	    return r;
	}

	int roundY(int y) {
	    int r = 17*(Math.round(y/17));
	    if(r<0) r = 0;
	    return r;
	}
	
	public JsonObject toJson() {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("name", title);
		jsonObject.addProperty("x", x);
		jsonObject.addProperty("y", y);
		jsonObject.addProperty("expanded", expanded);
		
		JsonArray c = new JsonArray();
		for (int i = 0; i < contents.length; i++) {
			c.add(contents[i].toJson());
		}
		jsonObject.add("contents", c);
		
		
		JsonArray d = new JsonArray();
		for (int i = 0; i < desc.length; i++) {
			d.add(desc[i]);
		}
		jsonObject.add("desc", d);
		
		return jsonObject;
	}

	public static ClickGuiPanel fromJson(JsonObject obj) {
		ClickGuiPanel p = new ClickGuiPanel(obj.get("name").getAsString());
		p.expanded = obj.get("expanded").getAsBoolean();
		p.x = obj.get("x").getAsInt();
		p.y = obj.get("y").getAsInt();
		
		
		JsonArray deskRows = obj.get("desc").getAsJsonArray();
		String[] descRowsArray = new String[deskRows.size()];
		for (int i = 0; i < deskRows.size(); i++) {
			descRowsArray[i] = deskRows.get(i).getAsString();
		}
		p.desc = descRowsArray;
		
		p.contents = ClickGuiSetting.settingsFromJson(obj.get("contents").getAsJsonArray(), p.getTitle());
		return p;
	}
}
