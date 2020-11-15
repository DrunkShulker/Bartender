package org.newdawn.slick.svg;

import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.ImageBuffer;
import org.newdawn.slick.geom.Transform;


public class Gradient {
	
	private String name;
	
	private ArrayList steps = new ArrayList();
	
	private float x1;
	
	private float x2;
	
	private float y1;
	
	private float y2;
	
	private float r;
	
	private Image image;
	
	private boolean radial;
	
	private Transform transform;
	
	private String ref;
	
	
	public Gradient(String name, boolean radial) {
		this.name = name;
		this.radial = radial;
	}
	
	
	public boolean isRadial() {
		return radial;
	}
	
	
	public void setTransform(Transform trans) {
		this.transform = trans;
	}
	
	
	public Transform getTransform() {
		return transform;
	}
	
	
	public void reference(String ref) {
		this.ref = ref;
	}
	
	
	public void resolve(Diagram diagram) {
		if (ref == null) {
			return;
		}
		
		Gradient other = diagram.getGradient(ref);
		
		for (int i=0;i<other.steps.size();i++) {
			steps.add(other.steps.get(i));
		}
	}
	
	
	public void genImage() {
		if (image == null) {
			ImageBuffer buffer = new ImageBuffer(128,16);
			for (int i=0;i<128;i++) {
				Color col = getColorAt(i / 128.0f);
				for (int j=0;j<16;j++) {
					buffer.setRGBA(i, j, col.getRedByte(), col.getGreenByte(), col.getBlueByte(), col.getAlphaByte());
				}
			}
			image = buffer.getImage();
		}
	}
	
	
	public Image getImage() {
		genImage();
		
		return image;
	}

	
	public void setR(float r) {
		this.r = r;
	}
	
	
	public void setX1(float x1) {
		this.x1 = x1;
	}

	
	public void setX2(float x2) {
		this.x2 = x2;
	}

	
	public void setY1(float y1) {
		this.y1 = y1;
	}

	
	public void setY2(float y2) {
		this.y2 = y2;
	}

	
	public float getR() {
		return r;
	}
	
	
	public float getX1() {
		return x1;
	}

	
	public float getX2() {
		return x2;
	}

	
	public float getY1() {
		return y1;
	}

	
	public float getY2() {
		return y2;
	}
	
	
	public void addStep(float location, Color c) {
		steps.add(new Step(location, c));
	}
	
	
	public Color getColorAt(float p) {
		if (p <= 0) {
			return ((Step) steps.get(0)).col;
		}
		if (p > 1) {
			return ((Step) steps.get(steps.size()-1)).col;
		}
		
		for (int i=1;i<steps.size();i++) {
			Step prev = ((Step) steps.get(i-1));
			Step current = ((Step) steps.get(i));
			
			if (p <= current.location) {
				float dis = current.location - prev.location;
				p -= prev.location;
				float v = p / dis;
				
				Color c = new Color(1,1,1,1);
				c.a = (prev.col.a * (1 - v)) + (current.col.a * (v));
				c.r = (prev.col.r * (1 - v)) + (current.col.r * (v));
				c.g = (prev.col.g * (1 - v)) + (current.col.g * (v));
				c.b = (prev.col.b * (1 - v)) + (current.col.b * (v));
				
				return c;
			}
		}

		
		return Color.black;
	}
	
	
	private class Step {
		
		float location;
		
		Color col;
		
		
		public Step(float location, Color c) {
			this.location = location;
			this.col = c;
		}
	}
}
