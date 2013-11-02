package robot;
/**Odometer keeps track of the position of the robot and its orientation
 * 
 * @author Simon Lee, Sidney Ng
 * 
 */

/*
 * Odometer Class
 * Author: Simon Lee, Sidney Ng
 * uses HardwareInfo
 */

import lejos.util.Timer;
import lejos.util.TimerListener;

public class Odometer implements TimerListener {

	private static final int DEFAULT_PERIOD = 25;
	private Timer odometerTimer;
	// position data
	private Object lock;
	private double x, y, theta;
	private double[] oldDH, dDH;

	
	public Odometer(int period, boolean start) {
		// initialise variables
		odometerTimer = new Timer(period, this);
		x = 0.0;
		y = 0.0;
		theta = 0.0;
		oldDH = new double[2];
		dDH = new double[2];
		lock = new Object();

		// start the odometer immediately, if necessary
		if (start)
			odometerTimer.start();
	}

	public Odometer() {
		this(DEFAULT_PERIOD, false);
	}

	public Odometer(boolean start) {
		this(DEFAULT_PERIOD, start);
	}

	public Odometer(int period) {
		this(period, false);
	}

	public void timedOut() {
		int leftTacho, rightTacho;
		leftTacho = HardwareInfo.leftMotor.getTachoCount();
		rightTacho = HardwareInfo.rightMotor.getTachoCount();
		dDH[0] = (leftTacho * HardwareInfo.leftRadius + rightTacho
				* HardwareInfo.rightRadius)
				* Math.PI / 360;
		dDH[1] = (leftTacho * HardwareInfo.leftRadius - rightTacho
				* HardwareInfo.rightRadius)
				/ HardwareInfo.width;
		dDH[0] -= oldDH[0];
		dDH[1] -= oldDH[1];

		// update the position in a critical region
		synchronized (lock) {
			theta += dDH[1];
			theta = fixDegAngle(theta);

			x += dDH[0] * Math.sin(Math.toRadians(theta));
			y += dDH[0] * Math.cos(Math.toRadians(theta));
		}

		oldDH[0] += dDH[0];
		oldDH[1] += dDH[1];
	}

	// accessors
	public void getPosition(double[] pos) {
		synchronized (lock) {
			pos[0] = x;
			pos[1] = y;
			pos[2] = theta;
		}
	}

	// mutators
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

	// static 'helper' methods
	public static double fixDegAngle(double angle) {
		if (angle < 0.0)
			angle = 360.0 + (angle % 360.0);

		return angle % 360.0;
	}

	public static double minimumAngleFromTo(double a, double b) {
		double d = fixDegAngle(b - a);

		if (d < 180.0)
			return d;
		else
			return d - 360.0;
	}

	public double getX() {
		double result;
		synchronized (lock) {
			result = x;
		}
		return result;
	}

	public double getY() {
		double result;
		synchronized (lock) {
			result = y;
		}
		return result;
	}

	public double getAng() {
		double result;

		synchronized (lock) {
			result = theta;

		}
		return result;
	}

}
