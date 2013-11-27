package robot;

import lejos.nxt.ColorSensor.Color;
import lejos.nxt.Sound;
import lejos.nxt.comm.RConsole;
import lejos.util.Timer;
import lejos.util.TimerListener;

/**
 * BlockDetector uses the ultrasonic sensor and light sensor to detect blocks
 * that are close by. The light sensor identifies the block as wooden or
 * styrofoam.
 * 
 */
public class BlockDetector extends SensorMotorUser implements TimerListener {

	private Timer blockDetectorTimer;
	private Object lock;

	private final int DIST_TO_STOP = 11;
	private final int LIGHT_DIFF = 25;
	private final double UPPER_RED_BLUE_RATIO = 1.4;
	private final double WOOD_RATIO = 1.9;

	private boolean isObjectDetected = false;
	private boolean doBlockDetection = false;
	private boolean doMinDistanceScan = false;

	private int scanMinDistance;
	private double minDistanceAngle;
	private int frontCSIgnoreCounter;

	// variables for object detection
	private int[] window = { 255, 255, 255, 255, 255 };
	private int[] colorDiffWindow = { 0, 0, 0, 0, 0 , 0, 0, 0, 0, 0};
	private int prevValue = 0;
	private boolean prevLatch = false, latch = false;
	private int median, value, diff;

	public BlockDetector() {
		lock = new Object();
		blockDetectorTimer = new Timer(DEFAULT_TIMER_PERIOD, this);
	}

	public void startBlockDetectorTimer() {
		blockDetectorTimer.start();
	}

	public void turnOnBlockDetection() {
		isObjectDetected = false;
		doBlockDetection = true;
		latch = false;
		prevLatch = false;
		frontCSIgnoreCounter = 0;
	}

	public void turnOffBlockDetection() {
		isObjectDetected = false;
		doBlockDetection = false;
	}

	public void turnOnMinDistanceScanMode() {
		scanMinDistance = US_SENSOR_255;

		doMinDistanceScan = true;
	}

	public void turnOffMinDistanceScanMode() {
		doMinDistanceScan = false;
	}

	public void timedOut() {

		if (doBlockDetection) {
			if (isObjectDetected()) {

				synchronized (lock) {
					isObjectDetected = true;
				}

			} else {

				synchronized (lock) {
					isObjectDetected = false;
				}
			}
		}

		if (doMinDistanceScan) {

			minDistanceScanRoutine();

		}

	}

	public double getMinDistanceAngle() {

		if (scanMinDistance == US_SENSOR_255) {
			minDistanceAngle = DOUBLE_SPECIAL_FLAG;
		}

		return minDistanceAngle;

	}

	public double getMinDistance() {
		return scanMinDistance;
	}

	// returns the isObjectDetected boolean
	public boolean isObjectInFront() {
		boolean result;

		synchronized (lock) {
			result = isObjectDetected;
		}

		return result;
	}

	/**
	 * Returns a boolean indicating whether or not a block is detected by the
	 * ultrasonic or color sensor.
	 * 
	 * @return true if a block is detected, false otherwise.
	 */
	public boolean isObjectDetected() {
		// SensorMotorUser.frontCS.setFloodlight(true);

		frontCSIgnoreCounter++;

		// stopping by ultrasonic sensor
		shiftArrayByOne(window, getUSDistance());
		median = getMedian(window);
//		RConsole.println("Distance: " + median);
		if(frontCSIgnoreCounter>=10){

			if (median <= DIST_TO_STOP) {
				return true;
			}
		}

		// stopping by color sensor differential
		latch = false;
		value = frontCS.getRawLightValue();
		diff = value - prevValue;
		shiftArrayByOne(colorDiffWindow, diff);
		double colorDiffMedian = getMedian(colorDiffWindow);
		prevValue = value;
//		RConsole.println("value: " + value);
		
		if(frontCSIgnoreCounter>=10){
			if (colorDiffMedian > LIGHT_DIFF) {
				latch = true;
			}
			if (latch && prevLatch) {
				prevLatch = false;
				return true;
			}
		}
		
		prevLatch = latch;

		return false;
	}
	
	public boolean isObjectStyrofoam(){
		if(isStyrofoamColor() && !isWoodColor()){
			Sound.beep();
			return true;
		}
		
		else{
			return false;
		}
	}

	/**
	 * Returns a boolean indicating whether or not the block is styrofoam.
	 * 
	 * @return true if styrofoam, false otherwise.
	 */
	private boolean isStyrofoamColor() {
		// RConsole.println(red + " " + green + " " + blue);

		/*
		 * Color color; int redValue; int blueValue; int greenValue; double
		 * ratio; int counter = 0;
		 * 
		 * 
		 * for(int i=0; i<10; i++){ color = frontCS.getColor();
		 * 
		 * redValue = color.getRed(); greenValue = color.getGreen(); blueValue =
		 * color.getBlue();
		 * 
		 * ratio = -1.0; if (blueValue != 0) { ratio = ((redValue / blueValue) *
		 * (greenValue / blueValue)); }
		 * 
		 * // conditions for styrofoam block if (ratio > .75 && ratio < .9) {
		 * counter++; // this is only true when 5-7 cm from block }
		 * 
		 * }
		 * 
		 * if(counter>=CONFIRMATION_MINIMUM){ return true; }
		 * 
		 * else{ return false; }
		 */

		Color color;
		double redValue;
		double blueValue;
		double ratio;
		int counter = 0;

		for (int i = 0; i < DEFAULT_NUM_OF_SAMPLES; i++) {
			color = frontCS.getColor();
			redValue = color.getRed();
			blueValue = color.getBlue();

			ratio = redValue / blueValue;

			if (ratio < UPPER_RED_BLUE_RATIO && ratio > 0) {
				counter++;
			}
		}

		if (counter >= DEFAULT_CONFIRMATION_MINIMUM) {
			return true;
		}

		else {
			return false;
		}

	}

	private void minDistanceScanRoutine() {

		shiftArrayByOne(window, getUSDistance());
		median = getMedian(window);

		if (median <= scanMinDistance) {

			scanMinDistance = median;
			minDistanceAngle = MobileRobot.odo.getTheta();
		}

	}

	private boolean isWoodColor() {

		Color color;
		double redValue;
		double blueValue;
		double greenValue;
		double ratio;
		int counter = 0;
		
		
		for (int i = 0; i < DEFAULT_NUM_OF_SAMPLES; i++) {
			color = frontCS.getColor();
			redValue = color.getRed();
			blueValue = color.getBlue();
			greenValue = color.getGreen();

			ratio = ((redValue / blueValue) * (greenValue / blueValue));

			if (ratio > WOOD_RATIO) {
				counter++;
			}
		}

		if (counter >= DEFAULT_CONFIRMATION_MINIMUM) {
			//Sound.beep();
			return true;
		}

		else {
			return false;
		}
	}

}
