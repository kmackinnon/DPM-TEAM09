package robot;
/**
 * Localizer calculates the position and orientation of the robot at the start
 * of the game. This has to be done because the odometer initially assumes a
 * (0,0) position and 0 degree heading.
 */

public class Localizer extends MobileRobot {
	
	/** The distance from the wall at which to record the falling edge angle */
	private final int DIST_TO_WALL = 40;
	
	/** A noise margin to filter out noise when localizing */
	private final int NOISE_MARGIN = 2;
	
	/** Period for the  */
	private final int PERIOD = 100;
	
	/** Distance measured by the ultrasonic sensor */
	private int distance;
	
	/** Thread utilizing the ultrasonic sensor to poll distance values every PERIOD seconds */
	private Thread usThread = new Thread() {
		public void run() {
			while (true) {
				distance = ultrasonicSensor.getDistance();
			
				try {
					Thread.sleep(PERIOD);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					break;
				}
			}
		}
	};
	
	private Thread csThread = new Thread() {
		public void run() {
			while (true) {
				// get the color values from both color sensors
				// check if detected at the same time
				// if true correct for 0, 90, 180, 270
			}
		}
	};
	
	/**
	 * Default constructor
	 */
	public Localizer() {
		
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
	public void localize() throws InterruptedException {
		clawMotor.setSpeed(60);
		clawMotor.rotateTo(300);
		ultrasonicLocalization();
//		lightLocalization();
	}

	/**
	 * Performs falling edge ultrasonic localization.
	 * @throws InterruptedException 
	 */
	private void ultrasonicLocalization() throws InterruptedException {
		double [] pos = new double [3];
		double angleA, angleB, tempA, tempB;
		usThread.start();
		
		this.setRotationSpeed(ROTATION_SPEED);
		
		while(distance < 55); // keep rotating until there is no wall
		
		while(distance > DIST_TO_WALL + NOISE_MARGIN); // average the angles found at edges of the noise margin
		odo.getPosition(pos);
		tempA = pos[2];
		
		while (distance > DIST_TO_WALL - NOISE_MARGIN);
		odo.getPosition(pos);
		angleA = pos[2];
		
		angleA = (tempA + angleA)/2;
		
		// switch direction and wait until it sees no wall
		this.setRotationSpeed(ROTATION_SPEED * -1);
		while (distance < 50);
		
		// keep rotating until the robot sees a wall, then latch the angle
		while (distance > DIST_TO_WALL + NOISE_MARGIN);
		odo.getPosition(pos);
		tempB = pos[2];
		
		while (distance > DIST_TO_WALL - NOISE_MARGIN);
		odo.getPosition(pos);
		angleB = pos[2];
		
		angleB = (tempB + angleB)/2;
		
		if (angleA < angleB)
			turnTo((angleA + angleB + 270) / 2);
		else
			turnTo((angleA + angleB - 90) / 2);
		
		// update the odometer position
		odo.setPosition(new double [] {0.0, 0.0, 0.0}, new boolean [] {true, true, true});
		this.stopMotors();
		
		usThread.interrupt();
		usThread.join();
	}

	private void lightLocalization() {
		// start on an intersection
		// rotate until we detect a line
		// based on our current heading, correct for the angle
		// rotate until we detect another line, correct for this angle
	}
	
}
