package robot;

import lejos.nxt.Button;
import bluetooth.PlayerRole;
import bluetooth.StartCorner;

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
	
	private static StartCorner corner;
	private static PlayerRole role;
	private static int[] greenZone;
	private static int[] redZone;
	
	@SuppressWarnings("unused")
	public static void main(String[] args) throws InterruptedException {

		/*BluetoothConnection conn = new BluetoothConnection();
		


		// as of this point, the bluetooth connection is closed again, and you
		// can pair to another NXT (or PC) if you wish

		// example usage of Transmission class
		Transmission t = conn.getTransmission();

		if (t == null) {
			LCD.drawString("Failed to read transmission", 0, 5);
		} else {
			corner = t.startingCorner;
			role = t.role;

			// green zone is defined by (bottom-left and top-right) corners:
			greenZone = t.greenZone;

			// red zone is defined by these (bottom-left and top-right) corners:
			redZone = t.redZone;

			// print out the transmission information to the LCD
			conn.printTransmission();
		}
		
		
		if(role.getId()==1){
			SensorMotorUser.becomeBuilder(true);
		}

		else if(role.getId()==2){
			SensorMotorUser.becomeBuilder(false);
		}*/
		
		//Map.initializeMap();

		
		
		/*if(SensorMotorUser.isBuilder()){
			Map.setTargetZone(greenZone);
			Map.setForbiddenZone(redZone);
		}
		
		else if(!SensorMotorUser.isBuilder()){
			Map.setTargetZone(redZone);
			Map.setForbiddenZone(greenZone);
		}
		
		SensorMotorUser.setStartCorner(corner.getCooridinates());*/
		
		Button.waitForAnyPress();
		
		Localizer localizer = new Localizer();
		/*Explorer explorer = new Explorer();
		BlockMover blockMover = new BlockMover();*/

		localizer.localize();
		
		Button.waitForAnyPress();
		
		System.exit(0);

		/*explorer.lookForStyrofoamBlocks();*/

	}

}