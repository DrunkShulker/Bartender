package org.newdawn.slick.geom;

import java.io.Serializable;


public interface Triangulator extends Serializable {

	
	public int getTriangleCount();
	
	
	public float[] getTrianglePoint(int tri, int i);
	
	
	public void addPolyPoint(float x, float y);
	
	
	public void startHole();
	
	
	public boolean triangulate();
}
