package robot;

/**Odometer keeps track of the position of the robot and its orientation
 * 
 * @author Simon Lee, Sidney Ng, Kevin Musgrave
 * 
 */

import lejos.util.Timer;
import lejos.util.TimerListener;

public class Odometer extends SensorMotorUser implements TimerListener {

	private final int LARGE_ANGLE_RANGE_TOLERANCE = 20;

	private final int SMALL_ANGLE_RANGE_TOLERANCE = 10;
	
	/** Timer to execute timedOut method every period */
	private Timer odometerTimer;

	// position data
	/** Lock object to lock x, y and theta variables for reading and writing */
	private Object lock;

	/** Current x/y position */
	private double x;

	private double y;

	/** Current theta value */
	private double theta;

	/** Displacement/heading value */
	private double displacement, heading;

	/** Previous displacement/heading values */
	private double oldDisp, oldHeading;

	/**
	 * Odometer constructor
	 * 
	 * @param period
	 *            The period which the odometer timer should execute at
	 * @param start
	 *            Boolean indicating whether to start the timer on
	 *            initialization
	 */
	public Odometer() {
		// initialise variables
		odometerTimer = new Timer(DEFAULT_TIMER_PERIOD, this);
		x = 0.0;
		y = 0.0;
		theta = 0.0;
		oldDisp = 0.0;
		oldHeading = 0.0;
		
		lock = new Object();

		odometerTimer.start();
	}

	/**
	 * Executes every period.
	 * <p>
	 * Takes tachometer readings from both motors and and calculates change in
	 * displacement and heading from last reading. Updates x, y, and theta
	 * values based on the delta displacement and heading calculations.
	 */
	public void timedOut() {

		displacement = getDisplacement();
		heading = getHeading();
		displacement -= oldDisp;
		heading -= oldHeading;

		// update the position in a critical region
		synchronized (lock) {
			theta += heading;
			theta = fixDegAngle(theta);

			x += displacement * Math.sin(Math.toRadians(theta));
			y += displacement * Math.cos(Math.toRadians(theta));
			//RConsole.println("x: " + x + "y: " + y + " theta: " + theta);
		}

		oldDisp += displacement;
		oldHeading += heading;
		
	}
	



	public void setX(double input) {
		synchronized (lock) {
			x = input;
		}

	}

	public void setY(double input) {
		synchronized (lock) {
			y = input;
		}

	}

	public void setTheta(double input) {
		synchronized (lock) {
			theta = input;
		}
	}

	/**
	 * Uses synchronized block to read current x value
	 * 
	 * @return current x value
	 */
	public double getX() {
		double result;
		synchronized (lock) {
			result = x;
		}
		return result;
	}

	/**
	 * Uses synchronized block to read current y value
	 * 
	 * @return current y value
	 */
	public double getY() {
		double result;
		synchronized (lock) {
			result = y;
		}
		return result;
	}

	/**
	 * Uses synchronized block to read current theta value
	 * 
	 * @return current theta value
	 */
	public double getTheta() {
		double result;

		synchronized (lock) {
			result = theta;
		}
		return result;
	}

	// static 'helper' methods

	/**
	 * Wraparound angle values to stay within the range of 0.0 to 359.9
	 * 
	 * @param double angle angle to fix
	 * @return angle value within range of 0.0 to 359.9
	 */
	public double fixDegAngle(double angle) {
		if (angle < 0.0)
			angle = 360.0 + (angle % 360.0);

		return angle % 360.0;
	}


	/**
	 * Calculates the total change in displacement
	 * 
	 * @return the change in displacement
	 */
	private double getDisplacement() {
		return (leftMotor.getTachoCount() * LEFT_RADIUS + rightMotor
				.getTachoCount() * RIGHT_RADIUS)
				* Math.PI / 360.0;
	}

	/**
	 * Calculates the total change in displacement
	 * 
	 * @return the heading
	 */
	private double getHeading() {
		return (leftMotor.getTachoCount() * LEFT_RADIUS - rightMotor
				.getTachoCount() * RIGHT_RADIUS)
				/ WIDTH;
	}
	
	/**
	 * 
	 * @return the Direction that the robot is facing (i.e NORTH for 0 degrees, SOUTH for 180 degrees etc)
	 */
	public Direction getDirection(){
		
		if(theta < LARGE_ANGLE_RANGE_TOLERANCE || theta > (360 - LARGE_ANGLE_RANGE_TOLERANCE)){
			return Direction.NORTH;
		}
		
		else if(Math.abs(theta-90) < LARGE_ANGLE_RANGE_TOLERANCE){
			return Direction.EAST;
		}
		
		else if(Math.abs(theta-180) < LARGE_ANGLE_RANGE_TOLERANCE){
			return Direction.SOUTH;
		}
		
		else if(Math.abs(theta-270) < LARGE_ANGLE_RANGE_TOLERANCE){
			return Direction.WEST;
		}
		
		else if(Math.abs(theta-45) < SMALL_ANGLE_RANGE_TOLERANCE){
			return Direction.NORTHEAST;
		}
		
		else if(Math.abs(theta-135) < LARGE_ANGLE_RANGE_TOLERANCE){
			return Direction.SOUTHEAST;
		}
		
		else if(Math.abs(theta-225) < LARGE_ANGLE_RANGE_TOLERANCE){
			return Direction.SOUTHWEST;
		}
		
		else {
			return Direction.NORTHWEST;
		}
		
	}

	
	
	
}
