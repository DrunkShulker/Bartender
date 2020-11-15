package org.newdawn.slick.tests;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Path;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.opengl.renderer.Renderer;


public class LineRenderTest extends BasicGame {
	
	private Polygon polygon = new Polygon();
	
	private Path path = new Path(100,100);
	
	private float width = 10;
	
	private boolean antialias = true;
	
	
	public LineRenderTest() {
		super("LineRenderTest");
	}
	
	
	public void init(GameContainer container) throws SlickException {
		polygon.addPoint(100,100);
		polygon.addPoint(200,80);
		polygon.addPoint(320,150);
		polygon.addPoint(230,210);
		polygon.addPoint(170,260);
		
		path.curveTo(200,200,200,100,100,200);
		path.curveTo(400,100,400,200,200,100);
		path.curveTo(500,500,400,200,200,100);
	}

	
	public void update(GameContainer container, int delta) throws SlickException {
		if (container.getInput().isKeyPressed(Input.KEY_SPACE)) {
			antialias = !antialias;
		}
	}

	
	public void render(GameContainer container, Graphics g) throws SlickException {
		g.setAntiAlias(antialias);
		g.setLineWidth(50);
		g.setColor(Color.red);
		g.draw(path);
		














	}

	
	public static void main(String[] argv) {
		try {
			Renderer.setLineStripRenderer(Renderer.QUAD_BASED_LINE_STRIP_RENDERER);
			Renderer.getLineStripRenderer().setLineCaps(true);
			
			AppGameContainer container = new AppGameContainer(new LineRenderTest());
			container.setDisplayMode(800,600,false);
			container.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
}
