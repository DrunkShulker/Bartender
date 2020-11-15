package org.newdawn.slick;


public interface KeyListener extends ControlledInputReciever {
	
	public abstract void keyPressed(int key, char c);

	
	public abstract void keyReleased(int key, char c);

}