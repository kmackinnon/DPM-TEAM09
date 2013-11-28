package robot;

import java.util.ArrayList;

/**
 * Map contains a representation of the game area. This includes the locations
 * of any forbidden zones and the target zone.
 * @author Kevin Musgrave
 */

public class Map {

	public final static double TILE_SIZE = 30.48;

	/** There are 11 intersections in both the x and y directions. */
	public final static int NUM_OF_INTERSECTIONS = 11;

	/**
	 * This list contains all the intersections in the game area. An
	 * intersection is null if it is forbidden.
	 */
	private static ArrayList<Intersection> intersectionList = new ArrayList<Intersection>();

	/**
	 * Initializes the arraylist of intersections.
	 */
	public static void initializeMap() {

		for (int y = 0; y < NUM_OF_INTERSECTIONS; y++) {
			for (int x = 0; x < NUM_OF_INTERSECTIONS; x++) {
				intersectionList.add(new Intersection(x, y));
			}
		}

		for (int y = 0; y < NUM_OF_INTERSECTIONS; y++) {
			for (int x = 0; x < NUM_OF_INTERSECTIONS; x++) {
				addEdgesToInitialMap(x, y);
			}
		}

	}

	/**
	 * Assuming the robot is a builder, this sets all intersections in and on
	 * the boundary of the redZone as "forbidden". (The green zone would be
	 * forbidden for a garbage collector)
	 * 
	 * @param zone
	 *            the bottom left and top right tile coordinates of the
	 *            forbidden zone.
	 */
	public static void setForbiddenZone(int[] zone) {

		int[] bottomLeftCorner = new int[2];
		int[] topRightCorner = new int[2];

		bottomLeftCorner[0] = zone[0];
		bottomLeftCorner[1] = zone[1];
		topRightCorner[0] = zone[2];
		topRightCorner[1] = zone[3];

		setZone(topRightCorner, bottomLeftCorner, false);
	}

	/**
	 * Assuming the robot is a builder, this sets all intersections in and on
	 * the boundary of the greenZone as "target". (The red zone would be the
	 * target for a garbage collector)
	 * 
	 * @param zone
	 *            the bottom left and top right tile coordinates of the target
	 *            zone.
	 */
	public static void setTargetZone(int[] zone) {

		int[] bottomLeftCorner = new int[2];
		int[] topRightCorner = new int[2];

		bottomLeftCorner[0] = zone[0];
		bottomLeftCorner[1] = zone[1];
		topRightCorner[0] = zone[2];
		topRightCorner[1] = zone[3];

		setZone(topRightCorner, bottomLeftCorner, true);
	}

	/**
	 * Helper function for setForbiddenZone and setTargetZone
	 * 
	 * @param topRightCorner
	 *            xy tile coordinates of the top right corner
	 * @param bottomLeftCorner
	 *            xy tile coordinates of the bottom left corner
	 * @param keepNode
	 *            true if we do not want to set the intersection to null
	 */
	private static void setZone(int[] topRightCorner, int[] bottomLeftCorner,
			boolean keepNode) {

		int topRightXGrid = topRightCorner[0];
		int bottomLeftXGrid = bottomLeftCorner[0];

		int topRightYGrid = topRightCorner[1];
		int bottomLeftYGrid = bottomLeftCorner[1];

		for (int x = bottomLeftXGrid; x <= topRightXGrid; x++) {
			for (int y = bottomLeftYGrid; y <= topRightYGrid; y++) {
				if (keepNode) {
					get(index(x, y)).setAsTarget();
				}

				else {
					removeIntersection(index(x, y));
				}
			}
		}

	}

	/**
	 * Removes the path between two intersections. This happens when an obstacle
	 * is found along a path.
	 * 
	 * @param a
	 *            the first intersection
	 * @param b
	 *            the second intersection
	 */
	public static void removeEdge(Intersection a, Intersection b) {

		int x1 = a.getX();
		int y1 = a.getY();

		int x2 = b.getX();
		int y2 = b.getY();

		int index1 = index(x1, y1);
		int index2 = index(x2, y2);

		Intersection one = intersectionList.get(index1);
		Intersection two = intersectionList.get(index2);

		one.removeFromAdjacencyList(two);
		two.removeFromAdjacencyList(one);

	}

	/**
	 * 
	 * @return a list of intersections that are inside and on the boundary of
	 *         the target zone.
	 */
	public static ArrayList<Intersection> getTargetZone() {

		ArrayList<Intersection> targetZone = new ArrayList<Intersection>();

		for (Intersection intersection : intersectionList) {

			if (intersection != null && intersection.isTarget()) {
				targetZone.add(intersection);
			}

		}

		return targetZone;

	}

	/**
	 * 
	 * @param input
	 *            an x or y coordinate in centimeters
	 * @return the x or y coordinate in centimeters of the closest intersection
	 */
	public static double nearestIntersectionCoordinate(double input) {

		return (int) (Math.round(input / Map.TILE_SIZE)) * Map.TILE_SIZE;
	}

	/**
	 * 
	 * @param x
	 *            the x coordinate in cm
	 * @param y
	 *            the y coordinate in cm
	 * @return the intersection at the specified xy centimeter coordinates
	 */
	public static Intersection getIntersection(double x, double y) {

		int xGrid = (int) Math.round(x / Map.TILE_SIZE);
		int yGrid = (int) Math.round(y / Map.TILE_SIZE);

		return get(index(xGrid, yGrid));

	}

	/**
	 * 
	 * @param x
	 *            the x tile coordinate
	 * @param y
	 *            the y tile coordinate
	 * @return the intersection at the specified xy tile coordinates
	 */
	public static Intersection getIntersection(int x, int y) {
		return get(index(x, y));
	}

	/**
	 * 
	 * @param intersection
	 *            the intersection that we would like to obtain. This seems
	 *            redundant, but we always need to be dealing with an
	 *            intersection object that exists in the intersectionList. In
	 *            other classes we may deal with intersections that do not exist
	 *            in the list, but that have the same xy coordinates as one of
	 *            the objects in the list
	 * @return the intersection that has the same xy coordinates as the input
	 *         intersection
	 */
	public static Intersection getIntersection(Intersection intersection) {

		int index = index(intersection.getX(), intersection.getY());

		return get(index);
	}

	/**
	 * resets some intersection variables related to AStar and Dijkstra.
	 */
	public static void resetAllPreviousAndDistance() {

		for (Intersection intersection : intersectionList) {

			if (intersection != null) {
				intersection.setPrevious(null);
				intersection.setMinDistance(Double.POSITIVE_INFINITY);
				intersection.setHeuristicDistance(Double.POSITIVE_INFINITY);
			}
		}

	}

	/**
	 * resets some intersection variables related to AStar.
	 */
	public static void resetClosedOpenStatus() {

		for (Intersection intersection : intersectionList) {

			if (intersection != null) {
				intersection.resetClosedOpenStatus();
			}
		}

	}

	/**
	 * 
	 * @param x
	 *            the x tile coordinate
	 * @param y
	 *            the y tile coordinate
	 * @return the index of the intersection in the intersection list
	 */
	public static int index(int x, int y) {

		return (y * NUM_OF_INTERSECTIONS + x);

	}

	private static Intersection get(int index) {

		return intersectionList.get(index);

	}

	/**
	 * Adds a directed edge between two intersections. Must be used twice to
	 * indicate a two way path.
	 * 
	 * @param source
	 *            the intersection with the adjacency list
	 * @param adjacent
	 *            the intersection to add to the adjacency list
	 */
	private static void addEdge(Intersection source, Intersection adjacent) {

		source.addToAdjacencyList(adjacent);

	}

	/**
	 * sets the intersection at the index to null
	 * @param index the index of the intersection in the intersection list
	 */
	private static void removeIntersection(int index) {

		intersectionList.set(index, null);

	}

	/**
	 * Adds all the straight and diagonal paths to the map.
	 * @param x the x tile coordinate
	 * @param y the y tile coordinate
	 */
	private static void addEdgesToInitialMap(int x, int y) {

		Intersection temp = get(index(x, y));

		if (x == 0 && y == 0) {
			addTopRightCap(temp, x, y);
		}

		else if (x == 0 && y == NUM_OF_INTERSECTIONS - 1) {
			addBottomRightCap(temp, x, y);
		}

		else if (x == 0) {
			addRightSide(temp, x, y);
			addTopAndBottom(temp, x, y);
		}

		else if (x == NUM_OF_INTERSECTIONS - 1 && y == 0) {
			addTopLeftCap(temp, x, y);
		}

		else if (y == 0) {
			addTopSide(temp, x, y);
			addRightAndLeft(temp, x, y);
		}

		else if (x == NUM_OF_INTERSECTIONS - 1 && y == NUM_OF_INTERSECTIONS - 1) {
			addBottomLeftCap(temp, x, y);
		}

		else if (x == NUM_OF_INTERSECTIONS - 1) {
			addLeftSide(temp, x, y);
			addTopAndBottom(temp, x, y);
		}

		else if (y == NUM_OF_INTERSECTIONS - 1) {
			addBottomSide(temp, x, y);
			addRightAndLeft(temp, x, y);
		}

		else {
			addRightSide(temp, x, y);
			addLeftSide(temp, x, y);
			addTopAndBottom(temp, x, y);

		}
	}

	private static Intersection right(int x, int y) {
		return intersectionList.get(index(x + 1, y));
	}

	private static Intersection topRight(int x, int y) {
		return intersectionList.get(index(x + 1, y + 1));
	}

	private static Intersection bottomRight(int x, int y) {
		return intersectionList.get(index(x + 1, y - 1));
	}

	private static Intersection left(int x, int y) {
		return intersectionList.get(index(x - 1, y));
	}

	private static Intersection topLeft(int x, int y) {
		return intersectionList.get(index(x - 1, y + 1));
	}

	private static Intersection bottomLeft(int x, int y) {
		return intersectionList.get(index(x - 1, y - 1));
	}

	private static Intersection top(int x, int y) {
		return intersectionList.get(index(x, y + 1));
	}

	private static Intersection bottom(int x, int y) {
		return intersectionList.get(index(x, y - 1));
	}

	private static void addRightSide(Intersection temp, int x, int y) {
		addEdge(temp, bottomRight(x, y));
		addEdge(temp, right(x, y));
		addEdge(temp, topRight(x, y));
	}

	private static void addLeftSide(Intersection temp, int x, int y) {
		addEdge(temp, topLeft(x, y));
		addEdge(temp, left(x, y));
		addEdge(temp, bottomLeft(x, y));
	}

	private static void addTopAndBottom(Intersection temp, int x, int y) {
		addEdge(temp, top(x, y));
		addEdge(temp, bottom(x, y));
	}

	private static void addRightAndLeft(Intersection temp, int x, int y) {
		addEdge(temp, right(x, y));
		addEdge(temp, left(x, y));
	}

	private static void addTopSide(Intersection temp, int x, int y) {
		addEdge(temp, topRight(x, y));
		addEdge(temp, top(x, y));
		addEdge(temp, topLeft(x, y));
	}

	private static void addBottomSide(Intersection temp, int x, int y) {
		addEdge(temp, bottomLeft(x, y));
		addEdge(temp, bottom(x, y));
		addEdge(temp, bottomRight(x, y));
	}

	private static void addTopRightCap(Intersection temp, int x, int y) {
		addEdge(temp, right(x, y));
		addEdge(temp, topRight(x, y));
		addEdge(temp, top(x, y));
	}

	private static void addTopLeftCap(Intersection temp, int x, int y) {
		addEdge(temp, top(x, y));
		addEdge(temp, topLeft(x, y));
		addEdge(temp, left(x, y));
	}

	private static void addBottomRightCap(Intersection temp, int x, int y) {
		addEdge(temp, bottom(x, y));
		addEdge(temp, bottomRight(x, y));
		addEdge(temp, right(x, y));
	}

	private static void addBottomLeftCap(Intersection temp, int x, int y) {
		addEdge(temp, left(x, y));
		addEdge(temp, bottomLeft(x, y));
		addEdge(temp, bottom(x, y));
	}

}