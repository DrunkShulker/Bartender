package org.newdawn.slick.tests;

import java.io.IOException;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.imageout.ImageOut;
import org.newdawn.slick.particles.ParticleIO;
import org.newdawn.slick.particles.ParticleSystem;


public class ImageOutTest extends BasicGame {
	
	private GameContainer container;
	
	private ParticleSystem fire;
	
	private Graphics g;
	
	private Image copy;
	
	private String message;
	
	
	public ImageOutTest() {
		super("Image Out Test");
	}
	
	
	public void init(GameContainer container) throws SlickException {
		this.container = container;
		
		try {
			fire = ParticleIO.loadConfiguredSystem("testdata/system.xml");
		} catch (IOException e) {
			throw new SlickException("Failed to load particle systems", e);
		}
		
		copy = new Image(400,300);
		String[] formats = ImageOut.getSupportedFormats();
		message = "Formats supported: ";
		for (int i=0;i<formats.length;i++) {
			message += formats[i];
			if (i < formats.length - 1) {
				message += ",";
			}
		}
	}

	
	public void render(GameContainer container, Graphics g) {
		g.drawString("T - TGA Snapshot", 10,50);
		g.drawString("J - JPG Snapshot", 10,70);
		g.drawString("P - PNG Snapshot", 10,90);

		g.setDrawMode(Graphics.MODE_ADD);
		g.drawImage(copy, 200, 300);
		g.setDrawMode(Graphics.MODE_NORMAL);
		
		g.drawString(message, 10,400);
		g.drawRect(200,0,400,300);
		g.translate(400, 250);
		fire.render();
		this.g = g;
	}

	
	private void writeTo(String fname) throws SlickException {
		g.copyArea(copy, 200,0);
		ImageOut.write(copy, fname);
		message = "Written "+fname;
	}
	
	
	public void update(GameContainer container, int delta) throws SlickException {
		fire.update(delta);
		
		if (container.getInput().isKeyPressed(Input.KEY_P)) {
			writeTo("ImageOutTest.png");
		}
		if (container.getInput().isKeyPressed(Input.KEY_J)) {
			writeTo("ImageOutTest.jpg");
		}
		if (container.getInput().isKeyPressed(Input.KEY_T)) {
			writeTo("ImageOutTest.tga");
		}
	}

	
	public static void main(String[] argv) {
		try {
			AppGameContainer container = new AppGameContainer(new ImageOutTest());
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
