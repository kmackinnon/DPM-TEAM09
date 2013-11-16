package robot;

/**Odometer keeps track of the position of the robot and its orientation
 * 
 * @author Simon Lee, Sidney Ng
 * 
 */

import lejos.nxt.ColorSensor;
import lejos.nxt.Sound;
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

	/** Odometer correction variables */
	private static final int LINE_DIFF = 20;
	
	private boolean leftSensorDetected;
	private boolean rightSensorDetected;

	private double prevRightTacho;
	private double prevLeftTacho;

	private double[] positionAtFirstDetection;

	boolean doCorrection = false;

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
		
		// start the odometer immediately, if necessary
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

		if (doCorrection) {
			correctOdometer();
		}

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

	public void turnOnCorrection() {
		
		leftCS.setFloodlight(true);
		rightCS.setFloodlight(true);

		leftSensorDetected = false;
		rightSensorDetected = false;

		doCorrection = true;

	}

	public void turnOffCorrection() {
		
		leftCS.setFloodlight(false);
		rightCS.setFloodlight(false);

		doCorrection = false;

	}

	// accessors
	/**
	 * Return current position
	 * 
	 * @param pos
	 *            double array with 0th element for x, 1st element for y, 2nd
	 *            element for theta
	 */
	public void getPosition(double[] pos) {
		synchronized (lock) {
			pos[0] = x;
			pos[1] = y;
			pos[2] = theta;
		}
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

	private void correctOdometer() {

		double distanceTravelledByLaggingWheel = 0;
		double angleOff = 0;

		if (!leftSensorDetected && !rightSensorDetected) {

			if (lineDetected(leftCS)) {
				// if left has detected, then this is a new line; take position
				// and tacho count
				getPosition(positionAtFirstDetection);
				prevRightTacho = rightMotor.getTachoCount();
				leftSensorDetected = true;
			}

			if (lineDetected(rightCS)) {
				// if right has detected, then this is a new line; take position
				// and tacho count
				getPosition(positionAtFirstDetection);
				prevLeftTacho = leftMotor.getTachoCount();
				rightSensorDetected = true;
			}

		}

		if (leftSensorDetected && !rightSensorDetected) {

			if (lineDetected(rightCS)) {

				double currentRightTacho = rightMotor.getTachoCount();

				distanceTravelledByLaggingWheel = 2 * Math.PI * RIGHT_RADIUS
						* ((currentRightTacho - prevRightTacho) / 360);

				angleOff = Math.atan(distanceTravelledByLaggingWheel
						/ SENSOR_WIDTH);
				rightSensorDetected = true;

			}

		}

		if (!leftSensorDetected && rightSensorDetected) {

			if (lineDetected(leftCS)) {

				double currentLeftTacho = leftMotor.getTachoCount();

				distanceTravelledByLaggingWheel = 2 * Math.PI * LEFT_RADIUS
						* ((currentLeftTacho - prevLeftTacho) / 360);
				
				angleOff = -Math.atan(distanceTravelledByLaggingWheel
						/ SENSOR_WIDTH);

				leftSensorDetected = true;

			}

		}

		if (leftSensorDetected && rightSensorDetected) {

			if (distanceTravelledByLaggingWheel != 0) {

				setX(positionAtFirstDetection[0]
						+ (distanceTravelledByLaggingWheel)
						* Math.sin(angleOff));
				setY(positionAtFirstDetection[1]
						+ (distanceTravelledByLaggingWheel)
						* Math.cos(angleOff));
				setTheta(theta + Math.toDegrees(angleOff));

			}

			rightSensorDetected = false;
			leftSensorDetected = false;

		}
	}
	
	
	
	private int prevValueL = 0;
	private int prevValueR = 0;
	private boolean negativeDiffL = false;
	private boolean negativeDiffR = false;
	
	private boolean lineDetected(ColorSensor cs) {
		
		boolean left = (cs==leftCS);
		
		int value = cs.getRawLightValue();
		int diff = (left) ? (value - prevValueL) : (value - prevValueR);
		
//		RConsole.println("Diff: " + diff);
		if(diff<-LINE_DIFF){
			if (left) {
				negativeDiffL = true;
			} else {
				negativeDiffR = true;
			}
		}
		
		if (left) {
			prevValueL = value;
		} else {
			prevValueR = value;
		}
		
		if(diff>LINE_DIFF){
			if (negativeDiffL && left) {
//				RConsole.println("Ldetected");
				Sound.beep();
				negativeDiffL = false;
				return true;
			} else if (negativeDiffR && !left) {
//				RConsole.println("Rdetected");
				Sound.beep();
				negativeDiffR = false;
				return true;
			}
		}
		
		return false;
	}
	
	
	
}
