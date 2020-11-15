package org.newdawn.slick.tests;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Ellipse;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.RoundedRectangle;


public class GeomAccuracyTest extends BasicGame {
	
	private GameContainer container;
	
	
	private Color geomColor;
	
	
	private Color overlayColor;
	
	
	private boolean hideOverlay;
	
	
	private int colorIndex;
	
	
	private int curTest;
	
	
	private static final int NUMTESTS = 3;
	
	
	private Image magImage;
	
	
	public GeomAccuracyTest() {
		super("Geometry Accuracy Tests");
	}
	
	
	public void init(GameContainer container) throws SlickException {
		this.container = container;
		
		geomColor = Color.magenta;
		overlayColor = Color.white;
		
		magImage = new Image(21, 21);
	}

	
	public void render(GameContainer container, Graphics g) {
		
		String text = new String();
		
		switch(curTest) {
		
		case 0:
				text = "Rectangles";
				rectTest(g);
				break;
				
		case 1:
				text = "Ovals";
				ovalTest(g);
				break;
				
		case 2:
				text ="Arcs";
				arcTest(g);
				break;
		}
		
		g.setColor(Color.white);
		g.drawString("Press T to toggle overlay", 200, 55);
		g.drawString("Press N to switch tests", 200, 35);
		g.drawString("Press C to cycle drawing colors", 200, 15);
		g.drawString("Current Test:", 400, 35);
		g.setColor(Color.blue);
		g.drawString(text, 485, 35);
		
		g.setColor(Color.white);
		g.drawString("Normal:", 10, 150);
		g.drawString("Filled:", 10, 300);
		
		g.drawString("Drawn with Graphics context", 125, 400);
		g.drawString("Drawn using Shapes", 450, 400);
		
		
		g.copyArea(magImage, container.getInput().getMouseX() - 10, container.getInput().getMouseY() - 10);
		magImage.draw(351, 451, 5);
		g.drawString("Mag Area -", 250, 475);
		g.setColor(Color.darkGray);
		g.drawRect(350, 450, 106, 106);
		
		g.setColor(Color.white);
		g.drawString("NOTE:", 500, 450);
		g.drawString("lines should be flush with edges", 525, 470);
		g.drawString("corners should be symetric", 525, 490);
		
	}
	
	
	void arcTest(Graphics g) {
		
		if(hideOverlay == false) {
			g.setColor(overlayColor);
			g.drawLine(198, 100, 198, 198);
			g.drawLine(100, 198, 198, 198);
		}
		
		g.setColor(geomColor);
		g.drawArc(100, 100, 99, 99, 0, 90);

	
	}
	
	
	void ovalTest(Graphics g) {

		g.setColor(geomColor);
		g.drawOval(100, 100, 99, 99);
		g.fillOval(100, 250, 99, 99);
		
		
		Ellipse elip = new Ellipse(449, 149, 49, 49);
		g.draw(elip);
		elip = new Ellipse(449, 299, 49, 49);
		g.fill(elip);
		
		if(hideOverlay == false) {
			g.setColor(overlayColor);
			g.drawLine(100, 149, 198, 149);
			g.drawLine(149, 100, 149, 198);
			
			g.drawLine(100, 149 + 150, 198, 149 + 150);
			g.drawLine(149, 100 + 150, 149, 198 + 150);
			
			g.drawLine(100 + 300, 149, 198 + 300, 149);
			g.drawLine(149 + 300, 100, 149 + 300, 198);			
			
			g.drawLine(100 + 300, 149 + 150, 198 + 300, 149 + 150);
			g.drawLine(149 + 300, 100 + 150, 149 + 300, 198 + 150);			
		}
		

	}
	
	
	void rectTest(Graphics g) {

		g.setColor(geomColor);
		
		
		g.drawRect(100, 100, 99, 99);
		g.fillRect(100, 250, 99, 99);
		
		g.drawRoundRect(250, 100, 99, 99, 10);
		g.fillRoundRect(250, 250, 99, 99, 10);
		
		
		Rectangle rect = new Rectangle(400, 100, 99, 99);
		g.draw(rect);
		rect = new Rectangle(400, 250, 99, 99);
		g.fill(rect);
		
		RoundedRectangle rrect = new RoundedRectangle(550, 100, 99, 99, 10);
		g.draw(rrect);
		rrect = new RoundedRectangle(550, 250, 99, 99, 10);
		g.fill(rrect);
		
		
		if(hideOverlay == false) {
	 		g.setColor(overlayColor);
	 		
	 		
			g.drawLine(100, 149, 198, 149);
			g.drawLine(149, 100, 149, 198);
			
			g.drawLine(100 + 150, 149, 198 + 150, 149);
			g.drawLine(149 + 150, 100, 149 + 150, 198);
	
			g.drawLine(100 + 300, 149, 198 + 300, 149);
			g.drawLine(149 + 300, 100, 149 + 300, 198);
			
			g.drawLine(100 + 450, 149, 198 + 450, 149);
			g.drawLine(149 + 450, 100, 149 + 450, 198);
			
	 		
			g.drawLine(100, 149 + 150, 198, 149 + 150);
			g.drawLine(149, 100 + 150, 149, 198 + 150);
			
			g.drawLine(100 + 150, 149 + 150, 198 + 150, 149 + 150);
			g.drawLine(149 + 150, 100 + 150, 149 + 150, 198 + 150);
	
			g.drawLine(100 + 300, 149 + 150, 198 + 300, 149 + 150);
			g.drawLine(149 + 300, 100 + 150, 149 + 300, 198 + 150);
			
			g.drawLine(100 + 450, 149 + 150, 198 + 450, 149 + 150);
			g.drawLine(149 + 450, 100 + 150, 149 + 450, 198 + 150);
		}		
	}

	
	public void update(GameContainer container, int delta) {
		
	}

	
	public void keyPressed(int key, char c) {
		if (key == Input.KEY_ESCAPE) {
			System.exit(0);
		}
		
		if(key == Input.KEY_N) {
			curTest++;
			curTest %= NUMTESTS;
		}
		
		if(key == Input.KEY_C) {
			colorIndex++;
			
			colorIndex %= 4;
			setColors();
		}
		
		if(key == Input.KEY_T) {
			hideOverlay = !hideOverlay;
		}

	}
	
	
	private void setColors() {
		switch(colorIndex)
		{
			case 0:
				overlayColor = Color.white;
				geomColor = Color.magenta;
				break;
	
			case 1:
				overlayColor = Color.magenta;
				geomColor = Color.white;
				break;
	
			case 2:
				overlayColor = Color.red;
				geomColor = Color.green;
				break;
	
			case 3:
				overlayColor = Color.red;
				geomColor = Color.white;
				break;
		}
	}
	
	
	public static void main(String[] argv) {
		try {
			AppGameContainer container = new AppGameContainer(new GeomAccuracyTest());
			container.setDisplayMode(800, 600, false);
			container.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
}
