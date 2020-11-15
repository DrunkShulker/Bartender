
package org.newdawn.slick.geom;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;


public class MannTriangulator implements Triangulator {
	
	private static final double EPSILON = 1e-5;
	
	
	protected PointBag contour;
	
	protected PointBag holes;
	
	private PointBag nextFreePointBag;
	
	private Point nextFreePoint;
	
	private List triangles = new ArrayList();
	
	
	public MannTriangulator() {
		contour = getPointBag();
	}
	
	
	public void addPolyPoint(float x, float y) {
    	addPoint(new Vector2f(x,y));
    }

	
	public void reset() {
		while (holes != null) {
			holes = freePointBag(holes);
		}

		contour.clear();
		holes = null;
	}

	
	public void startHole() {
		PointBag newHole = getPointBag();
		newHole.next = holes;
		holes = newHole;
	}

	
	private void addPoint(Vector2f pt) {
		if (holes == null) {
			Point p = getPoint(pt);
			contour.add(p);
		} else {
			Point p = getPoint(pt);
			holes.add(p);
		}
	}

	
	private Vector2f[] triangulate(Vector2f[] result) {
		
		contour.computeAngles();
		for (PointBag hole = holes; hole != null; hole = hole.next) {
			hole.computeAngles();
		}

		
		while (holes != null) {
			Point pHole = holes.first;
			outer: do {
				if (pHole.angle <= 0) {
					Point pContour = contour.first;
					do {
						inner: if (pHole.isInfront(pContour)
								&& pContour.isInfront(pHole)) {
							if (!contour.doesIntersectSegment(pHole.pt,
									pContour.pt)) {
								PointBag hole = holes;
								do {
									if (hole.doesIntersectSegment(pHole.pt,
											pContour.pt)) {
										break inner;
									}
								} while ((hole = hole.next) != null);

								Point newPtContour = getPoint(pContour.pt);
								pContour.insertAfter(newPtContour);

								Point newPtHole = getPoint(pHole.pt);
								pHole.insertBefore(newPtHole);

								pContour.next = pHole;
								pHole.prev = pContour;

								newPtHole.next = newPtContour;
								newPtContour.prev = newPtHole;

								pContour.computeAngle();
								pHole.computeAngle();
								newPtContour.computeAngle();
								newPtHole.computeAngle();

								
								holes.first = null;
								break outer;
							}
						}
					} while ((pContour = pContour.next) != contour.first);
				}
			} while ((pHole = pHole.next) != holes.first);

			
			holes = freePointBag(holes);
		}

		
		int numTriangles = contour.countPoints() - 2;
		int neededSpace = numTriangles * 3 + 1; 
		if (result.length < neededSpace) {
			result = (Vector2f[]) Array.newInstance(result.getClass()
					.getComponentType(), neededSpace);
		}

		
		int idx = 0;
		for (;;) {
			Point pContour = contour.first;

			if (pContour == null) {
				break;
			}
			
			if (pContour.next == pContour.prev) {
				break;
			}

			outer: do {
				if (pContour.angle > 0) {
					Point prev = pContour.prev;
					Point next = pContour.next;

					if (next.next == prev || prev.isInfront(next) && next.isInfront(prev)) {
						if (!contour.doesIntersectSegment(prev.pt, next.pt)) {
							result[idx++] = pContour.pt;
							result[idx++] = next.pt;
							result[idx++] = prev.pt;
							break outer;
						}
					}
				}
			} while ((pContour = pContour.next) != contour.first);
			
			
			Point prev = pContour.prev;
			Point next = pContour.next;

			contour.first = prev;
			pContour.unlink();
			freePoint(pContour);

			next.computeAngle();
			prev.computeAngle();
		}

		
		result[idx] = null;

		
		contour.clear();

		
		return result;
	}

	
	private PointBag getPointBag() {
		PointBag pb = nextFreePointBag;
		if (pb != null) {
			nextFreePointBag = pb.next;
			pb.next = null;
			return pb;
		}
		return new PointBag();
	}

	
	private PointBag freePointBag(PointBag pb) {
		PointBag next = pb.next;
		pb.clear();
		pb.next = nextFreePointBag;
		nextFreePointBag = pb;
		return next;
	}

	
	private Point getPoint(Vector2f pt) {
		Point p = nextFreePoint;
		if (p != null) {
			nextFreePoint = p.next;
			
			p.next = null;
			p.prev = null;
			p.pt = pt;
			return p;
		}
		return new Point(pt);
	}

	
	private void freePoint(Point p) {
		p.next = nextFreePoint;
		nextFreePoint = p;
	}

	
	private void freePoints(Point head) {
		head.prev.next = nextFreePoint;
		head.prev = null;
		nextFreePoint = head;
	}

	
	private static class Point implements Serializable {
		
		protected Vector2f pt;
		
		protected Point prev;
		
		protected Point next;
		
		protected double nx;
		
		protected double ny;
		
		protected double angle;
		
		protected double dist;

		
		public Point(Vector2f pt) {
			this.pt = pt;
		}

		
		public void unlink() {
			prev.next = next;
			next.prev = prev;
			next = null;
			prev = null;
		}

		
		public void insertBefore(Point p) {
			prev.next = p;
			p.prev = prev;
			p.next = this;
			prev = p;
		}

		
		public void insertAfter(Point p) {
			next.prev = p;
			p.prev = this;
			p.next = next;
			next = p;
		}

		
		private double hypot(double x, double y) {
			return Math.sqrt(x*x + y*y);
		}
		
		
		public void computeAngle() {
			if (prev.pt.equals(pt)) {
				pt.x += 0.01f;
			}
			double dx1 = pt.x - prev.pt.x;
			double dy1 = pt.y - prev.pt.y;
			double len1 = hypot(dx1, dy1);
			dx1 /= len1;
			dy1 /= len1;

			if (next.pt.equals(pt)) {
				pt.y += 0.01f;
			}
			double dx2 = next.pt.x - pt.x;
			double dy2 = next.pt.y - pt.y;
			double len2 = hypot(dx2, dy2);
			dx2 /= len2;
			dy2 /= len2;

			double nx1 = -dy1;
			double ny1 = dx1;

			nx = (nx1 - dy2) * 0.5;
			ny = (ny1 + dx2) * 0.5;

			if (nx * nx + ny * ny < EPSILON) {
				nx = dx1;
				ny = dy2;
				angle = 1; 
				if (dx1 * dx2 + dy1 * dy2 > 0) {
					nx = -dx1;
					ny = -dy1;
				}
			} else {
				angle = nx * dx2 + ny * dy2;
			}
		}

		
		public double getAngle(Point p) {
			double dx = p.pt.x - pt.x;
			double dy = p.pt.y - pt.y;
			double dlen = hypot(dx, dy);

			return (nx * dx + ny * dy) / dlen;
		}

		
		public boolean isConcave() {
			return angle < 0;
		}

		
		public boolean isInfront(double dx, double dy) {
			
			
			boolean sidePrev = ((prev.pt.y - pt.y) * dx + (pt.x - prev.pt.x)
					* dy) >= 0;
			boolean sideNext = ((pt.y - next.pt.y) * dx + (next.pt.x - pt.x)
					* dy) >= 0;

			return (angle < 0) ? (sidePrev | sideNext) : (sidePrev & sideNext);
		}

		
		public boolean isInfront(Point p) {
			return isInfront(p.pt.x - pt.x, p.pt.y - pt.y);
		}
	}

	
	protected class PointBag implements Serializable {
		
		protected Point first;
		
		protected PointBag next;

		
		public void clear() {
			if (first != null) {
				freePoints(first);
				first = null;
			}
		}

		
		public void add(Point p) {
			if (first != null) {
				first.insertBefore(p);
			} else {
				first = p;
				p.next = p;
				p.prev = p;
			}
		}

		
		public void computeAngles() {
			if (first == null) {
				return;
			}

			Point p = first;
			do {
				p.computeAngle();
			} while ((p = p.next) != first);
		}

		
		public boolean doesIntersectSegment(Vector2f v1, Vector2f v2) {
			double dxA = v2.x - v1.x;
			double dyA = v2.y - v1.y;

			for (Point p = first;;) {
				Point n = p.next;
				if (p.pt != v1 && n.pt != v1 && p.pt != v2 && n.pt != v2) {
					double dxB = n.pt.x - p.pt.x;
					double dyB = n.pt.y - p.pt.y;
					double d = (dxA * dyB) - (dyA * dxB);

					if (Math.abs(d) > EPSILON) {
						double tmp1 = p.pt.x - v1.x;
						double tmp2 = p.pt.y - v1.y;
						double tA = (dyB * tmp1 - dxB * tmp2) / d;
						double tB = (dyA * tmp1 - dxA * tmp2) / d;

						if (tA >= 0 && tA <= 1 && tB >= 0 && tB <= 1) {
							return true;
						}
					}
				}

				if (n == first) {
					return false;
				}
				p = n;
			}
		}

		
		public int countPoints() {
			if (first == null) {
				return 0;
			}

			int count = 0;
			Point p = first;
			do {
				++count;
			} while ((p = p.next) != first);
			return count;
		}
		
		
		public boolean contains(Vector2f point) {
			if (first == null) {
				return false;
			}
			
			if (first.prev.pt.equals(point)) {
				return true;
			}
			if (first.pt.equals(point)) {
				return true;
			}
			return false;
		}
	}

	public boolean triangulate() {
		Vector2f[] temp = triangulate(new Vector2f[0]);

		for (int i = 0; i < temp.length; i++) {
			if (temp[i] == null) {
				break;
			} else {
				triangles.add(temp[i]);
			}
		}

		return true;
	}

	
	public int getTriangleCount() {
		return triangles.size() / 3;
	}

	
	public float[] getTrianglePoint(int tri, int i) {
		Vector2f pt = (Vector2f) triangles.get((tri * 3) + i);

		return new float[] { pt.x, pt.y };
	}

}
