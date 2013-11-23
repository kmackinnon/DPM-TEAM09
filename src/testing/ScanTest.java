package testing;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.Sound;
import robot.BlockMover;
import robot.Map;
import robot.MobileRobot;

public class ScanTest {

	public static void main(String[] args) {

		int buttonChoice = 0;

		while (buttonChoice == 0)
			buttonChoice = Button.waitForAnyPress();

		Map.initializeMap();

		Map.setTargetZone(new int[] {4,3,6,5});
		
		BlockMover blockMover = new BlockMover();
		
		blockMover.initializePrevTarget(0,0);

		blockMover.liftClaw(); // lift the claw at the beginning

		MobileRobot.blockDetector.startBlockDetectorTimer();
		MobileRobot.blockDetector.turnOnBlockDetection();
		blockMover.moveForward();

		boolean isSearching = true;
		while (isSearching) {
			
			// stop moving if there is something in front
			if (MobileRobot.blockDetector.isObjectInFront()) {
				blockMover.stopMoving();
				isSearching = false; // i.e. it found a block

				// if that object is styrofoam, perform grabBlock() method
				if (MobileRobot.blockDetector.isObjectStyrofoam()) {
					MobileRobot.blockDetector.turnOffBlockDetection();
					
					blockMover.moveBlockToZone();
				} else {
					LCD.drawString("Not styro", 0, 0);
				}
				
			}

		}
		
		blockMover.dropClaw();
		
		Button.waitForAnyPress();
		
		System.exit(0);

	}
}
