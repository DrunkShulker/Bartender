package org.newdawn.slick.geom;

import java.util.ArrayList;


public class BasicTriangulator implements Triangulator {
	
	private static final float EPSILON = 0.0000000001f;
	
	private PointList poly = new PointList();
	
	private PointList tris = new PointList();
	
	private boolean tried;
	
	
	public BasicTriangulator() {
	}
	
	
	public void addPolyPoint(float x, float y) {
		Point p = new Point(x,y);
		if (!poly.contains(p)) {
			poly.add(p);
		}
	}
	
	
	public int getPolyPointCount() {
		return poly.size();
	}

	
	public float[] getPolyPoint(int index) {
		return new float[] {poly.get(index).x,poly.get(index).y};
	}
	
	
	public boolean triangulate() {
		tried = true;
		
		boolean worked = process(poly,tris);
		return worked;
	}
	
	
	public int getTriangleCount() {
		if (!tried) {
			throw new RuntimeException("Call triangulate() before accessing triangles");
		}
		return tris.size() / 3;
	}
	
	
	public float[] getTrianglePoint(int tri, int i) {
		if (!tried) {
			throw new RuntimeException("Call triangulate() before accessing triangles");
		}
		
		return tris.get((tri*3)+i).toArray();
	}
	
	
	private float area(PointList contour) {
		int n = contour.size();

		float A = 0.0f;

		for (int p = n - 1, q = 0; q < n; p = q++) {
			Point contourP = contour.get(p);
			Point contourQ = contour.get(q);

			A += contourP.getX() * contourQ.getY() - contourQ.getX()
					* contourP.getY();
		}
		return A * 0.5f;
	}

	
	private boolean insideTriangle(float Ax, float Ay, float Bx,
			float By, float Cx, float Cy, float Px, float Py) {
		float ax, ay, bx, by, cx, cy, apx, apy, bpx, bpy, cpx, cpy;
		float cCROSSap, bCROSScp, aCROSSbp;

		ax = Cx - Bx;
		ay = Cy - By;
		bx = Ax - Cx;
		by = Ay - Cy;
		cx = Bx - Ax;
		cy = By - Ay;
		apx = Px - Ax;
		apy = Py - Ay;
		bpx = Px - Bx;
		bpy = Py - By;
		cpx = Px - Cx;
		cpy = Py - Cy;

		aCROSSbp = ax * bpy - ay * bpx;
		cCROSSap = cx * apy - cy * apx;
		bCROSScp = bx * cpy - by * cpx;

		return ((aCROSSbp >= 0.0f) && (bCROSScp >= 0.0f) && (cCROSSap >= 0.0f));
	}

	
	private boolean snip(PointList contour, int u, int v, int w, int n,
			int[] V) {
		int p;
		float Ax, Ay, Bx, By, Cx, Cy, Px, Py;

		Ax = contour.get(V[u]).getX();
		Ay = contour.get(V[u]).getY();

		Bx = contour.get(V[v]).getX();
		By = contour.get(V[v]).getY();

		Cx = contour.get(V[w]).getX();
		Cy = contour.get(V[w]).getY();

		if (EPSILON > (((Bx - Ax) * (Cy - Ay)) - ((By - Ay) * (Cx - Ax)))) {
			return false;
		}

		for (p = 0; p < n; p++) {
			if ((p == u) || (p == v) || (p == w)) {
				continue;
			}

			Px = contour.get(V[p]).getX();
			Py = contour.get(V[p]).getY();

			if (insideTriangle(Ax, Ay, Bx, By, Cx, Cy, Px, Py)) {
				return false;
			}
		}

		return true;
	}

	
	private boolean process(PointList contour, PointList result) {
		result.clear();
		
		

		int n = contour.size();
		if (n < 3)
			return false;

		int[] V = new int[n];

		

		if (0.0f < area(contour)) {
			for (int v = 0; v < n; v++)
				V[v] = v;
		} else {
			for (int v = 0; v < n; v++)
				V[v] = (n - 1) - v;
		}

		int nv = n;

		
		int count = 2 * nv; 

		for (int m = 0, v = nv - 1; nv > 2;) {
			
			if (0 >= (count--)) {
				
				return false;
			}

			
			int u = v;
			if (nv <= u)
				u = 0; 
			v = u + 1;
			if (nv <= v)
				v = 0; 
			int w = v + 1;
			if (nv <= w)
				w = 0; 

			if (snip(contour, u, v, w, nv, V)) {
				int a, b, c, s, t;

				
				a = V[u];
				b = V[v];
				c = V[w];

				
				result.add(contour.get(a));
				result.add(contour.get(b));
				result.add(contour.get(c));

				m++;

				
				for (s = v, t = v + 1; t < nv; s++, t++) {
					V[s] = V[t];
				}
				nv--;

				
				count = 2 * nv;
			}
		}

		return true;
	}

	
	private class Point {
		
		private float x;
		
		private float y;
		
		private float[] array;
		
		
		public Point(float x, float y) {
			this.x = x;
			this.y = y;
			array = new float[] {x,y};
		}

		
		public float getX() {
			return x;
		}

		
		public float getY() {
			return y;
		}
	
		
		public float[] toArray() {
			return array;
		}
		
		
		public int hashCode() {
			return (int) (x * y * 31);
		}
		
		
		public boolean equals(Object other) {
			if (other instanceof Point) {
				Point p = (Point) other;
				return (p.x == x) && (p.y == y);
			}
			
			return false;
		}
	}
	
	
	private class PointList {
		
		private ArrayList points = new ArrayList();
		
		
		public PointList() {
		}
		
		
		public boolean contains(Point p) {
			return points.contains(p);
		}
		
		
		public void add(Point point) {
			points.add(point);
		}
		
		
		public void remove(Point point) {
			points.remove(point);
		}
		
		
		public int size() {
			return points.size();
		}
		
		
		public Point get(int i) {
			return (Point) points.get(i);
		}
		
		
		public void clear() {
			points.clear();
		}
	}

	
	public void startHole() {
		
		
	}
}
