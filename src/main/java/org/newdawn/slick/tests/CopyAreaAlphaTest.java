package org.newdawn.slick.tests;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;


public class CopyAreaAlphaTest extends BasicGame {
	
	private Image textureMap;
	
	private Image copy;
	
	
	public CopyAreaAlphaTest() {
		super("CopyArea Alpha Test");
	}
	
	
	public void init(GameContainer container) throws SlickException {
		textureMap = new Image("testdata/grass.png");
		container.getGraphics().setBackground(Color.black);
		copy = new Image(100,100);
	}

	
	public void update(GameContainer container, int delta)
			throws SlickException {
	}

	
	public void render(GameContainer container, Graphics g)
			throws SlickException {
		g.clearAlphaMap();
		g.setDrawMode(Graphics.MODE_NORMAL);
		g.setColor(Color.white);
		g.fillOval(100,100,150,150);
		textureMap.draw(10,50);
		
		g.copyArea(copy, 100,100);
		g.setColor(Color.red);
		g.fillRect(300,100,200,200);
		copy.draw(350,150);
	}

	
	public void keyPressed(int key, char c) {
	}
	
	
	public static void main(String[] argv) {
		try {
			AppGameContainer container = new AppGameContainer(new CopyAreaAlphaTest());
			container.setDisplayMode(800,600,false);
			container.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
}
