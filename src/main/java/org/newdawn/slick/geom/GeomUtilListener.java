package org.newdawn.slick.geom;


public interface GeomUtilListener {
	
	public void pointExcluded(float x, float y);

	
	public void pointIntersected(float x, float y);

	
	public void pointUsed(float x, float y);
}
