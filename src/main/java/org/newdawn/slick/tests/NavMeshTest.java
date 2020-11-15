package org.newdawn.slick.tests;

import java.io.IOException;

import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.util.Bootstrap;
import org.newdawn.slick.util.ResourceLoader;
import org.newdawn.slick.util.pathfinding.Mover;
import org.newdawn.slick.util.pathfinding.PathFindingContext;
import org.newdawn.slick.util.pathfinding.TileBasedMap;
import org.newdawn.slick.util.pathfinding.navmesh.Link;
import org.newdawn.slick.util.pathfinding.navmesh.NavMesh;
import org.newdawn.slick.util.pathfinding.navmesh.NavMeshBuilder;
import org.newdawn.slick.util.pathfinding.navmesh.NavPath;
import org.newdawn.slick.util.pathfinding.navmesh.Space;


public class NavMeshTest extends BasicGame implements PathFindingContext {
	
	private NavMesh navMesh;
	
	private NavMeshBuilder builder;
	
	private boolean showSpaces = true;
	
	private boolean showLinks = true;
	
	private NavPath path;
	
	
	private float sx;
	
	private float sy;
	
	private float ex;
	
	private float ey;
	
	private DataMap dataMap;
	
	
	public NavMeshTest() {
		super("Nav-mesh Test");
	}

	
	public void init(GameContainer container) throws SlickException {
		container.setShowFPS(false);

		try {
			dataMap = new DataMap("testdata/map.dat");
		} catch (IOException e) {
			throw new SlickException("Failed to load map data", e);
		}
		builder = new NavMeshBuilder();
		navMesh = builder.build(dataMap);
		
		System.out.println("Navmesh shapes: "+navMesh.getSpaceCount());
	}
	
	
	public void update(GameContainer container, int delta)
			throws SlickException {
		if (container.getInput().isKeyPressed(Input.KEY_1)) {
			showLinks = !showLinks;
		}
		if (container.getInput().isKeyPressed(Input.KEY_2)) {
			showSpaces = !showSpaces;
		}
	}

	
	public void render(GameContainer container, Graphics g)
			throws SlickException {
		g.translate(50,50);
		for (int x=0;x<50;x++) {
			for (int y=0;y<50;y++) {
				if (dataMap.blocked(this, x, y)) {
					g.setColor(Color.gray);
					g.fillRect((x*10)+1,(y*10)+1,8,8);
				}
			}
		}
		
		if (showSpaces) {
			for (int i=0;i<navMesh.getSpaceCount();i++) {
				Space space = navMesh.getSpace(i);
				if (builder.clear(dataMap, space)) {
					g.setColor(new Color(1,1,0,0.5f));
					g.fillRect(space.getX()*10, space.getY()*10, space.getWidth()*10, space.getHeight()*10);
				}
				g.setColor(Color.yellow);
				g.drawRect(space.getX()*10, space.getY()*10, space.getWidth()*10, space.getHeight()*10);

				if (showLinks) {
					int links = space.getLinkCount();
					for (int j=0;j<links;j++) {
						Link link = space.getLink(j);
						g.setColor(Color.red);
						g.fillRect((link.getX()*10)-2, (link.getY()*10)-2,5,5);
					}
				}
			}
		}
		
		if (path != null) {
			g.setColor(Color.white);
			for (int i=0;i<path.length()-1;i++) {
				g.drawLine(path.getX(i)*10, path.getY(i)*10, path.getX(i+1)*10, path.getY(i+1)*10);
			}
		}
	}
	
	
	public Mover getMover() {
		return null;
	}

	
	public int getSearchDistance() {
		return 0;
	}

	
	public int getSourceX() {
		return 0;
	}

	
	public int getSourceY() {
		return 0;
	}

	
	public void mousePressed(int button, int x, int y) {
		float mx = (x - 50) / 10.0f;
		float my = (y - 50) / 10.0f;
		
		if (button == 0) {
			sx = mx;
			sy = my;
		} else {
			ex = mx;
			ey = my;
		}

		path = navMesh.findPath(sx,sy,ex,ey,true);
	}

	
	private class DataMap implements TileBasedMap {
		
		private byte[] map = new byte[50*50];
	
		
		public DataMap(String ref) throws IOException {
			ResourceLoader.getResourceAsStream(ref).read(map);
		}

		
		public boolean blocked(PathFindingContext context, int tx, int ty) {
			if ((tx < 0) || (ty < 0) || (tx >= 50) || (ty >= 50)) {
				return false;
			}
			
			return map[tx+(ty*50)] != 0;
		}
		
		
		public float getCost(PathFindingContext context, int tx, int ty) {
			return 1;
		}

		
		public int getHeightInTiles() {
			return 50;
		}

		
		public int getWidthInTiles() {
			return 50;
		}

		
		public void pathFinderVisited(int x, int y) {
		}
	}
	
	
	public static void main(String[] argv) {
		Bootstrap.runAsApplication(new NavMeshTest(), 600, 600, false);
	}
}
