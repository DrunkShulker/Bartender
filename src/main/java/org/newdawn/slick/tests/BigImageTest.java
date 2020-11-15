package org.newdawn.slick.tests;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.BigImage;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;


public class BigImageTest extends BasicGame {
	
	private Image original;
	
	private Image image;
	
	private Image imageX;
	
	private Image imageY;
	
	private Image sub;
	
	private Image scaledSub;
	
	private float x;
	
	private float y;
	
	private float ang = 30f;
	
	private SpriteSheet bigSheet;
	
	
	public BigImageTest() {
		super("Big Image Test");
	}
	
	
	public void init(GameContainer container) throws SlickException {
		
		original = image = new BigImage("testdata/bigimage.tga", Image.FILTER_NEAREST, 512);
		sub = image.getSubImage(210,210,200,130);
		scaledSub = sub.getScaledCopy(2);
		image = image.getScaledCopy(0.3f);
		imageX = image.getFlippedCopy(true, false);
		imageY = imageX.getFlippedCopy(true, true);
		
		bigSheet = new SpriteSheet(original, 16, 16);
	}

	
	public void render(GameContainer container, Graphics g) {
		original.draw(0,0,new Color(1,1,1,0.4f));
		
		image.draw(x,y);
		imageX.draw(x+400,y);
		imageY.draw(x,y+300);
		scaledSub.draw(x+300,y+300);
		
		bigSheet.getSprite(7, 5).draw(50,10);
		g.setColor(Color.white);
		g.drawRect(50,10,64,64);
		g.rotate(x+400, y+165, ang);
		g.drawImage(sub, x+300, y+100);
	}

	
	public static void main(String[] argv) {
		try {
			AppGameContainer container = new AppGameContainer(new BigImageTest());
			container.setDisplayMode(800,600,false);
			container.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}

	
	public void update(GameContainer container, int delta) throws SlickException {
		ang += delta * 0.1f;
		
		if (container.getInput().isKeyDown(Input.KEY_LEFT)) {
			x -= delta * 0.1f;
		}
		if (container.getInput().isKeyDown(Input.KEY_RIGHT)) {
			x += delta * 0.1f;
		}
		if (container.getInput().isKeyDown(Input.KEY_UP)) {
			y -= delta * 0.1f;
		}
		if (container.getInput().isKeyDown(Input.KEY_DOWN)) {
			y += delta * 0.1f;
		}
	}
}
