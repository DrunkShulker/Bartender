package org.newdawn.slick.tests;
	
import org.newdawn.slick.AngelCodeFont;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Font;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;


public class PureFontTest extends BasicGame {
	
	private Font font;
	
	private Image image;
	
	
	public PureFontTest() {
		super("Hiero Font Test");
	}
	
	
	public void init(GameContainer container) throws SlickException {
		image = new Image("testdata/sky.jpg");
		font = new AngelCodeFont("testdata/hiero.fnt","testdata/hiero.png");
	}

	
	public void render(GameContainer container, Graphics g) {
		image.draw(0,0,800,600);
		font.drawString(100, 32, "On top of old smokey, all");
		font.drawString(100, 80, "covered with sand..");
	}

	
	public void update(GameContainer container, int delta) throws SlickException {
	}
	
	
	public void keyPressed(int key, char c) {
		if (key == Input.KEY_ESCAPE) {
			System.exit(0);
		}
	}
	
	
	private static AppGameContainer container;
	
	
	public static void main(String[] argv) {
		try {
			container = new AppGameContainer(new PureFontTest());
			container.setDisplayMode(800,600,false);
			container.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
}
