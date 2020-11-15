package org.newdawn.slick.fills;

import org.newdawn.slick.Color;
import org.newdawn.slick.ShapeFill;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Vector2f;


public class GradientFill implements ShapeFill {
	
	private Vector2f none = new Vector2f(0,0);
	
	private Vector2f start;
	
	private Vector2f end;
	
	private Color startCol;
	
	private Color endCol;
	
	private boolean local = false;
	
	
	public GradientFill(float sx, float sy, Color startCol, float ex, float ey, Color endCol) 
	{
		this(sx,sy,startCol,ex,ey,endCol,false);
	}

	
	public GradientFill(float sx, float sy, Color startCol, float ex, float ey, Color endCol, boolean local) 
	{
		this(new Vector2f(sx,sy), startCol, new Vector2f(ex,ey), endCol, local);
	}
	
	
	public GradientFill(Vector2f start, Color startCol, Vector2f end, Color endCol, boolean local) {
		this.start = new Vector2f(start);
		this.end = new Vector2f(end);
		this.startCol = new Color(startCol);
		this.endCol = new Color(endCol);
		this.local = local;
	}
	
	
	public GradientFill getInvertedCopy() {
		return new GradientFill(start, endCol, end, startCol, local);
	}
	
	
	public void setLocal(boolean local) {
		this.local = local;
	}
	
	
	public Vector2f getStart() {
		return start;
	}

	
	public Vector2f getEnd() {
		return end;
	}
	
	
	public Color getStartColor() {
		return startCol;
	}

	
	public Color getEndColor() {
		return endCol;
	}
	
	
	public void setStart(float x, float y) {
		setStart(new Vector2f(x,y));
	}
	
	
	public void setStart(Vector2f start) {
		this.start = new Vector2f(start);
	}
	
	
	public void setEnd(float x, float y) {
		setEnd(new Vector2f(x,y));
	}
	
	
	public void setEnd(Vector2f end) {
		this.end = new Vector2f(end);
	}
	
	
	public void setStartColor(Color color) {
		this.startCol = new Color(color);
	}

	
	public void setEndColor(Color color) {
		this.endCol = new Color(color);
	}
	
	
	public Color colorAt(Shape shape, float x, float y) {
		if (local) {
			return colorAt(x-shape.getCenterX(),y-shape.getCenterY());
		} else {
			return colorAt(x,y);
		}
	}

	
	public Color colorAt(float x, float y) {
		float dx1 = end.getX() - start.getX();
		float dy1 = end.getY() - start.getY();
		
		float dx2 = -dy1;
		float dy2 = dx1;
		float denom = (dy2 * dx1) - (dx2 * dy1);
		
		if (denom == 0) {
			return Color.black;
		}
		
		float ua = (dx2 * (start.getY() - y)) - (dy2 * (start.getX() - x));
		ua /= denom;
		float ub = (dx1 * (start.getY() - y)) - (dy1 * (start.getX() - x));
		ub /= denom;
		float u = ua;
		if (u < 0) {
			u = 0;
		} 
		if (u > 1) {
			u = 1;
		}
		float v = 1 - u;

		
		Color col = new Color(1,1,1,1);
		col.r = (u * endCol.r) + (v * startCol.r);
		col.b = (u * endCol.b) + (v * startCol.b);
		col.g = (u * endCol.g) + (v * startCol.g);
		col.a = (u * endCol.a) + (v * startCol.a);
		
		return col;
	}

	
	public Vector2f getOffsetAt(Shape shape, float x, float y) {
		return none;
	}
}
