package org.newdawn.slick.state.transition;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.renderer.Renderer;
import org.newdawn.slick.opengl.renderer.SGL;
import org.newdawn.slick.state.GameState;
import org.newdawn.slick.state.StateBasedGame;


public class SelectTransition implements Transition {
	
	protected static SGL GL = Renderer.get();
	
	
	private GameState prev;
	
	private boolean finish;
	
	private Color background;

	
	private float scale1 = 1;
	
	private float xp1 = 0;
	
	private float yp1 = 0;
	
	private float scale2 = 0.4f;
	
	private float xp2 = 0;
	
	private float yp2 = 0;
	
	private boolean init = false;
	
	
	private boolean moveBackDone = false;
	
	private int pause = 300;
	
	
	public SelectTransition() {
		
	}

	
	public SelectTransition(Color background) {
		this.background = background;
	}
	
	
	public void init(GameState firstState, GameState secondState) {
		prev = secondState;
	}

	
	public boolean isComplete() {
		return finish;
	}

	
	public void postRender(StateBasedGame game, GameContainer container, Graphics g) throws SlickException {
		g.resetTransform();
		
		if (!moveBackDone) {
			g.translate(xp1,yp1);
			g.scale(scale1, scale1);
			g.setClip((int) xp1,(int) yp1,(int) (scale1*container.getWidth()),(int) (scale1*container.getHeight()));
			prev.render(container, game, g);
			g.resetTransform();
			g.clearClip();
		}
	}

	
	public void preRender(StateBasedGame game, GameContainer container,
			Graphics g) throws SlickException {
		if (moveBackDone) {
			g.translate(xp1,yp1);
			g.scale(scale1, scale1);
			g.setClip((int) xp1,(int) yp1,(int) (scale1*container.getWidth()),(int) (scale1*container.getHeight()));
			prev.render(container, game, g);
			g.resetTransform();
			g.clearClip();
		}
		
		g.translate(xp2,yp2);
		g.scale(scale2, scale2);
		g.setClip((int) xp2,(int) yp2,(int) (scale2*container.getWidth()),(int) (scale2*container.getHeight()));
	}

	
	public void update(StateBasedGame game, GameContainer container, int delta)
			throws SlickException {
		if (!init) {
			init = true;
			xp2 = (container.getWidth()/2)+50;
			yp2 = (container.getHeight()/4);
		}
		
		if (!moveBackDone) {
			if (scale1 > 0.4f) {
				scale1 -= delta * 0.002f;
				if (scale1 <= 0.4f) {
					scale1 = 0.4f;
				}
				xp1 += delta * 0.3f;
				if (xp1 > 50) {
					xp1 = 50;
				}
				yp1 += delta * 0.5f;
				if (yp1 > (container.getHeight()/4)) {
					yp1 = (container.getHeight()/4);
				}
			} else {
				moveBackDone = true;
			}
		} else {
			pause -= delta;
			if (pause > 0) {
				return;
			}
			if (scale2 < 1) {
				scale2 += delta * 0.002f;
				if (scale2 >= 1) {
					scale2 = 1f;
				}
				xp2 -= delta * 1.5f;
				if (xp2 < 0) {
					xp2 = 0;
				}
				yp2 -= delta * 0.5f;
				if (yp2 < 0) {
					yp2 = 0;
				}
			} else {
				finish = true;
			}
		}
	}
}
