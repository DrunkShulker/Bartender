package org.newdawn.slick.tests;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;


public class KeyRepeatTest extends BasicGame {
	
	private int count;
	
	private Input input;
	
	
	public KeyRepeatTest() {
		super("KeyRepeatTest");
	}
	
	
	public void init(GameContainer container) throws SlickException {
		input = container.getInput();
		input.enableKeyRepeat(300,100);
	}

	
	public void render(GameContainer container, Graphics g) {
		g.drawString("Key Press Count: "+count, 100,100);
		g.drawString("Press Space to Toggle Key Repeat", 100,150);
		g.drawString("Key Repeat Enabled: "+input.isKeyRepeatEnabled(), 100,200);
	}

	
	public void update(GameContainer container, int delta) {
	}

	
	public static void main(String[] argv) {
		try {
			AppGameContainer container = new AppGameContainer(new KeyRepeatTest());
			container.setDisplayMode(800,600,false);
			container.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}

	
	public void keyPressed(int key, char c) {
		count++;
		if (key == Input.KEY_SPACE) {
			if (input.isKeyRepeatEnabled()) {
				input.disableKeyRepeat();
			} else {
				input.enableKeyRepeat(300,100);
			}
		}
	}
}
