package org.newdawn.slick.gui;

import org.lwjgl.input.Cursor;
import org.newdawn.slick.Font;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.ImageData;


public interface GUIContext {

	
	public Input getInput();
	
	
	public long getTime();
	
	
	public abstract int getScreenWidth();
	
	
	public abstract int getScreenHeight();
	
	
	public int getWidth();
	
	
	public int getHeight();
	
	
	public Font getDefaultFont();
	
	
	public abstract void setMouseCursor(String ref, int hotSpotX, int hotSpotY) throws SlickException;

	
	public abstract void setMouseCursor(ImageData data, int hotSpotX, int hotSpotY) throws SlickException;

	
	public abstract void setMouseCursor(Cursor cursor, int hotSpotX, int hotSpotY) throws SlickException;
	
	
	public abstract void setDefaultMouseCursor();
}
