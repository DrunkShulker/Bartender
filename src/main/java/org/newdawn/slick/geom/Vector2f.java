package org.newdawn.slick.geom;

import java.io.Serializable;

import org.newdawn.slick.util.FastTrig;


public strictfp class Vector2f implements Serializable {
	
	private static final long serialVersionUID = 1339934L;
	
	
	public float x;
	
	public float y;
	
	
	public Vector2f() {
	}

	
	public Vector2f(float[] coords) {
		x = coords[0];
		y = coords[1];
	}
	
	
	public Vector2f(double theta) {
		x = 1;
		y = 0;
		setTheta(theta);
	}

	
	public void setTheta(double theta) {
		
		
		if ((theta < -360) || (theta > 360)) {
			theta = theta % 360;
		}
		if (theta < 0) {
			theta = 360 + theta;
		}
		double oldTheta = getTheta();
		if ((theta < -360) || (theta > 360)) {
			oldTheta = oldTheta % 360;
		}
		if (theta < 0) {
			oldTheta = 360 + oldTheta;
		}

		float len = length();
		x = len * (float) FastTrig.cos(StrictMath.toRadians(theta));
		y = len * (float) FastTrig.sin(StrictMath.toRadians(theta));
		




	} 
	
	
	public Vector2f add(double theta) {
		setTheta(getTheta() + theta);
		
		return this;
	}

	
	public Vector2f sub(double theta) {
		setTheta(getTheta() - theta);
		
		return this;
	}
	
	
	public double getTheta() {
		double theta = StrictMath.toDegrees(StrictMath.atan2(y, x));
		if ((theta < -360) || (theta > 360)) {
			theta = theta % 360;
		}
		if (theta < 0) {
			theta = 360 + theta;
		}

		return theta;
	} 
	
	
	public float getX() {
		return x;
	}

	
	public float getY() {
		return y;
	}
	
	
	public Vector2f(Vector2f other) {
		this(other.getX(),other.getY());
	}
	
	
	public Vector2f(float x, float y) {
		this.x = x;
		this.y = y;
	}

	
	public void set(Vector2f other) {
		set(other.getX(),other.getY());
	}
	
	
	public float dot(Vector2f other) {
		return (x * other.getX()) + (y * other.getY());
	}
	
	
	public Vector2f set(float x, float y) { 
		this.x = x; 
		this.y = y; 
		
		return this;
	}
	
	
	public Vector2f getPerpendicular() {
	   return new Vector2f(-y, x);
	}
	
	
	public Vector2f set(float[] pt) {
		return set(pt[0], pt[1]);
	}
	
	
	public Vector2f negate() {
		return new Vector2f(-x, -y); 
	}

	
	public Vector2f negateLocal() {
		x = -x;
		y = -y;
		
		return this;
	}
	
	
	public Vector2f add(Vector2f v)
	{
		x += v.getX(); 
		y += v.getY();
		
		return this;
	}
	
	
	public Vector2f sub(Vector2f v)
	{
		x -= v.getX(); 
		y -= v.getY();
		
		return this;
	}

	
	public Vector2f scale(float a)
	{
		x *= a; 
		y *= a;
		
		return this;
	}

	
	public Vector2f normalise() {
		float l = length();
		
		if (l == 0) {
			return this;
		}
		
		x /= l;
		y /= l;
		return this;
	}
	
	
    public Vector2f getNormal() {
	   Vector2f cp = copy();
	   cp.normalise();
	   return cp;
    } 
    
	
	public float lengthSquared() {
		return (x * x) + (y * y);
	}
	
	
	public float length() 
	{
		return (float) Math.sqrt(lengthSquared());
	}
	
	
	public void projectOntoUnit(Vector2f b, Vector2f result) {
		float dp = b.dot(this);
		
		result.x = dp * b.getX();
		result.y = dp * b.getY();
		
	}
	
	
	public Vector2f copy() {
		return new Vector2f(x,y);
	}
	
	
	public String toString() {
		return "[Vector2f "+x+","+y+" ("+length()+")]";
	}
	
	
	public float distance(Vector2f other) {
		return (float) Math.sqrt(distanceSquared(other));
	}
	
	
	public float distanceSquared(Vector2f other) {
		float dx = other.getX() - getX();
		float dy = other.getY() - getY();
		
		return (float) (dx*dx)+(dy*dy);
	}
	
	
	public int hashCode() {
        return 997 * ((int)x) ^ 991 * ((int)y); 
	}
	
	
	public boolean equals(Object other) {
		if (other instanceof Vector2f) {
			Vector2f o = ((Vector2f) other);
			return (o.x == x) && (o.y == y);
		}
		
		return false;
	}
}
