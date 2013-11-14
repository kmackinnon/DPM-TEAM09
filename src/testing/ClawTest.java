package testing;

import lejos.nxt.Button;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;

/**
 * Qualitative test to demonstrate the functioning of the lifting mechanism.
 * Note that the claw should be in a closed position prior to commencing the
 * program as the motor tries to rotate -100 degrees.
 * 
 * @author Keith MacKinnon
 * 
 */
public class ClawTest {

	public static void main(String[] args) {

		int option = 0;
		while (option == 0) {
			option = Button.waitForAnyPress();
		}

		NXTRegulatedMotor clawMotor = Motor.C;

		clawMotor.setSpeed(50);

		clawMotor.rotate(-100); // opens claw
		clawMotor.resetTachoCount(); // reset tachometer when fully open

		try {
			Thread.sleep(1000); // sleep for one second
		} catch (Exception e) {
			e.printStackTrace();
		}

		clawMotor.rotateTo(350); // lift the block

		try {
			Thread.sleep(1000); // sleep for one second
		} catch (Exception e) {
			e.printStackTrace();
		}

		clawMotor.rotateTo(0); // set the block back down

		System.exit(0);
	}

}
