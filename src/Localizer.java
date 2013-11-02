/**
 * Localizer calculates the position and orientation of the robot at the start
 * of the game. This has to be done because the odometer initially assumes a
 * (0,0) position and 0 degree heading.
 */

public class Localizer extends MobileRobot {

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
	 */
	public void localize() {
		ultrasonicLocalization();
		lightLocalization();
	}

	private void ultrasonicLocalization() {
	}

	private void lightLocalization() {

	}

}
