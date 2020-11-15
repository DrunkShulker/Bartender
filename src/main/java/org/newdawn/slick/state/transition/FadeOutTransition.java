package org.newdawn.slick.state.transition;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.GameState;
import org.newdawn.slick.state.StateBasedGame;


public class FadeOutTransition implements Transition {
	
	private Color color;
	
	private int fadeTime;

	
	public FadeOutTransition() {
		this(Color.black, 500);
	}
	
	
	public FadeOutTransition(Color color) {
		this(color, 500);
	}
	
	
	public FadeOutTransition(Color color, int fadeTime) {
		this.color = new Color(color);
		this.color.a = 0;
		this.fadeTime = fadeTime;
	}
	
	
	public boolean isComplete() {
		return (color.a >= 1);
	}

	
	public void postRender(StateBasedGame game, GameContainer container, Graphics g) {
		Color old = g.getColor();
		g.setColor(color);
		g.fillRect(0, 0, container.getWidth() * 2, container.getHeight() * 2);
		g.setColor(old);
	}
	
	
	public void update(StateBasedGame game, GameContainer container, int delta) {
		color.a += delta * (1.0f / fadeTime);
		if (color.a > 1) {
			color.a = 1;
		}
	}

	
	public void preRender(StateBasedGame game, GameContainer container, Graphics g) {
	}

	public void init(GameState firstState, GameState secondState) {
		
		
	}

}
