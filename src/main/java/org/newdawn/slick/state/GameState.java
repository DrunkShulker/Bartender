package org.newdawn.slick.state;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.InputListener;
import org.newdawn.slick.SlickException;


public interface GameState extends InputListener {
	
	public int getID();
	
	
	public void init(GameContainer container, StateBasedGame game) throws SlickException;
	
	
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException;
	
	
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException ;
	
	
	public void enter(GameContainer container, StateBasedGame game) throws SlickException;

	
	public void leave(GameContainer container, StateBasedGame game) throws SlickException;
}
