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

		Localizer localizer = new Localizer();
		Explorer explorer = new Explorer();
		BlockMover blockMover = new BlockMover();

		localizer.localize();

		explorer.lookForStyrofoamBlocks();

	}

	// Get Bluetooth info

	// Map.setRedZone(new Coordinates(x1,y1), new Coordinates(x2,y2))
	// Map.setGreenZone(new Coordinates(x3,y3), new Coordinates(x4,y4))

	// If we are the garbage collector, then Map.setTargetZone(Map.redZone),
	// else Map.setTargetZone(Map.greenZone)

	// Competitor.play()

}