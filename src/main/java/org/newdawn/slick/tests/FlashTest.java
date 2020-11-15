package org.newdawn.slick.tests;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;


public class FlashTest extends BasicGame {
	
	private Image image;
	
	private boolean flash;
	
	private GameContainer container;
	
	
	public FlashTest() {
		super("Flash Test");
	}
	
	
	public void init(GameContainer container) throws SlickException {
		this.container = container;
		
		image = new Image("testdata/logo.tga");
	}

	
	public void render(GameContainer container, Graphics g) {
		g.drawString("Press space to toggle",10,50);
		if (flash) {
			image.draw(100,100);
		} else {
			image.drawFlash(100,100,image.getWidth(), image.getHeight(), new Color(1,0,1f,1f));
		}
	}

	
	public void update(GameContainer container, int delta) {
	}

	
	public static void main(String[] argv) {
		try {
			AppGameContainer container = new AppGameContainer(new FlashTest());
			container.setDisplayMode(800,600,false);
			container.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}

	
	public void keyPressed(int key, char c) {
		if (key == Input.KEY_SPACE) {
			flash = !flash;
		}
		if (key == Input.KEY_ESCAPE) {
			container.exit();
		}
	}
}
