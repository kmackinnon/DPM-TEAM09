package robot;

/**
 * Map contains a representation of the game area. This includes the locations
 * of obstacles, the red and green zones, and the 4 corners
 */

import java.util.ArrayList;

public class Map {

	private static ArrayList<Coordinates> obstacles;

	private static double topBoundary;
	private static double bottomBoundary;
	private static double leftBoundary;
	private static double rightBoundary;

	public static SquareRegion redZone;
	public static SquareRegion greenZone;

	/**
	 * The zone that the robot moves to when carrying a styrofoam block. This is
	 * equal to greenZone when in Builder mode, and redZone when in
	 * GarbageCollector mode
	 */
	public static SquareRegion targetZone;

	/**
	 * The zone that the robot is not allowed to enter. This is equal to redZone
	 * when in Builder mode, and greenZone when in GarbageCollector mode
	 */
	public static SquareRegion forbiddenZone;

	/** This is the bottom left starting tile of the board */
	public static SquareRegion cornerX1;

	/** This is the bottom right starting tile of the board */
	public static SquareRegion cornerX2;

	/** This is the top right starting tile of the board */
	public static SquareRegion cornerX3;

	/** This is the top left starting tile of the board */
	public static SquareRegion cornerX4;

	/**
	 * Records the coordinates of an obstacle.
	 * 
	 * @param x
	 *            The x coordinate of the obstacle.
	 * @param y
	 *            The y coordinate of the obstacle.
	 */
	public static void addObstacle(double x, double y) {

	}

	/**
	 * Sets the targetZone and forbiddenZone of the robot. This is determined at
	 * the very beginning of the game.
	 * 
	 * @param target
	 *            The target zone. Green if Builder, and Red if Garbage
	 *            Collector
	 */
	public static void setTargetZone(SquareRegion target) {
		targetZone = target;

		if (targetZone == redZone) {
			forbiddenZone = greenZone;
		}

		else {
			forbiddenZone = redZone;
		}

	}

	/**
	 * Creates the redZone rectangle.
	 * 
	 * @param topRightCorner
	 *            the coordinates of the top right corner of the redZone.
	 * @param bottomLeftCorner
	 *            the coordinates of the bottom left corner of the redZone.
	 */
	public static void setRedZone(Coordinates topRightCorner,
			Coordinates bottomLeftCorner) {

		redZone = new SquareRegion(topRightCorner, bottomLeftCorner);
	}

	/**
	 * Creates the greenZone rectangle.
	 * 
	 * @param topRightCorner
	 *            the coordinates of the top right corner of the greenZone.
	 * @param bottomLeftCorner
	 *            the coordinates of the bottom left corner of the greenZone.
	 */
	public static void setGreenZone(Coordinates topRightCorner,
			Coordinates bottomLeftCorner) {
		greenZone = new SquareRegion(topRightCorner, bottomLeftCorner);
	}

	/**
	 * Returns a boolean indicating if the robot is near a forbidden region.
	 * 
	 * @param x
	 *            The x position of the robot.
	 * @param y
	 *            The y position of the robot.
	 * @return true if the robot is close to a forbidden region, false
	 *         otherwise.
	 */
	public boolean isCloseToForbiddenRegion(double x, double y) {

		if (forbiddenZone.isClose(x, y)) {
			return true;
		}

		else if (cornerX1.isClose(x, y)) {
			return true;
		}

		else if (cornerX2.isClose(x, y)) {
			return true;
		}

		else if (cornerX3.isClose(x, y)) {
			return true;
		}

		else if (cornerX4.isClose(x, y)) {
			return true;
		}

		else {
			return false;
		}
	}

	// TODO fill in
	/**
	 * Returns a list of waypoints of the shortest path to a destination.
	 * 
	 * @param x
	 *            current x position of the robot
	 * @param y
	 *            current y position of the robot
	 * @param destination
	 *            the coordinates of the destination
	 * @return an arraylist of coordinates that represent the shortest path from
	 *         the current position to the destination
	 */
	public ArrayList<Coordinates> findBestPath(double x, double y,
			Coordinates destination) {

		return new ArrayList<Coordinates>();
	}

}
