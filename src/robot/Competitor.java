package robot;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.Sound;
import bluetooth.BluetoothConnection;
import bluetooth.PlayerRole;
import bluetooth.StartCorner;
import bluetooth.Transmission;

/**
 * This class contains the main function, and gives responsibility of the robot
 * to Localizer, Explorer, or BlockMover, depending on the situation. It
 * receives the initial Bluetooth signal which determines competition
 * characteristics.
 * 
 * @author Keith MacKinnon, Kevin Musgrave
 * 
 */

public class Competitor {

	/**
	 * The starting corner specified by the bluetooth signal
	 */
	private static StartCorner corner;

	/**
	 * Garbage collector or builder
	 */
	private static PlayerRole role;

	/**
	 * Location of the green zone.
	 */
	private static int[] greenZone;

	/**
	 * Location of the red zone.
	 */
	private static int[] redZone;

	public static void main(String[] args) throws InterruptedException {

		Map.initializeMap();

		int buttonChoice;

		// Left or Right buttons must be pressed for the robot to begin
		do {
			LCD.clear(); // clear the display

			// chooses BT for competition or fixed values for testing
			LCD.drawString("< Left  | Right >", 0, 0);
			LCD.drawString("        |        ", 0, 1);
			LCD.drawString("< Demo  | Test > ", 0, 2);

			buttonChoice = Button.waitForAnyPress();

		} while (buttonChoice != Button.ID_LEFT
				&& buttonChoice != Button.ID_RIGHT);

		// Lets user choose whether to get starting data from BT or test data
		if (buttonChoice == Button.ID_LEFT) {
			connectBT(); // start the BT connection determines characteristics

		} else {
			testWithoutBluetooth();
		}

		// sets the start corner from the test data or from the BT connection

		Localizer localizer = new Localizer();
		Explorer explorer = new Explorer();
		BlockMover blockMover = new BlockMover();

		MobileRobot.corr.turnOnLightSensors();

		localizer.localize();
		Sound.beep();

		// explorer.liftClaw();

		MobileRobot.corr.startCorrectionTimer();
		MobileRobot.blockDetector.startBlockDetectorTimer();

		while (!explorer.isFinishedLooking()) {

			if (explorer.lookForStyrofoamBlocks()) {

				blockMover.moveBlockToZone();

			}

		}

		System.exit(0);
	}

	/** This sets up the demo by setting starting corner and player type. */
	private static void connectBT() {
		BluetoothConnection conn = new BluetoothConnection();

		// as of this point, the bluetooth connection is closed again, and
		// you can pair to another NXT (or PC) if you wish

		// example usage of Transmission class
		Transmission t = conn.getTransmission();

		if (t == null) {
			LCD.drawString("Failed to read transmission", 0, 5);
		} else {
			corner = t.startingCorner;
			role = t.role;
		}

		// green zone is defined by (bottom-left and top-right) corners:
		greenZone = t.greenZone;

		// red zone is defined by these (bottom-left and top-right) corners:
		redZone = t.redZone;

		// print out the transmission information to the LCD
		conn.printTransmission();

		if (role.getId() == 1) {
			SensorMotorUser.becomeBuilder(true);
		}

		else if (role.getId() == 2) {
			SensorMotorUser.becomeBuilder(false);
		}

		if (SensorMotorUser.isBuilder()) {
			Map.setTargetZone(greenZone);
			Map.setForbiddenZone(redZone);
		}

		else if (!SensorMotorUser.isBuilder()) {
			Map.setTargetZone(redZone);
			Map.setForbiddenZone(greenZone);
		}

		SensorMotorUser.setStartCorner(corner.getCooridinates());

		setCornersForbidden();

	}

	/**
	 * For testing the robot without using the bluetooth input.
	 */
	private static void testWithoutBluetooth() {

		int[] testGreen = { 6, 3, 8, 4 };
		int[] testRed = { 0, 5, 2, 9 };

		// set role. For now, assume the robot will be a builder.
		SensorMotorUser.becomeBuilder(true);

		// ensure that map size is big enough for test data
		Map.setTargetZone(testGreen);
		Map.setForbiddenZone(testRed);

		// corners
		Map.setForbiddenZone(new int[] { 0, 0, 0, 0 });

		Map.setForbiddenZone(new int[] { Map.NUM_OF_INTERSECTIONS - 1,
				Map.NUM_OF_INTERSECTIONS - 1, Map.NUM_OF_INTERSECTIONS - 1,
				Map.NUM_OF_INTERSECTIONS - 1 });

		Map.setForbiddenZone(new int[] { Map.NUM_OF_INTERSECTIONS - 1, 0,
				Map.NUM_OF_INTERSECTIONS - 1, 0 });

		SensorMotorUser.setStartCorner(new int[] { 0,
				Map.NUM_OF_INTERSECTIONS - 1 });

	}

	/**
	 * Sets the four starting corners as forbidden, so that the robot does not
	 * enter them.
	 */
	private static void setCornersForbidden() {

		if (corner == StartCorner.BOTTOM_LEFT) {
			Map.setForbiddenZone(new int[] { Map.NUM_OF_INTERSECTIONS - 1, 0,
					Map.NUM_OF_INTERSECTIONS - 1, 0 });

			Map.setForbiddenZone(new int[] { Map.NUM_OF_INTERSECTIONS - 1,
					Map.NUM_OF_INTERSECTIONS - 1, Map.NUM_OF_INTERSECTIONS - 1,
					Map.NUM_OF_INTERSECTIONS - 1 });

			Map.setForbiddenZone(new int[] { 0, Map.NUM_OF_INTERSECTIONS - 1,
					0, Map.NUM_OF_INTERSECTIONS - 1 });
		}

		else if (corner == StartCorner.BOTTOM_RIGHT) {
			Map.setForbiddenZone(new int[] { 0, 0, 0, 0 });

			Map.setForbiddenZone(new int[] { Map.NUM_OF_INTERSECTIONS - 1,
					Map.NUM_OF_INTERSECTIONS - 1, Map.NUM_OF_INTERSECTIONS - 1,
					Map.NUM_OF_INTERSECTIONS - 1 });

			Map.setForbiddenZone(new int[] { 0, Map.NUM_OF_INTERSECTIONS - 1,
					0, Map.NUM_OF_INTERSECTIONS - 1 });
		}

		else if (corner == StartCorner.TOP_RIGHT) {
			Map.setForbiddenZone(new int[] { 0, 0, 0, 0 });

			Map.setForbiddenZone(new int[] { Map.NUM_OF_INTERSECTIONS - 1, 0,
					Map.NUM_OF_INTERSECTIONS - 1, 0 });

			Map.setForbiddenZone(new int[] { 0, Map.NUM_OF_INTERSECTIONS - 1,
					0, Map.NUM_OF_INTERSECTIONS - 1 });
		}

		else if (corner == StartCorner.TOP_LEFT) {
			Map.setForbiddenZone(new int[] { 0, 0, 0, 0 });

			Map.setForbiddenZone(new int[] { Map.NUM_OF_INTERSECTIONS - 1, 0,
					Map.NUM_OF_INTERSECTIONS - 1, 0 });

			Map.setForbiddenZone(new int[] { Map.NUM_OF_INTERSECTIONS - 1,
					Map.NUM_OF_INTERSECTIONS - 1, Map.NUM_OF_INTERSECTIONS - 1,
					Map.NUM_OF_INTERSECTIONS - 1 });
		}
	}

}