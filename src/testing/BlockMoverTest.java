package testing;

import lejos.nxt.Button;
import lejos.nxt.Sound;
import robot.BlockMover;
import robot.MobileRobot;

public class BlockMoverTest {

	public static void main(String[] args) {
		
		int option = 0;
		
		while (option == 0)
			option = Button.waitForAnyPress();

		
		BlockMover blockMover = new BlockMover();
		
		blockMover.liftClaw();
		
		MobileRobot.blockDetector.startBlockDetectorTimer();
		MobileRobot.blockDetector.turnOnBlockDetection();
		
		
		while(true){
			if(MobileRobot.blockDetector.isObjectInFront()){
				blockMover.stopMoving();
				
				if(MobileRobot.blockDetector.isObjectStyrofoam()){
					blockMover.grabBlock();
					System.exit(0);
				}
			}
			
			else{
				blockMover.moveForward();
			}
		}

	}

}
