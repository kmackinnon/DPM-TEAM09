package robot;

import lejos.nxt.Sound;
import lejos.nxt.comm.RConsole;

/**
 * Localizer calculates the position and orientation of the robot at the start
 * of the game. This has to be done because the odometer initially assumes a
 * (0,0) position and 0 degree heading.
 */

public class Localizer extends MobileRobot {
	
	/** The distance from the wall at which to record the falling edge angle */
	private final int DIST_TO_WALL = 30;
	
	/** A noise margin to filter out noise when localizing */
	private final int NOISE_MARGIN = 2;
	
	/** Distance measured by the ultrasonic sensor */
	private int distance;
	
	/** Boolean to indicate when the robot has detected the falling edge of a wall */
	private boolean isLatched;
	
	/**
	 * Default constructor
	 */
	public Localizer() {
		isLatched = false;
	}

	/**
	 * Performs the localization routine. This consists of two steps: ultrasonic
	 * localization and light localization.
	 * <p>
	 * Ultrasonic localization uses the corner walls to calculate the angle of
	 * the robot and an estimate of the x and y position of the robot.
	 * <p>
	 * Light localization uses the tile lines to improve on the values obtained
	 * by the ultrasonic localization.
	 * @throws InterruptedException 
	 */
	public void localize() {
		// raise the claw to unimpede sensor vision
		clawMotor.setSpeed(60);
		clawMotor.rotateTo(310);
		ultrasonicLocalization();
		lightLocalization();
	}

	/**
	 * Performs falling edge ultrasonic localization.
	 * @throws InterruptedException 
	 */
	private void ultrasonicLocalization() {
		// a double array to store the latched angle values
		// first latched angle corresponds to 0th element, second angle to 1st element
		double[] angles = new double[2];
		
		//TODO: delete RConsole stuff
		RConsole.open();
		
		setRotationSpeed(ROTATION_SPEED);
		
		// turn until the robot has latched the falling edge
		// checkForLatched() will modify the values of angleA, tempA, 
		while (!isLatched) {
			isLatched = checkForLatched(angles);
		}

//		turnTo(odo.getAng() - 10);
		isLatched = false;
		double angleA = (angles[0] + angles[1])/2; // calculate the first angle
		RConsole.println("" + isLatched);
		setRotationSpeed(-1 * ROTATION_SPEED);
		while (!isLatched) {
		}
//		// latch the second falling edge
//		while (!isLatched) {
//			isLatched = checkForLatched(angles);
//		}
//		
//		double angleB = (angles[0] + angles[1]) / 2; // calculate the second angle
//		
//		if (angleA < angleB)
//			turnTo((angleA + angleB + 270) / 2);
//		else
//			turnTo((angleA + angleB - 90) / 2);
//		
//		// update the odometer position
//		odo.setPosition(new double [] {0.0, 0.0, 0.0}, new boolean [] {true, true, true});
		
		// TODO: delete
		RConsole.close();
	}

	private void lightLocalization() {
		// start on an intersection
		// rotate until we detect a line
		// based on our current heading, correct for the angle
		// rotate until we detect another line, correct for this angle
	}
	
	/**
	 * Helper method for Ultrasonic localization to store the latched angles in an array.
	 * 
	 * @param angles a double array within which the latched angles are stored
	 * @return true if lower bound has been detected, false if lower bound has not been reached
	 */
	private boolean checkForLatched(double[] angles) {
		double [] pos = new double [3];

		distance = getFilteredUSData();
		if (distance <= (DIST_TO_WALL + NOISE_MARGIN) && distance > DIST_TO_WALL) {
			// robot turned to the upper bound of the NOISE_MARGIN
			// get the angle here
			odo.getPosition(pos);
			angles[0]= pos[2];
		} else if (distance >= (DIST_TO_WALL - NOISE_MARGIN) && distance < DIST_TO_WALL) {
			// detected the lower bound of the NOISE_MARGIN
			// get the angle here and stop the robot
			setSpeeds(0.0, 0.0);
			odo.getPosition(pos);
			angles[1] = pos[2];
			return true; // exit the loop
		}
		return false;
	}
	
	// TODO: filter this data
	private int getFilteredUSData() {
		//TODO: get rid of rconsole
		distance = ultrasonicSensor.getDistance();
		RConsole.println("Distance: " + distance + " Theta: " + odo.getAng());
		return distance;
	}
	
} // end of doc
