package org.newdawn.slick.tests;

import org.newdawn.slick.Animation;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.PackedSpriteSheet;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;


public class PackedSheetTest extends BasicGame {
	
	private PackedSpriteSheet sheet;
	
	private GameContainer container;
	
	private float r = -500;
	
	private Image rocket;
	
	private Animation runner;
	
	private float ang;
	
	
	public PackedSheetTest() {
		super("Packed Sprite Sheet Test");
	}
	
	
	public void init(GameContainer container) throws SlickException {
		this.container = container;
		
		sheet = new PackedSpriteSheet("testdata/testpack.def", Image.FILTER_NEAREST);
		rocket = sheet.getSprite("rocket");
		
		SpriteSheet anim = sheet.getSpriteSheet("runner");
		runner = new Animation();
		
		for (int y=0;y<2;y++) {
			for (int x=0;x<6;x++) {
				runner.addFrame(anim.getSprite(x,y), 50);
			}
		}
	}

	
	public void render(GameContainer container, Graphics g) {
		rocket.draw((int) r,100);
		runner.draw(250,250);
		g.scale(1.2f,1.2f);
		runner.draw(250,250);
		g.scale(1.2f,1.2f);
		runner.draw(250,250);
		g.resetTransform();
		
		g.rotate(670, 470, ang);
		sheet.getSprite("floppy").draw(600,400);
	}

	
	public void update(GameContainer container, int delta) {
		r += delta * 0.4f;
		if (r > 900) {
			r = -500;
		}
		
		ang += delta * 0.1f;
	}

	
	public static void main(String[] argv) {
		try {
			AppGameContainer container = new AppGameContainer(new PackedSheetTest());
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
