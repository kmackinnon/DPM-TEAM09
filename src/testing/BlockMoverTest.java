package testing;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.Sound;
import robot.BlockMover;
import robot.MobileRobot;

/**
 * Tests the acquisition of a styrofoam block once one has been found.
 * 
 * @author Keith MacKinnon
 * 
 */

public class BlockMoverTest {

	public static void main(String[] args) {

		int buttonChoice = 0;

		while (buttonChoice == 0)
			buttonChoice = Button.waitForAnyPress();

		BlockMover blockMover = new BlockMover();

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
					blockMover.grabBlock();
					Sound.beep();
				} else {
					LCD.drawString("Not styro", 0, 0);
				}
				
			}

		}

	}

}
