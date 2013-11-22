package testing;

import lejos.nxt.Button;
import lejos.nxt.ColorSensor.Color;
import lejos.nxt.Sound;
import lejos.nxt.comm.RConsole;
import robot.BlockDetector;
import robot.MobileRobot;
import robot.SensorMotorUser;

/**
 * Test for object detection and stopping the robot
 * Test for object recognition
 *         
 * @author Sidney Ng
 * 
 */
public class BlockTest {

	public static void main(String[] args) {
		MobileRobot robot = new MobileRobot();
		
		int option = 0;
		
		while (option == 0)
			option = Button.waitForAnyPress();
		
		switch(option) {
			case Button.ID_ENTER:
				//SensorMotorUser.frontCS.setFloodlight(true);
				robot.liftClaw();

//			RConsole.open(); // opens a USB connection with no timeout
				MobileRobot.blockDetector.startBlockDetectorTimer();
				MobileRobot.blockDetector.turnOnBlockDetection();

				robot.moveForward();
				boolean object = false;
				int buttonChoice;
				do {

					if (!object) {
						if (MobileRobot.blockDetector.isObjectInFront()) {
							robot.stopMoving();
					
						object = true; // comment or uncomment this if you want only object detection or detection and recognition
						}
					}
			
			
					if (object) {
						if (MobileRobot.blockDetector.isObjectStyrofoam()) {
							RConsole.println("block");
							Sound.beep();
						}
					}
					
			
					buttonChoice = Button.readButtons();
			
				} while (buttonChoice != Button.ID_ESCAPE);
				break;
			default:
				System.out.println("Error - invalid button");
				System.exit(-1);
				break;
		}
//		RConsole.close(); // closes the USB connection
	}

}
