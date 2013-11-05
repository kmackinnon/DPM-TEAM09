package robot;

/**
 * This class chooses which routines to execute in a certain order. It receives
 * the initial Bluetooth signal which determines competition characteristics.
 * 
 * @author Keith MacKinnon
 * 
 */

public class Competitor {

	public Localizer localizer = new Localizer();
	public Explorer explorer = new Explorer();
	public BlockMover blockMover = new BlockMover();

	public Competitor() {

	}

	public void play() {

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
