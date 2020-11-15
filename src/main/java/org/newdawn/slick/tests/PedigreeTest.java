package org.newdawn.slick.tests;

import java.io.IOException;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.particles.ConfigurableEmitter;
import org.newdawn.slick.particles.ParticleIO;
import org.newdawn.slick.particles.ParticleSystem;


public class PedigreeTest extends BasicGame {
	
	private Image image;
	
	private GameContainer container;
	
	private ParticleSystem trail;
	
	private ParticleSystem fire;
	
	private float rx;
	
	private float ry = 900;
	
	
	public PedigreeTest() {
		super("Pedigree Test");
	}
	
	
	public void init(GameContainer container) throws SlickException {
		this.container = container;
		
		try {
			fire = ParticleIO.loadConfiguredSystem("testdata/system.xml");
			trail = ParticleIO.loadConfiguredSystem("testdata/smoketrail.xml");
			
		} catch (IOException e) {
			throw new SlickException("Failed to load particle systems", e);
		}
		image = new Image("testdata/rocket.png");
	
		spawnRocket();
	}

	
	private void spawnRocket() {
		ry = 700;
		rx = (float) ((Math.random()*600) + 100);
	}

	
	public void render(GameContainer container, Graphics g) {
		((ConfigurableEmitter) trail.getEmitter(0)).setPosition(rx+14,ry+35);
		trail.render();
		image.draw((int) rx,(int) ry);
		
		g.translate(400, 300);
		fire.render();
	}

	
	public void update(GameContainer container, int delta) {
		fire.update(delta);
		trail.update(delta);
		
		ry -= delta * 0.25f;
		if (ry < -100) {
			spawnRocket();
		}
	}

	public void mousePressed(int button, int x, int y) {
		super.mousePressed(button, x, y);
		
		for (int i=0;i<fire.getEmitterCount();i++) {
			((ConfigurableEmitter) fire.getEmitter(i)).setPosition(x - 400, y - 300, true);
		}
	}

	
	public static void main(String[] argv) {
		try {
			AppGameContainer container = new AppGameContainer(new PedigreeTest());
			container.setDisplayMode(800,600,false);
			container.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}

	
	public void keyPressed(int key, char c) {
		if (key == Input.KEY_ESCAPE) {
			container.exit();
		}
	}
}
