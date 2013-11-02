package robot;

/**Odometer keeps track of the position of the robot and its orientation
 * 
 * @author Simon Lee, Sidney Ng
 * 
 */

import lejos.util.Timer;
import lejos.util.TimerListener;

public class Odometer implements TimerListener {

	/** Default period for the Timer object */
	private static final int DEFAULT_PERIOD = 25;
	
	/** Timer to execute timedOut method every period */
	private Timer odometerTimer;
	
	// position data
	/** Lock object to lock x, y and theta variables for reading and writing */
	private Object lock;
	
	/** Current x/y position */
	private double x, y;
	
	/** Current theta value */
	private double theta;
	
	/** Displacement/heading value */
	private double displacement, heading;
	
	/** Previous displacement/heading values */
	private double oldDisp, oldHeading;

	/**
	 * Odometer constructor
	 * 
	 * @param period The period which the odometer timer should execute at
	 * @param start Boolean indicating whether to start the timer on initialization
	 */
	public Odometer(int period, boolean start) {
		// initialise variables
		odometerTimer = new Timer(period, this);
		x = 0.0;
		y = 0.0;
		theta = 0.0;
		oldDisp = 0.0;
		oldHeading = 0.0;
		lock = new Object();

		// start the odometer immediately, if necessary
		if (start)
			odometerTimer.start();
	}

	/**
	 * Default Constructor uses the DEFAULT_PERIOD and does not start on initialization
	 */
	public Odometer() {
		this(DEFAULT_PERIOD, false);
	}

	/**
	 * Constructor with default period. Takes in boolean to indicate start on initialization
	 * @param start Boolean indicating whether to start the timer on initialization
	 */
	public Odometer(boolean start) {
		this(DEFAULT_PERIOD, start);
	}

	/**
	 * Constructor does not start on initialization and takes a period for the timer object
	 * @param period
	 */
	public Odometer(int period) {
		this(period, false);
	}

	/**
	 * Executes every period.
	 * <p>
	 * Takes tachometer readings from both motors and and calculates change in displacement and heading from last reading. 
	 * Updates x, y, and theta values based on the delta displacement and heading calculations.
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
		}

		oldDisp += displacement;
		oldHeading += heading;
	}

	// accessors
	/**
	 * Return current position
	 * @param pos double array with 0th element for x, 1st element for y, 2nd element for theta
	 */
	public void getPosition(double[] pos) {
		synchronized (lock) {
			pos[0] = x;
			pos[1] = y;
			pos[2] = theta;
		}
	}

	// mutators
	/**
	 * Updates odometer x, y, theta values to new coordinates
	 * 
	 * @param pos array of coordinates to change; 0th element for x, 1st element for y, 2nd element for theta
	 * @param update array of booleans to indicate whether corresponding value in pos array needs to update
	 */
	public void setPosition(double[] pos, boolean[] update) {
		synchronized (lock) {
			if (update[0])
				x = pos[0];
			if (update[1])
				y = pos[1];
			if (update[2])
				theta = pos[2];
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
	 * @return current theta value
	 */
	public double getAng() {
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
	public static double fixDegAngle(double angle) {
		if (angle < 0.0)
			angle = 360.0 + (angle % 360.0);

		return angle % 360.0;
	}

	/**
	 * Calculates the minimum angle between two angles
	 * 
	 * @param a The first angle
	 * @param b The second angle
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
	 * @return the change in displacement
	 */
	public double getDisplacement() {
		return (HardwareInfo.leftMotor.getTachoCount() * HardwareInfo.leftRadius +
				HardwareInfo.rightMotor.getTachoCount() * HardwareInfo.rightRadius) *
				Math.PI / 360.0;
	}
	
	/**
	 * Calculates the total change in displacement
	 * @return the heading
	 */
	public double getHeading() {
		return (HardwareInfo.leftMotor.getTachoCount() * HardwareInfo.leftRadius -
				HardwareInfo.rightMotor.getTachoCount() * HardwareInfo.rightRadius) / HardwareInfo.width;
	}
	
	/**
	 * Calculates the total change in heading and displacement
	 * @param data the double array storing displacement as 0th element and heading as the 1st element
	 */
	public void getDisplacementAndHeading(double [] data) {
		data[0] = getDisplacement();
		data[1] = getHeading();
	}
}
