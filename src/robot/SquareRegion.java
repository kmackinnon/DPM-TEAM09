package robot;

/**
 * SquareRegion is meant to represent rectangular areas on the board.
 * This is simply a way to hide some code that is needed to determine if the
 * robot is entering a "forbidden" zone. It may also make it easier to write the
 * code for finding optimal paths
 * 
 */

public class SquareRegion {

	double xLeft;
	double xRight;
	double yTop;
	double yBottom;

	private Line topLine;
	private Line bottomLine;
	private Line leftLine;
	private Line rightLine;
	
	private final double CLOSE_DISTANCE = 6;

	/**
	 * 
	 * @param topRightCorner
	 *            The coordinates of the top right corner of the rectangle.
	 * @param bottomLeftCorner
	 *            The coordinates of the bottom left corner of the rectangle.
	 */
	public SquareRegion(Coordinates topRightCorner, Coordinates bottomLeftCorner) {
		
		this.xLeft = bottomLeftCorner.getX();
		this.xRight = topRightCorner.getX();
		this.yTop = topRightCorner.getY();
		this.yBottom = bottomLeftCorner.getY();
		
		Coordinates topLeftCorner = new Coordinates(bottomLeftCorner.getX(), topRightCorner.getY());
		Coordinates bottomRightCorner = new Coordinates(topRightCorner.getX(), bottomLeftCorner.getY());
		
		topLine = new Line(topLeftCorner, topRightCorner);
		bottomLine = new Line(bottomLeftCorner, bottomRightCorner);
		leftLine = new Line(topLeftCorner, bottomLeftCorner);
		rightLine = new Line(topRightCorner, bottomRightCorner);
		
	}

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
		
		if(getDistance(x,y) < CLOSE_DISTANCE){
			return true;
		}
		
		return false;
		
	}

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

		Coordinates current = new Coordinates(x,y);
		
		if(x < xLeft){
			return leftLine.shortestDistance(current);
		}
		
		else if(x > xRight){
			return rightLine.shortestDistance(current);
		}
		
		else if(y > yTop){
			return topLine.shortestDistance(current);
		}
		
		else{
			return bottomLine.shortestDistance(current);
		}
		
	}

}
