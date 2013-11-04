package robot;

/**
 * SquareRegion is a class meant to represent rectangular areas on the board.
 * This is simply a way to hide some code that is needed to determine if the
 * robot is entering a "forbidden" zone. It may also make it easier to write the
 * code for finding optimal paths
 * 
 */

public class SquareRegion {

	private Coordinates topRightCorner;
	private Coordinates bottomLeftCorner;
	private final double closeDistance = 6;

	/**
	 * 
	 * @param c1
	 *            The coordinates of the top right corner of the rectangle.
	 * @param c2
	 *            The coordinates of the bottom left corner of the rectangle.
	 */
	public SquareRegion(Coordinates c1, Coordinates c2) {
		this.topRightCorner = c1;
		this.bottomLeftCorner = c2;
	}

	// TODO fill in
	/**
	 * Returns a boolean indicating if the robot is close to a rectangle.
	 * 
	 * @param x
	 *            The current x position of the robot.
	 * @param y
	 *            The current y position of the robot.
	 * @return true if the robot is close to the rectangle, false otherwise.
	 *         "Close" is defined in the variable closeDistance.
	 */
	public boolean isClose(double x, double y) {

		return false;
	}

	// TODO fill in
	/**
	 * Returns a double indicating the distance to the closest point of the
	 * rectangle.
	 * 
	 * @param x
	 *            The current x position of the robot.
	 * @param y
	 *            The current y position of the robot.
	 * @return the distance to the closest point of the rectangle
	 */
	public double getDistance(double x, double y) {

		return 0;
	}

}
