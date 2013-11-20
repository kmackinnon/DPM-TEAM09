package robot;

import lejos.nxt.ColorSensor.Color;
import lejos.nxt.Sound;
import lejos.util.Timer;
import lejos.util.TimerListener;

/**
 * BlockDetector uses the ultrasonic sensor and light sensor to detect blocks
 * that are close by. The light sensor identifies the block as wooden or
 * styrofoam
 * 
 */
public class BlockDetector extends SensorMotorUser implements TimerListener {

	private Timer blockDetectorTimer;
	private Object lock;

	private final int DIST_TO_STOP = 11;

	private final int LIGHT_DIFF = 5;

	private boolean objectDetected = false;
	private boolean isStyrofoam = false;

	public BlockDetector() {
		lock = new Object();

		blockDetectorTimer = new Timer(DEFAULT_TIMER_PERIOD, this);
	}

	public void turnOnBlockDetection() {
		blockDetectorTimer.start();
	}

	public void timedOut() {



		if (objectDetected()) {
			Sound.beep();
			
			synchronized (lock) {
				objectDetected = true;
			}

			if (isStyrofoam()) {
				synchronized (lock) {
					isStyrofoam = true;
				}
			}
		}
		
		else{
			
			synchronized (lock) {
				objectDetected = false;
				isStyrofoam = false;
			}
			
		}

	}

	public boolean objectIsStyrofoam() {

		boolean result;

		synchronized (lock) {
			result = isStyrofoam;
		}

		return result;
	}

	public boolean objectInFrontOfRobot() {

		boolean result;

		synchronized (lock) {
			result = objectDetected;
		}

		return result;

	}

	/**
	 * Returns a boolean indicating whether or not a block is detected by the
	 * ultrasonic or color sensor.
	 * 
	 * @return true if a block is detected, false otherwise.
	 */

	private int[] window = { 255, 255, 255, 255, 255 };
	private int prevValue = 0;
	private boolean prevLatch = false;
	private int median, value, diff;
	private boolean latch;

	private boolean objectDetected() {
		// SensorMotorUser.frontCS.setFloodlight(true);

		// stopping by ultrasonic sensor
		shiftArrayByOne(window, getUSDistance());
		median = getMedian(window);
		// RConsole.println("Distance: " + median);
		if (median <= DIST_TO_STOP) {
			return true;
		}

		// stopping by color sensor differential
		latch = false;
		value = frontCS.getRawLightValue();
		diff = value - prevValue;
		// RConsole.println("diff: " + diff);
		if (diff > LIGHT_DIFF) {
			latch = true;
		}
		if (latch && prevLatch) {
			prevLatch = false;
			return true;
		}
		prevValue = value;
		prevLatch = latch;
		return false;
	}

	/**
	 * Returns a boolean indicating whether or not the block is styrofoam.
	 * 
	 * @return true if styrofoam, false otherwise.
	 */
	private boolean isStyrofoam() {

		// RConsole.println(red + " " + green + " " + blue);

		Color color = frontCS.getColor();

		double red = color.getRed();
		double green = color.getGreen();
		double blue = color.getBlue();

		double testValue = -1.0;
		if (blue != 0) {
			testValue = ((red / blue) * (green / blue));
		}

		// RConsole.println("testValue: " + testValue);

		if (testValue > .75 && testValue < .9) {
			return true; // this is only true when 5-7 cm from block
		}

		return false;
	}

	/**
	 * Returns a boolean indicating whether or not the block is wooden.
	 * 
	 * @return true if wooden, false otherwise.
	 */
	/*
	 * public boolean isWood(double red, double green, double blue) {
	 * 
	 * double testValue = -1.0;
	 * 
	 * testValue = ((red / blue) * (green / blue));
	 * 
	 * if (testValue > 1.9){ return true; }
	 * 
	 * return false; }
	 */

}
