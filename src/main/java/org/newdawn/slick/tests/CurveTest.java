package org.newdawn.slick.tests;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Curve;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Vector2f;


public class CurveTest extends BasicGame {
	
	private Curve curve;
	
	private Vector2f p1 = new Vector2f(100,300);
	
	private Vector2f c1 = new Vector2f(100,100);
	
	private Vector2f c2 = new Vector2f(300,100);
	
	private Vector2f p2 = new Vector2f(300,300);
	
	
	private Polygon poly;
	
	
	public CurveTest() {
		super("Curve Test");
	}

	
	public void init(GameContainer container) throws SlickException {
		container.getGraphics().setBackground(Color.white);
		
		curve = new Curve(p2,c2,c1,p1);
		poly = new Polygon();
		poly.addPoint(500,200);
		poly.addPoint(600,200);
		poly.addPoint(700,300);
		poly.addPoint(400,300);
	}

	
	public void update(GameContainer container, int delta) throws SlickException {
	}

	
	private void drawMarker(Graphics g, Vector2f p) {
		g.drawRect(p.x-5, p.y-5,10,10);
	}
	
	
	public void render(GameContainer container, Graphics g) throws SlickException {
		g.setColor(Color.gray);
		drawMarker(g, p1);
		drawMarker(g, p2);
		g.setColor(Color.red);
		drawMarker(g, c1);
		drawMarker(g, c2);
		
		g.setColor(Color.black);
		g.draw(curve);
		g.fill(curve);
		
		g.draw(poly);
		g.fill(poly);
	}
	
	
	public static void main(String argv[]) {
		try {
			AppGameContainer container = new AppGameContainer(new CurveTest());
			container.setDisplayMode(800,600,false);
			container.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
}
