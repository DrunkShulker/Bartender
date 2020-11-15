package org.newdawn.slick.tests;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.AngelCodeFont;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;


public class DistanceFieldTest extends BasicGame {
	
	private AngelCodeFont font;
	
	
	public DistanceFieldTest() {
		super("DistanceMapTest Test");
	}
	
	
	public void init(GameContainer container) throws SlickException {
		font = new AngelCodeFont("testdata/distance.fnt", "testdata/distance-dis.png");
		container.getGraphics().setBackground(Color.black);
	}

	
	public void update(GameContainer container, int delta)
			throws SlickException {
	}

	
	public void render(GameContainer container, Graphics g)
			throws SlickException {
		String text = "abc";
		font.drawString(610,100,text);
		
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glAlphaFunc(GL11.GL_GEQUAL, 0.5f);
		font.drawString(610,150,text);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		
		g.translate(-50,-130);
		g.scale(10,10);
		font.drawString(0,0,text);

		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glAlphaFunc(GL11.GL_GEQUAL, 0.5f);
		font.drawString(0,26,text);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		
		g.resetTransform();
		g.setColor(Color.lightGray);
		g.drawString("Original Size on Sheet", 620, 210);
		g.drawString("10x Scale Up", 40, 575);
		
		g.setColor(Color.darkGray);
		g.drawRect(40, 40, 560,530);
		g.drawRect(610, 105, 150,100);

		g.setColor(Color.white);
		g.drawString("512x512 Font Sheet", 620, 300);
		g.drawString("NEHE Charset", 620, 320);
		g.drawString("4096x4096 (8x) Source Image", 620, 340);
		g.drawString("ScanSize = 20", 620, 360);
	}

	
	public void keyPressed(int key, char c) {
	}
	
	
	public static void main(String[] argv) {
		try {
			AppGameContainer container = new AppGameContainer(new DistanceFieldTest());
			container.setDisplayMode(800,600,false);
			container.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
}
