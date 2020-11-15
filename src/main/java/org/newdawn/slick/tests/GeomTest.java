package org.newdawn.slick.tests;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Ellipse;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.RoundedRectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.opengl.renderer.Renderer;


public class GeomTest extends BasicGame {
	
	private Shape rect = new Rectangle(100,100,100,100);
	
	private Shape circle = new Circle(500,200,50);
	
	private Shape rect1 = new Rectangle(150,120,50,100).transform(Transform.createTranslateTransform(50, 50));
	
	private Shape rect2 = new Rectangle(310,210,50,100).transform(
            Transform.createRotateTransform((float)Math.toRadians(45), 335, 260));
	
	private Shape circle1 = new Circle(150,90,30);
	
	private Shape circle2 = new Circle(310,110,70);
	
	private Shape circle3 = new Ellipse(510, 150, 70, 70);
	
	private Shape circle4 = new Ellipse(510, 350, 30, 30).transform(
            Transform.createTranslateTransform(-510, -350)).transform(
                    Transform.createScaleTransform(2, 2)).transform(
                            Transform.createTranslateTransform(510, 350));
	
    private Shape roundRect = new RoundedRectangle(50, 175, 100, 100, 20);
	
    private Shape roundRect2 = new RoundedRectangle(50, 280, 50, 50, 20, 20, RoundedRectangle.TOP_LEFT | RoundedRectangle.BOTTOM_RIGHT);

    
	public GeomTest() {
		super("Geom Test");
	}
	
	
	public void init(GameContainer container) throws SlickException {
	}

	
	public void render(GameContainer container, Graphics g) {
		g.setColor(Color.white);
		g.drawString("Red indicates a collision, green indicates no collision", 50, 420);
        g.drawString("White are the targets", 50, 435);

        g.pushTransform();
        g.translate(100,100);
        g.pushTransform();
        g.translate(-50,-50);
        g.scale(10, 10);
        g.setColor(Color.red);
        g.fillRect(0,0,5,5);
        g.setColor(Color.white);
        g.drawRect(0,0,5,5);
        g.popTransform();
        g.setColor(Color.green);
        g.fillRect(20,20,50,50);
        g.popTransform();
        
		g.setColor(Color.white);
		g.draw(rect);
		g.draw(circle);
		
		g.setColor(rect1.intersects(rect) ? Color.red : Color.green);
		g.draw(rect1);
		g.setColor(rect2.intersects(rect) ? Color.red : Color.green);
		g.draw(rect2);
        g.setColor(roundRect.intersects(rect) ? Color.red : Color.green);
        g.draw(roundRect);
		g.setColor(circle1.intersects(rect) ? Color.red : Color.green);
		g.draw(circle1);
		g.setColor(circle2.intersects(rect) ? Color.red : Color.green);
		g.draw(circle2);
		g.setColor(circle3.intersects(circle) ? Color.red : Color.green);
		g.fill(circle3);
		g.setColor(circle4.intersects(circle) ? Color.red : Color.green);
		g.draw(circle4);

        g.fill(roundRect2);
		g.setColor(Color.blue);
        g.draw(roundRect2);
		g.setColor(Color.blue);
		g.draw(new Circle(100,100,50));
		g.drawRect(50,50,100,100);
        
	}

	
	public void update(GameContainer container, int delta) {
	}

	
	public void keyPressed(int key, char c) {
		if (key == Input.KEY_ESCAPE) {
			System.exit(0);
		}
	}
	
	
	public static void main(String[] argv) {
		try {
			Renderer.setRenderer(Renderer.VERTEX_ARRAY_RENDERER);
			
			AppGameContainer container = new AppGameContainer(new GeomTest());
			container.setDisplayMode(800,600,false);
			container.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
}