package robot;

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

	public static void main(String[] args) throws InterruptedException {

		//GetBluetooth info
		
		Map.initializeMap();
		
		//Map.setForbiddenZone;
		//Map.setTargetZone;
		
		//if we are builder
		//SensorMotorUser.isBuilder(true);
		
		//if we are garbage collector
		//SensorMotorUser.isBuilder(false);
		
		
		Localizer localizer = new Localizer();
		Explorer explorer = new Explorer();
		BlockMover blockMover = new BlockMover();

		localizer.localize();

		explorer.lookForStyrofoamBlocks();

	}


}