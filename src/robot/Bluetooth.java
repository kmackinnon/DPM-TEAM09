package robot;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import bluetooth.BluetoothConnection;
import bluetooth.PlayerRole;
import bluetooth.StartCorner;
import bluetooth.Transmission;

/**
 * 
 * This class will be the controller between our robot and the input data. Our
 * robot will become either a garbage collector or a builder in a given round of
 * the competition. We will also be passed the corner in which we start.
 * 
 * @author Keith MacKinnon
 * 
 */
public class Bluetooth {

	@SuppressWarnings("unused")
	public static void main(String[] args) {
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
	}
}
