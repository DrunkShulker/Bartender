package org.newdawn.slick.tests;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;


public class ClipTest extends BasicGame {

	
	private float ang = 0;
	
	private boolean world;
	
	private boolean clip;
	
	
	public ClipTest() {
		super("Clip Test");
	}
	
	
	public void init(GameContainer container) throws SlickException {
	}

	
	public void update(GameContainer container, int delta)
			throws SlickException {
		ang += delta * 0.01f;
	}

	
	public void render(GameContainer container, Graphics g)
			throws SlickException {
		g.setColor(Color.white);
		g.drawString("1 - No Clipping", 100, 10);
		g.drawString("2 - Screen Clipping", 100, 30);
		g.drawString("3 - World Clipping", 100, 50);
		
		if (world) {
			g.drawString("WORLD CLIPPING ENABLED", 200, 80);
		} 
		if (clip) {
			g.drawString("SCREEN CLIPPING ENABLED", 200, 80);
		}
		
		g.rotate(400, 400, ang);
		if (world) {
			g.setWorldClip(350,302,100,196);
		}
		if (clip) {
			g.setClip(350,302,100,196);
		}
		g.setColor(Color.red);
		g.fillOval(300,300,200,200);
		g.setColor(Color.blue);
		g.fillRect(390,200,20,400);
		
		g.clearClip();
		g.clearWorldClip();
	}

	
	public void keyPressed(int key, char c) {
		if (key == Input.KEY_1) {
			world = false;
			clip = false;
		}
		if (key == Input.KEY_2) {
			world = false;
			clip = true;
		}
		if (key == Input.KEY_3) {
			world = true;
			clip = false;
		}
	}
	
	
	public static void main(String[] argv) {
		try {
			AppGameContainer container = new AppGameContainer(new ClipTest());
			container.setDisplayMode(800,600,false);
			container.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
}
