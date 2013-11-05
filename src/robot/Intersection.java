package robot;

/**
 * Contains the x,y coordinates of the intersection, and the status of the
 * intersection (forbidden, target, unreachable, etc.)
 * 
 * @author Keith MacKinnon
 * 
 */

public class Intersection {

	private Coordinates coordinates;
	private boolean isForbidden;
	private boolean isTarget;

	public Intersection(int x, int y) {

		coordinates = new Coordinates(x * Map.TILE_SIZE, y * Map.TILE_SIZE);
		isForbidden = false;
		isTarget = false;

	}

	public void setAsForbidden() {
		isForbidden = true;
	}

	public void setAsTarget() {
		isTarget = true;
	}

	public boolean isForbidden() {
		return isForbidden;
	}

	public boolean isTarget() {
		return isTarget;
	}

}
