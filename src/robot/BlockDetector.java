package robot;

import lejos.nxt.ColorSensor;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.ColorSensor.Color;
import lejos.nxt.comm.RConsole;

/**
 * BlockDetector uses the ultrasonic sensor and light sensor to detect blocks
 * that are close by. The light sensor identifies the block as wooden or
 * styrofoam
 * 
 */
public class BlockDetector extends SensorMotorUser{
	
	private static final int DIST_TO_STOP = 11;
	
	private static final int LIGHT_DIFF = 5;
	
	public BlockDetector() {

	}
	
	
	
	private int[] window = {255, 255, 255, 255, 255};
	private int prevValue = 0;
	private boolean prevLatch = false;
	public boolean objectDetected() {
		//SensorMotorUser.frontCS.setFloodlight(true);
		
		// stopping by ultrasonic sensor
		shiftArrayByOne(window, ultrasonicSensor.getDistance());
		int median = getMedian(window);
		RConsole.println("Distance: " + median);
		if (median <= DIST_TO_STOP) {
			return true;
		}
		
		// stopping by color sensor differential
		boolean latch = false;
		int value = frontCS.getRawLightValue();
		int diff = value - prevValue;
		if (diff > LIGHT_DIFF) {
			latch = true;
		}
		if (latch && prevLatch) {
			prevLatch = false;
			return true;
		}
		prevValue = value;
		return false;
	}
	
	public boolean blockDetected() {
		Color colorValue = frontCS.getColor();
		double redValue = colorValue.getRed();
		double greenValue = colorValue.getGreen();
		double blueValue = colorValue.getBlue();

		RConsole.println(redValue + " " + greenValue + " " + blueValue);

		double testValue = -1.0;
		if (blueValue != 0) {
			testValue = ((redValue / blueValue) * (greenValue / blueValue));
		}
		
		RConsole.println("testValue: " + testValue);// + " distance: " + distance);
		
		if (testValue < .9 && testValue > .75) {
			return true; // this is only true within a certain distance 5-7 by the ultrasonic readings
		}
		
		return false;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	// TODO fill in
	/**
	 * Returns a boolean indicating whether or not a block is detected by the ultrasonic or color sensor.
	 * 
	 * @return true if a block is detected, false otherwise.
	 */
	public boolean frontDetectBlock() {
		
		return false;
	}

	// TODO fill in
	/**Returns a boolean indicating whether or not the block is styrofoam.
	 * 
	 * @return true if styrofoam, false otherwise.
	 */
	public boolean isStyrofoam() {

		return false;
	}

	// TODO fill in
	/**Returns a boolean indicating whether or not the block is wooden.
	 * 
	 * @return true if wooden, false otherwise.
	 */
	public boolean isWood() {

		return false;
	}

}
