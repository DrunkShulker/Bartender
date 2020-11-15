package org.newdawn.slick.state.transition;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.GameState;
import org.newdawn.slick.state.StateBasedGame;


public interface Transition {
	
	public void update(StateBasedGame game, GameContainer container, int delta) throws SlickException;

	
	public void preRender(StateBasedGame game, GameContainer container, Graphics g) throws SlickException;
	
	
	public void postRender(StateBasedGame game, GameContainer container, Graphics g) throws SlickException;
	
	
	public boolean isComplete();
	
	
	public void init(GameState firstState, GameState secondState);
}
