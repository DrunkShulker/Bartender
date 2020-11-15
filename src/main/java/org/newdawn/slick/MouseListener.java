package org.newdawn.slick;


public interface MouseListener extends ControlledInputReciever {

	
	public abstract void mouseWheelMoved(int change);

	
	public abstract void mouseClicked(int button, int x, int y, int clickCount);

	
	public abstract void mousePressed(int button, int x, int y);

	
	public abstract void mouseReleased(int button, int x, int y);

	
	public abstract void mouseMoved(int oldx, int oldy, int newx, int newy);
	
	
	public abstract void mouseDragged(int oldx, int oldy, int newx, int newy);

}