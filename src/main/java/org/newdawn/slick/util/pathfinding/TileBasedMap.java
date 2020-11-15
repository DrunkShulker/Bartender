package org.newdawn.slick.util.pathfinding;


public interface TileBasedMap {
	
	public int getWidthInTiles();

	
	public int getHeightInTiles();
	
	
	public void pathFinderVisited(int x, int y);
	
	
	public boolean blocked(PathFindingContext context, int tx, int ty);
	
	
	public float getCost(PathFindingContext context, int tx, int ty);
}
