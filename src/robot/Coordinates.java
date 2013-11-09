package robot;

/**
 * Here we define an x,y pair as an object. It is more convenient to deal with
 * xy pairs as an object, since position is always defined in terms of both x
 * and y.
 * 
 * @author Keith MacKinnon
 * 
 */
public class Coordinates {

	private double x;
	private double y;
	private final double atPointTolerance = 1;

	public Coordinates(double x, double y) {

		this.x = x;
		this.y = y;

	}

	/**
	 * Returns a boolean indicating whether or not the robot is at these
	 * coordinates.
	 * 
	 * @param x
	 *            the current x position of the robot
	 * @param y
	 *            the current y position of the robot
	 * @return true if the robot is considered at this point, false otherwise
	 */
	public boolean isAtPoint(double x, double y) {
		return (Math.abs(x - getX()) < atPointTolerance)
				&& (Math.abs(y - getY()) < atPointTolerance);
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

}