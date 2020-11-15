package org.newdawn.slick.util.pathfinding.navmesh;

import java.util.ArrayList;
import java.util.HashMap;


public class Space {
	
	private float x;
	
	private float y;
	
	private float width;
	
	private float height;
	
	
	private HashMap links = new HashMap();
	
	private ArrayList linksList = new ArrayList();
	
	private float cost;
	
	
	public Space(float x, float y, float width, float height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	
	public float getWidth() {
		return width;
	}

	
	public float getHeight() {
		return height;
	}

	
	public float getX() {
		return x;
	}

	
	public float getY() {
		return y;
	}
	
	
	public void link(Space other) {
		
		if (inTolerance(x,other.x+other.width) || inTolerance(x+width, other.x)) {
			float linkx = x;
			if (x+width == other.x) {
				linkx = x+width;
			}
			
			float top = Math.max(y, other.y);
			float bottom = Math.min(y+height, other.y+other.height);
			float linky = top + ((bottom-top)/2);
			
			Link link = new Link(linkx, linky, other);
			links.put(other,link);
			linksList.add(link);
		}
		
		if (inTolerance(y, other.y+other.height) || inTolerance(y+height, other.y)) {
			float linky = y;
			if (y+height == other.y) {
				linky = y+height;
			}
			
			float left = Math.max(x, other.x);
			float right = Math.min(x+width, other.x+other.width);
			float linkx = left + ((right-left)/2);
			
			Link link = new Link(linkx, linky, other);
			links.put(other, link);
			linksList.add(link);
		}		
	}
	
	
	private boolean inTolerance(float a, float b) {
		return a == b;
	}
	
	
	public boolean hasJoinedEdge(Space other) {
		
		if (inTolerance(x,other.x+other.width) || inTolerance(x+width,other.x)) {
			if ((y >= other.y) && (y <= other.y + other.height)) {
				return true;
			}
			if ((y+height >= other.y) && (y+height <= other.y + other.height)) {
				return true;
			}
			if ((other.y >= y) && (other.y <= y + height)) {
				return true;
			}
			if ((other.y+other.height >= y) && (other.y+other.height <= y + height)) {
				return true;
			}
		}
		
		if (inTolerance(y, other.y+other.height) || inTolerance(y+height, other.y)) {
			if ((x >= other.x) && (x <= other.x + other.width)) {
				return true;
			}
			if ((x+width >= other.x) && (x+width <= other.x + other.width)) {
				return true;
			}
			if ((other.x >= x) && (other.x <= x + width)) {
				return true;
			}
			if ((other.x+other.width >= x) && (other.x+other.width <= x + width)) {
				return true;
			}
		}
		
		return false;
	}
	
	
	public Space merge(Space other) {
		float minx = Math.min(x, other.x);
		float miny = Math.min(y, other.y);
		
		float newwidth = width+other.width;
		float newheight = height+other.height;
		if (x == other.x) {
			newwidth = width;
		} else {
			newheight = height;
		}
		return new Space(minx, miny, newwidth, newheight);
	}
	
	
	public boolean canMerge(Space other) {
		if (!hasJoinedEdge(other)) {
			return false;
		}
		
		if ((x == other.x) && (width == other.width)) {
			return true;
		}
		if ((y == other.y) && (height == other.height)) {
			return true;
		}
		
		return false;
	}
	
	
	public int getLinkCount() {
		return linksList.size();
	}
	
	
	public Link getLink(int index) {
		return (Link) linksList.get(index);
	}
	
	
	public boolean contains(float xp, float yp) {
		return (xp >= x) && (xp < x+width) && (yp >= y) && (yp < y+height);
	}
	
	
	public void fill(Space target, float sx, float sy, float cost) {
		if (cost >= this.cost) {
			return;
		}
		this.cost = cost;
		if (target == this) {
			return;
		}
		
		for (int i=0;i<getLinkCount();i++) {
			Link link = getLink(i);
			float extraCost = link.distance2(sx,sy);
			float nextCost = cost + extraCost;
			link.getTarget().fill(target, link.getX(), link.getY(), nextCost);
		}
	}
	
	
	public void clearCost() {
		cost = Float.MAX_VALUE;
	}

	
	public float getCost() {
		return cost;
	}

	
	public boolean pickLowestCost(Space target, NavPath path) {
		if (target == this) {
			return true;
		}
		if (links.size() == 0) {
			return false;
		}

		Link bestLink = null;	
		for (int i=0;i<getLinkCount();i++) {
			Link link = getLink(i);
			if ((bestLink == null) || (link.getTarget().getCost() < bestLink.getTarget().getCost())) {
				bestLink = link;
			}
		}
		
		path.push(bestLink);
		return bestLink.getTarget().pickLowestCost(target, path);
	}
	
	
	public String toString() {
		return "[Space "+x+","+y+" "+width+","+height+"]";
	}
}
