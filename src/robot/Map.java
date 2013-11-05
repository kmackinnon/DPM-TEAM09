package robot;

import java.util.Hashtable;

/**
 * Map contains a representation of the game area. This includes the locations
 * of obstacles, the red and green zones, and the 4 corners
 */

public class Map {

	public final static double TILE_SIZE = 30.48;

	/** There are 11 intersections in both the x and y directions. */
	public final static int NUM_OF_INTERSECTIONS = 11;

	private static Hashtable<int[], Intersection> intersectionTable = new Hashtable<int[], Intersection>();

	/**
	 * Initializees the hashtable of intersections. The corner intersections are
	 * marked as "forbidden" because the robot is not allowed to enter the
	 * corner tiles
	 */
	public static void initializeMap() {

		for (int x = 0; x < NUM_OF_INTERSECTIONS; x++) {
			for (int y = 0; y < NUM_OF_INTERSECTIONS; y++) {
				intersectionTable.put(key(x, y), new Intersection(x, y));
			}
		}

		// The four corners are forbidden.
		intersectionTable.get(key(0, 0)).setAsForbidden();
		intersectionTable.get(key(10, 0)).setAsForbidden();
		intersectionTable.get(key(0, 10)).setAsForbidden();
		intersectionTable.get(key(10, 10)).setAsForbidden();

	}

	/**
	 * Assuming the robot is a builder, this sets all intersections in and on
	 * the boundary of the redZone as "forbidden". (The green zone would be
	 * forbidden for a garbage collector)
	 * 
	 * @param topRightCorner
	 *            the xy coordinates of the top right corner of the forbidden
	 *            zone.
	 * 
	 * @param bottomLeftCorner
	 *            the xy coordinates of the bottom left corner of the forbidden
	 *            zone
	 * 
	 */
	public static void setForbiddenZone(Coordinates topRightCorner,
			Coordinates bottomLeftCorner) {

		int[] topRightKey = findKeyFromCoordinates(topRightCorner);
		int[] bottomLeftKey = findKeyFromCoordinates(bottomLeftCorner);

		for (int x = bottomLeftKey[0]; x <= topRightKey[0]; x++) {
			for (int y = bottomLeftKey[1]; y <= topRightKey[1]; y++) {
				intersectionTable.get(key(x, y)).setAsForbidden();
			}
		}

	}

	
	/**
	 * Assuming the robot is a builder, this sets all intersections in and on
	 * the boundary of the greenZone as "target". (The red zone would be
	 * the target for a garbage collector)
	 * 
	 * @param topRightCorner
	 *            the xy coordinates of the top right corner of the target
	 *            zone.
	 * 
	 * @param bottomLeftCorner
	 *            the xy coordinates of the bottom left corner of the target
	 *            zone
	 * 
	 */
	public static void setTargetZone(Coordinates topRightCorner,
			Coordinates bottomLeftCorner) {

		int[] topRightKey = findKeyFromCoordinates(topRightCorner);
		int[] bottomLeftKey = findKeyFromCoordinates(bottomLeftCorner);

		for (int x = bottomLeftKey[0]; x <= topRightKey[0]; x++) {
			for (int y = bottomLeftKey[1]; y <= topRightKey[1]; y++) {
				intersectionTable.get(key(x, y)).setAsTarget();
			}
		}

	}

	
	private static int[] findKeyFromCoordinates(Coordinates input) {

		int xKey = (int) Math.round(input.getX() / TILE_SIZE);
		int yKey = (int) Math.round(input.getY() / TILE_SIZE);

		return key(xKey, yKey);
	}

	private static int[] key(int x, int y) {
		int[] key = { x, y };

		return key;
	}

}
