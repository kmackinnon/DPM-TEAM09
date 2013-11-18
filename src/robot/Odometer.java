package robot;

/**Odometer keeps track of the position of the robot and its orientation
 * 
 * @author Simon Lee, Sidney Ng
 * 
 */

import lejos.util.Timer;
import lejos.util.TimerListener;

public class Odometer extends SensorMotorUser implements TimerListener {
	/** Default period for the Timer object */
	private static final int DEFAULT_PERIOD = 25;

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
		odometerTimer = new Timer(DEFAULT_PERIOD, this);
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
	private static double fixDegAngle(double angle) {
		if (angle < 0.0)
			angle = 360.0 + (angle % 360.0);

		return angle % 360.0;
	}

	/**
	 * Calculates the minimum angle between two angles
	 * 
	 * @param a
	 *            The first angle
	 * @param b
	 *            The second angle
	 * @return The minimum angle
	 */
	public static double minimumAngleFromTo(double a, double b) {
		double d = fixDegAngle(b - a);

		if (d < 180.0)
			return d;
		else
			return d - 360.0;
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

	
	
	
}
