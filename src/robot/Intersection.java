package robot;

/**
 * Contains the x,y coordinates of the intersection, and the status of the
 * intersection (forbidden, target, unreachable, etc.)
 * 
 * @author Keith MacKinnon
 * 
 */

public class Intersection {

	private int x, y;
	private boolean isTarget;
	private Intersection previous;
	private double minDistance = Double.POSITIVE_INFINITY;

	public Intersection(int x, int y) {

		this.x = x;
		this.y = y;

		isTarget = false;

		previous = null;

	}

	public void setPreviousToNull() {
		previous = null;
	}

	public void setPrevious(Intersection intersection) {
		previous = intersection;
	}

	public Intersection getPrevious() {
		return previous;
	}

	public void setMinDistance(double min) {
		minDistance = min;
	}

	public double getMinDistance() {
		return minDistance;
	}

	public void setAsTarget() {
		isTarget = true;
	}

	public boolean isTarget() {
		return isTarget;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
	public static Intersection convertToIntersection(double x, double y){
		
		int xGrid = (int) Math.round(x/Map.TILE_SIZE);
		int yGrid = (int) Math.round(y/Map.TILE_SIZE);
		
		return new Intersection(xGrid, yGrid);
		
	}
	
	public double getXInCm(){
		return x*Map.TILE_SIZE;
	}
	
	public double getYInCm(){
		return y*Map.TILE_SIZE;
	}
	

	public boolean equals(Object obj) {

		boolean result = false;

		if (obj instanceof Intersection) {
			Intersection otherIntersection = (Intersection) obj;

			if (otherIntersection.getX() == x && otherIntersection.getY() == y) {
				result = true;
			}
		}

		return result;

	}

}
