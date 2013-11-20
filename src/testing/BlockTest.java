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
public class BlockTest /*extends Thread*/ {

	public static void main(String[] args) {
		MobileRobot robot = new MobileRobot();
		BlockDetector blockDetector = new BlockDetector();
		
		//SensorMotorUser.frontCS.setFloodlight(true);
		SensorMotorUser.clawMotor.setSpeed(60);
		SensorMotorUser.clawMotor.rotateTo(320);

//		RConsole.open(); // opens a USB connection with no timeout
		
		robot.moveForward();
		boolean object = false;
		int buttonChoice;
		blockDetector.turnOnBlockDetection();
		do {
//			try {
//				Thread.sleep(25); // 20 polls per second
//			} catch (Exception e) {
//				e.printStackTrace();
//			}

			if (!object) {
				if (blockDetector.objectInFrontOfRobot()) {
					robot.stopMoving();
					
//					object = true; // comment or uncomment this if you want only object detection or detection and recognition
				}
			}
			
			
			if (object) {
				if (blockDetector.objectIsStyrofoam()) {
					RConsole.println("block");
					Sound.beep();
				}
			}
			
			buttonChoice = Button.readButtons();
			
		} while (buttonChoice != Button.ID_ESCAPE);
//		RConsole.close(); // closes the USB connection
	}

}
