package org.newdawn.slick.util.pathfinding;


public interface PathFinder {

	
	public Path findPath(Mover mover, int sx, int sy, int tx, int ty);
}
