package org.newdawn.slick.tests;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.fills.GradientFill;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.RoundedRectangle;
import org.newdawn.slick.opengl.renderer.Renderer;


public class GradientTest extends BasicGame {
	
	private GameContainer container;
	
	private GradientFill gradient;
	
	private GradientFill gradient2;
	
	private GradientFill gradient4;
	
	private Rectangle rect;
	
	private Rectangle center;
	
	private RoundedRectangle round;
	
	private RoundedRectangle round2;
	
	private Polygon poly;
	
	private float ang;
	
	
	public GradientTest() {
		super("Gradient Test");
	}
	
	
	public void init(GameContainer container) throws SlickException {
		this.container = container;
	
		rect = new Rectangle(400,100,200,150);
		round = new RoundedRectangle(150,100,200,150,50);
		round2 = new RoundedRectangle(150,300,200,150,50);
		center = new Rectangle(350,250,100,100);
		
		poly = new Polygon();
		poly.addPoint(400,350);
		poly.addPoint(550,320);
		poly.addPoint(600,380);
		poly.addPoint(620,450);
		poly.addPoint(500,450);
		
		gradient = new GradientFill(0,-75,Color.red,0,75,Color.yellow,true);
		gradient2 = new GradientFill(0,-75,Color.blue,0,75,Color.white,true);
		gradient4 = new GradientFill(-50,-40,Color.green,50,40,Color.cyan,true);
	}

	
	public void render(GameContainer container, Graphics g) {
		
		g.rotate(400, 300, ang);
		g.fill(rect, gradient);
		g.fill(round, gradient);
		g.fill(poly, gradient2);
		g.fill(center, gradient4);

		g.setAntiAlias(true);
		g.setLineWidth(10);
		g.draw(round2, gradient2);
		g.setLineWidth(2);
		g.draw(poly, gradient);
		g.setAntiAlias(false);
		
		g.fill(center, gradient4);
		g.setAntiAlias(true);
		g.setColor(Color.black);
		g.draw(center);
		g.setAntiAlias(false);
	}

	
	public void update(GameContainer container, int delta) {
		ang += (delta * 0.01f);
	}

	
	public static void main(String[] argv) {
		try {
			Renderer.setRenderer(Renderer.VERTEX_ARRAY_RENDERER);
			
			AppGameContainer container = new AppGameContainer(new GradientTest());
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
