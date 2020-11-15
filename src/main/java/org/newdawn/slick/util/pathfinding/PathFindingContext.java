package org.newdawn.slick.util.pathfinding;


public interface PathFindingContext {
	
	public Mover getMover();
	
	
	public int getSourceX();

	
	public int getSourceY();
	
	
	public int getSearchDistance();
}
