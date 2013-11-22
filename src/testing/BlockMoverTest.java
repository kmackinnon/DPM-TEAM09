package testing;

import lejos.nxt.Button;
import lejos.nxt.Sound;
import robot.BlockMover;

public class BlockMoverTest {

	public static void main(String[] args) {
		
		int option = 0;
		
		while (option == 0)
			option = Button.waitForAnyPress();

		
		BlockMover blockMover = new BlockMover();
		
		blockMover.liftClaw();
		
		blockMover.travelCoordinate(0, 30.48, true);
		
		blockMover.grabBlock();
		
		Sound.beep();

	}

}
