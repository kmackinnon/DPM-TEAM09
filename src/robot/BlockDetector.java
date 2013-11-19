package robot;

import lejos.nxt.ColorSensor.Color;
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
	
	private final int DIST_TO_STOP = 11;

	private final int LIGHT_DIFF = 5;
	
	private boolean objectDetected = false;
	private boolean isStyrofoam = false;



	public BlockDetector() {

		blockDetectorTimer = new Timer(DEFAULT_TIMER_PERIOD,this);
		
		blockDetectorTimer.start();
	}


	
	public void timedOut(){
			
		if(!objectDetected){
			detectObjects();
		}
		
		if(objectDetected){
			checkObjectType();
		}
		
	}
	
	
	public boolean objectDetected(){
		
		return objectDetected;
		
	}
	
	public boolean isStyrofoam(){
		return isStyrofoam;
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

	private void detectObjects() {
		// SensorMotorUser.frontCS.setFloodlight(true);

		// stopping by ultrasonic sensor
		shiftArrayByOne(window, getUSDistance());
		median = getMedian(window);
//		RConsole.println("Distance: " + median);
		if (median <= DIST_TO_STOP) {
			objectDetected = true;
		}

		// stopping by color sensor differential
		latch = false;
		value = frontCS.getRawLightValue();
		diff = value - prevValue;
//		RConsole.println("diff: " + diff);
		if (diff > LIGHT_DIFF) {
			latch = true;
		}
		if (latch && prevLatch) {
			prevLatch = false;
			objectDetected = true;
		}
		prevValue = value;
		prevLatch = latch;
		objectDetected = false;
	}
	
	

	/**
	 * Returns a boolean indicating whether or not the block is styrofoam.
	 * 
	 * @return true if styrofoam, false otherwise.
	 */
	private void checkObjectType() {

		//RConsole.println(red + " " + green + " " + blue);
		
		Color color = frontCS.getColor();
		
		double red = color.getRed();
		double green = color.getGreen();
		double blue = color.getBlue();

		double testValue = -1.0;
		if (blue != 0) {
			testValue = ((red / blue) * (green / blue));
		}

		//RConsole.println("testValue: " + testValue);

		if (testValue > .75 && testValue < .9 ) {
			isStyrofoam = true; // this is only true when 5-7 cm from block
		}

		isStyrofoam = false;
	}
	
	

	/**
	 * Returns a boolean indicating whether or not the block is wooden.
	 * 
	 * @return true if wooden, false otherwise.
	 */
	/*public void isWood(double red, double green, double blue) {
		
		double testValue = -1.0;
		
		testValue = ((red / blue) * (green / blue));
		
		if (testValue > 1.9){
			return true;
		}

		return false;
	}*/

}
