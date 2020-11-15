package org.newdawn.slick.tests;

import org.newdawn.slick.AngelCodeFont;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.pbuffer.GraphicsFactory;


public class ImageGraphicsTest extends BasicGame {
	
	private Image preloaded;
	
	private Image target;
	
	private Image cut;
	
	private Graphics gTarget;
	
	private Graphics offscreenPreload;
	
	private Image testImage;
	
	private Font testFont;
	
	private float ang;
	
	private String using = "none";
	
	
	public ImageGraphicsTest() {
		super("Image Graphics Test");
	}
	
	
	public void init(GameContainer container) throws SlickException {
		testImage = new Image("testdata/logo.png");
		preloaded = new Image("testdata/logo.png");
		testFont = new AngelCodeFont("testdata/hiero.fnt","testdata/hiero.png");
		target = new Image(400,300);
		cut = new Image(100,100);
		gTarget = target.getGraphics();
		offscreenPreload = preloaded.getGraphics();
		
		offscreenPreload.drawString("Drawing over a loaded image", 5, 15);
		offscreenPreload.setLineWidth(5);
		offscreenPreload.setAntiAlias(true);
		offscreenPreload.setColor(Color.blue.brighter());
		offscreenPreload.drawOval(200, 30, 50, 50);
		offscreenPreload.setColor(Color.white);
		offscreenPreload.drawRect(190,20,70,70);
		offscreenPreload.flush();
		
		if (GraphicsFactory.usingFBO()) {
			using = "FBO (Frame Buffer Objects)";
		} else if (GraphicsFactory.usingPBuffer()) {
			using = "Pbuffer (Pixel Buffers)";
		}
		
		System.out.println(preloaded.getColor(50,50));
	}
	
	
	public void render(GameContainer container, Graphics g) throws SlickException {

		
		
		gTarget.setBackground(new Color(0,0,0,0));
		gTarget.clear();
		gTarget.rotate(200,160,ang);
		gTarget.setFont(testFont);
		gTarget.fillRect(10, 10, 50, 50);
		gTarget.drawString("HELLO WORLD",10,10);

		gTarget.drawImage(testImage,100,150);
		gTarget.drawImage(testImage,100,50);
		gTarget.drawImage(testImage,50,75);
		
		
		
		gTarget.flush(); 

		g.setColor(Color.red);
		g.fillRect(250, 50, 200, 200);
		
		
		target.draw(300,100);
		target.draw(300,410,200,150);
		target.draw(505,410,100,75);
		
		
		
		g.setColor(Color.white);
		g.drawString("Testing On Offscreen Buffer", 300, 80);
		g.setColor(Color.green);
		g.drawRect(300, 100, target.getWidth(), target.getHeight());
		g.drawRect(300, 410, target.getWidth()/2, target.getHeight()/2);
		g.drawRect(505, 410, target.getWidth()/4, target.getHeight()/4);
		
		
		
		
		g.setColor(Color.white);
		g.drawString("Testing Font On Back Buffer", 10, 100);
		g.drawString("Using: "+using, 10, 580);
		g.setColor(Color.red);
		g.fillRect(10,120,200,5);
		
		
		int xp = (int) (60 + (Math.sin(ang / 60) * 50));
		g.copyArea(cut,xp,50);
		
		
		
		cut.draw(30,250);
		g.setColor(Color.white);
		g.drawRect(30, 250, cut.getWidth(), cut.getHeight());
		g.setColor(Color.gray);
		g.drawRect(xp, 50, cut.getWidth(), cut.getHeight());
		
		
		
		
		preloaded.draw(2,400);
		g.setColor(Color.blue);
		g.drawRect(2,400,preloaded.getWidth(),preloaded.getHeight());
	}

	
	public void update(GameContainer container, int delta) {
		ang += delta * 0.1f;
	}

	
	public static void main(String[] argv) {
		try {
			GraphicsFactory.setUseFBO(false);
			
			AppGameContainer container = new AppGameContainer(new ImageGraphicsTest());
			container.setDisplayMode(800,600,false);
			container.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
}
