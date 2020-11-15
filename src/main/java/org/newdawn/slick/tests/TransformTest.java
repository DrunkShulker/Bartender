package org.newdawn.slick.tests;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;


public class TransformTest extends BasicGame {
	
	private float scale = 1;
	
	private boolean scaleUp;
	
	private boolean scaleDown;
	
	
	public TransformTest() {
		super("Transform Test");
	}
	
	
	public void init(GameContainer container) throws SlickException {
		container.setTargetFrameRate(100);
	}

	
	public void render(GameContainer contiainer, Graphics g) {
		g.translate(320,240);
		g.scale(scale, scale);

		g.setColor(Color.red);
		for (int x=0;x<10;x++) {
			for (int y=0;y<10;y++) {
				g.fillRect(-500+(x*100), -500+(y*100), 80, 80);
			}
		}
		
		g.setColor(new Color(1,1,1,0.5f));
		g.fillRect(-320,-240,640,480);
		g.setColor(Color.white);
		g.drawRect(-320,-240,640,480);
	}

	
	public void update(GameContainer container, int delta) {
		if (scaleUp) {
			scale += delta * 0.001f;
		}
		if (scaleDown) {
			scale -= delta * 0.001f;
		}
	}

	
	public void keyPressed(int key, char c) {
		if (key == Input.KEY_ESCAPE) {
			System.exit(0);
		}
		if (key == Input.KEY_Q) {
			scaleUp = true;
		}
		if (key == Input.KEY_A) {
			scaleDown = true;
		}
	}

	
	public void keyReleased(int key, char c) {
		if (key == Input.KEY_Q) {
			scaleUp = false;
		}
		if (key == Input.KEY_A) {
			scaleDown = false;
		}
	}
	
	
	public static void main(String[] argv) {
		try {
			AppGameContainer container = new AppGameContainer(new TransformTest());
			container.setDisplayMode(640,480,false);
			container.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
}
