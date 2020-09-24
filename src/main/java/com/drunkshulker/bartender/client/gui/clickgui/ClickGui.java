package com.drunkshulker.bartender.client.gui.clickgui;

import java.io.IOException;

import com.drunkshulker.bartender.client.input.Keybinds;
import com.google.gson.JsonObject;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.drunkshulker.bartender.client.gui.GuiConfig;
import com.drunkshulker.bartender.client.gui.GuiHandler;
import com.drunkshulker.bartender.client.input.KeyInputHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiInventory;

public class ClickGui extends GuiScreen{
	
	public static ClickGuiPanel[] panels;
	
	public static boolean click = false;
	public static boolean middleClick = false;
	public static boolean drag = false;
	public static int lastMouseKey = 0;
	
	public static int dragBeginX = 0;
	public static int dragBeginY = 0;
	public static String currentEditingBind = null;
	
	public static ClickGuiPanel currentDraggable;

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		Minecraft mc = Minecraft.getMinecraft();
		
		if(GuiHandler.showDimmed) super.drawDefaultBackground();
		
		if(GuiHandler.showPlayer){
			GL11.glPushMatrix();
			GL11.glTranslated(0, 0, 200);
			GL11.glColor4f(1, 1, 1, 1);
			
	    	int ey = height / 2 + 90;
	    	
	    	if(mc.player.isElytraFlying()) ey -= 120;
	    	
	    	final int size = 100;
	    	
	    	int swidth = 80 * (int) (width / 427.0);
	    	int sheight = 150 * (int) (height / 150.0);
	    	final float mX = (float) swidth - mouseX;
	        final float mY = (float) sheight - size * 1.67F - mouseY;
	        
	    	GuiInventory.drawEntityOnScreen(width / 2, ey, size, mX , mY, mc.player);
	    	GL11.glPopMatrix();
		}

		
		if(currentEditingBind==null){
			if(GuiScreen.isAltKeyDown()){
				String bindWarning = "Middle click the setting you want to bind";
				drawString(mc.fontRenderer,bindWarning,
						width-mc.fontRenderer.getStringWidth(bindWarning)-2,
						height-12,
						Integer.parseInt("FFFFFF", 16));
			}
		}else{
			String bindWarning = getBindName(currentEditingBind)+" ["+currentEditingBind+"]";
			drawString(mc.fontRenderer,bindWarning,
					width-mc.fontRenderer.getStringWidth(bindWarning)-2,
					height-21,
					Integer.parseInt("FFFFFF", 16));

			String bindDelWarning = "Press any key to bind | Backspace to reset";
			drawString(mc.fontRenderer,bindDelWarning,
					width-mc.fontRenderer.getStringWidth(bindDelWarning)-2,
					height-12,
					Integer.parseInt("FFFFFF", 16));
		}

    	drawPanels(mouseX, mouseY);

		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	private String getBindName(String bind) {
		String panelName, settingName;
		String[] l = currentEditingBind.split("->");
		panelName = l[0];
		settingName = l[1];

		JsonObject j = GuiConfig.getPanelByName(panelName);
		JsonObject s = GuiConfig.getSettingByName(j.get("contents").getAsJsonArray(), settingName);

		if(s.has("bind")){
			int b = s.get("bind").getAsInt();
			if(b==0) return "";
			else return b+"";
		}

		return "";
	}

	private void drawPanels(int mouseX, int mouseY) {
		for (ClickGuiPanel panel : panels) {
			panel.draw(mouseX, mouseY);
		}

		boolean hovered = false;
		for (int i = panels.length-1; i >=0; i--) {
			hovered = panels[i].listen(mouseX, mouseY, hovered, GuiScreen.isAltKeyDown());
		}
	}

	@Override
	public void initGui() {
		if(GuiConfig.usingDefaultConfig) resetLayout();
		super.initGui();
	}
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		super.actionPerformed(button);
	}
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if(keyCode==Keyboard.KEY_BACK){
			
			GuiConfig.bindKey(currentEditingBind, 0);
			currentEditingBind=null;
		}else if(keyCode==Keyboard.KEY_ESCAPE){
			
			currentEditingBind=null;
		}else if(keyCode==Keyboard.KEY_LMENU||keyCode==Keyboard.KEY_RMENU){
			
		}else{
			
			if(currentEditingBind!=null){
				GuiConfig.bindKey(currentEditingBind, keyCode);
				currentEditingBind=null;
			}
		}
		super.keyTyped(typedChar, keyCode);
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		lastMouseKey = mouseButton;

		if(mouseButton==1) {
			drag = true;
			dragBeginX = mouseX;
			dragBeginY = mouseY;
		}
		else if(mouseButton==0) {
			click = true;
		}
		else if(mouseButton==2) {
			middleClick = true;
		}
		
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}
	
	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		drag = false;
		click = false;
		currentDraggable = null;
		
		super.mouseReleased(mouseX, mouseY, state);
	}
	
	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
	
	@Override
	public void onGuiClosed() {
		KeyInputHandler.guiMouseHold = false;
		
		GuiConfig.save();
		currentEditingBind = null;
		super.onGuiClosed();
	}

	public static void bringToFront(ClickGuiPanel panel) {
		int i = java.util.Arrays.asList(panels).indexOf(panel);
		int last = panels.length-1;
		ClickGuiPanel temp = panels[last];
	    panels[last] = panels[i];
	    panels[i] = temp;
	}
	
	public static void resetLayout() {
		for (int i = 0; i < panels.length; i++) {
			panels[i].expanded = false;
			panels[i].x = 0;
			panels[i].y = 17*i;
		}
		
		GuiConfig.save();
	}

}
