package org.newdawn.slick;

import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Vector2f;


public interface ShapeFill {

	
	public Color colorAt(Shape shape, float x, float y);

	
	public Vector2f getOffsetAt(Shape shape, float x, float y);
}
