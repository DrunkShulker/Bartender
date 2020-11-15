package org.newdawn.slick.tests;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.MorphShape;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Transform;


public class MorphShapeTest extends BasicGame {
	
	private Shape a;
	
	private Shape b;
	
	private Shape c;
	
	private MorphShape morph;
	
	private float time;
	
	
	public MorphShapeTest() {
		super("MorphShapeTest");
	}

	
	public void init(GameContainer container) throws SlickException {
		a = new Rectangle(100,100,50,200);
		a = a.transform(Transform.createRotateTransform(0.1f,100,100));
		b = new Rectangle(200,100,50,200);
		b = b.transform(Transform.createRotateTransform(-0.6f,100,100));
		c = new Rectangle(300,100,50,200);
		c = c.transform(Transform.createRotateTransform(-0.2f,100,100));
		
		morph = new MorphShape(a);
		morph.addShape(b);
		morph.addShape(c);
		
		container.setVSync(true);
	}

	
	public void update(GameContainer container, int delta)
			throws SlickException {
		time += delta * 0.001f;
		morph.setMorphTime(time);
	}

	
	public void render(GameContainer container, Graphics g)
			throws SlickException {
		g.setColor(Color.green);
		g.draw(a);
		g.setColor(Color.red);
		g.draw(b);
		g.setColor(Color.blue);
		g.draw(c);
		g.setColor(Color.white);
		g.draw(morph);
	}

	
	public static void main(String[] argv) {
		try {
			AppGameContainer container = new AppGameContainer(
					new MorphShapeTest());
			container.setDisplayMode(800, 600, false);
			container.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
}
