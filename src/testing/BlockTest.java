package testing;

import lejos.nxt.Button;
import lejos.nxt.Sound;
import lejos.nxt.comm.RConsole;
import robot.BlockDetector;
import robot.MobileRobot;
import robot.SensorMotorUser;

/**
 * 
 * Steps for using RConsole 
 * 1. Using data from ColorRangeTest, apply a method to do block detection 
 * 	a. Ratio of Values 
 * 	b. //TODO
 *         
 * @author Sidney Ng
 * 
 */
public class BlockTest /*extends Thread*/ {

	public static void main(String[] args) {
		//MobileRobot robot = new MobileRobot();
		BlockDetector blockDetector = new BlockDetector();
		
		//SensorMotorUser.frontCS.setFloodlight(true);
		SensorMotorUser.clawMotor.setSpeed(60);
		SensorMotorUser.clawMotor.rotateTo(320);

		RConsole.open(); // opens a USB connection with no timeout
		
		//robot.setForwardSpeed(SensorMotorUser.FORWARD_SPEED);
		boolean object = false;
		int buttonChoice;
		
		do {
//			try {
//				Thread.sleep(25); // 20 polls per second
//			} catch (Exception e) {
//				e.printStackTrace();
//			}

			if (!object) {
				if (blockDetector.objectDetected()) {
					//robot.stopMotors();
					
					object = true;
				}
			}
			
			
			if (object) {
				if (blockDetector.blockDetected()) {
					RConsole.println("block");
					Sound.beep();
				}
			}
			
			buttonChoice = Button.readButtons();
			
		} while (buttonChoice != Button.ID_ESCAPE);
		RConsole.close(); // closes the USB connection
	}

}
