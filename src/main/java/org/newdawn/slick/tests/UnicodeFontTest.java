
package org.newdawn.slick.tests;

import java.io.IOException;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.font.effects.ShadowEffect;


public class UnicodeFontTest extends BasicGame {
	
	private UnicodeFont unicodeFont;

	
	public UnicodeFontTest() {
		super("Font Test");
	}

	
	public void init(GameContainer container) throws SlickException {
		container.setShowFPS(false);

		
		unicodeFont = new UnicodeFont("c:/windows/fonts/arial.ttf", 48, false, false);




		unicodeFont.getEffects().add(new ColorEffect(java.awt.Color.white));

		
		

		
		

		container.getGraphics().setBackground(Color.darkGray);
	}

	
	public void render(GameContainer container, Graphics g) {
		g.setColor(Color.white);

		String text = "This is UnicodeFont!\nIt rockz. Kerning: T,";
		unicodeFont.drawString(10, 33, text);
		
		g.setColor(Color.red);
		g.drawRect(10, 33, unicodeFont.getWidth(text), unicodeFont.getLineHeight());
		g.setColor(Color.blue);
		int yOffset = unicodeFont.getYOffset(text);
		g.drawRect(10, 33 + yOffset, unicodeFont.getWidth(text), unicodeFont.getHeight(text) - yOffset);

		

		unicodeFont.addGlyphs("~!@!#!#$%___--");
		
		
	}

	
	public void update (GameContainer container, int delta) throws SlickException {
		unicodeFont.loadGlyphs(1);
	}

	
	public static void main(String[] args) throws SlickException, IOException {
		Input.disableControllers();
		AppGameContainer container = new AppGameContainer(new UnicodeFontTest());
		container.setDisplayMode(512, 600, false);
		container.setTargetFrameRate(20);
		container.start();
	}
}
