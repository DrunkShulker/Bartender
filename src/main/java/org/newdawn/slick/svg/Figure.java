package org.newdawn.slick.svg;

import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Transform;


public class Figure {
	
	public static final int ELLIPSE = 1;
	
	public static final int LINE = 2;
	
	public static final int RECTANGLE = 3;
	
	public static final int PATH = 4;
	
	public static final int POLYGON = 5;
	
	
	private int type;
	
	
	private Shape shape;
	
	private NonGeometricData data;
	
	private Transform transform;
	
	
	public Figure(int type, Shape shape, NonGeometricData data, Transform transform) {
		this.shape = shape;
		this.data = data;
		this.type = type;
		this.transform = transform;
	}

	
	public Transform getTransform() {
		return transform;
	}
	
	
	public int getType() {
		return type;
	}
	
	
	public Shape getShape() {
		return shape;
	}
	
	
	public NonGeometricData getData() {
		return data;
	}
}
