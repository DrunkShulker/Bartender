package org.newdawn.slick;


public interface Game {
	
	public void init(GameContainer container) throws SlickException;
	
	
	public void update(GameContainer container, int delta) throws SlickException;
	
	
	public void render(GameContainer container, Graphics g) throws SlickException;
	
	
	public boolean closeRequested();
	
	
	public String getTitle();
}
