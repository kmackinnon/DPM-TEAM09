package robot;

import lejos.nxt.Button;
import lejos.nxt.LCD;
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
 * @author Keith MacKinnon
 * 
 */

public class Competitor {

	@SuppressWarnings("unused")
	public static void main(String[] args) throws InterruptedException {

		BluetoothConnection conn = new BluetoothConnection();

		// as of this point, the bluetooth connection is closed again, and you
		// can pair to another NXT (or PC) if you wish

		// example usage of Transmission class
		Transmission t = conn.getTransmission();

		if (t == null) {
			LCD.drawString("Failed to read transmission", 0, 5);
		} else {
			StartCorner corner = t.startingCorner;
			PlayerRole role = t.role;

			// green zone is defined by (bottom-left and top-right) corners:
			int[] greenZone = t.greenZone;

			// red zone is defined by these (bottom-left and top-right) corners:
			int[] redZone = t.redZone;

			// print out the transmission information to the LCD
			conn.printTransmission();
		}

		// stall until user decides to end program
		Button.waitForAnyPress();

		Map.initializeMap();

		// Map.setForbiddenZone;
		// Map.setTargetZone;

		// if we are builder
		// SensorMotorUser.isBuilder(true);

		// if we are garbage collector
		// SensorMotorUser.isBuilder(false);

		Localizer localizer = new Localizer();
		Explorer explorer = new Explorer();
		BlockMover blockMover = new BlockMover();

		localizer.localize();

		explorer.lookForStyrofoamBlocks();

	}

}