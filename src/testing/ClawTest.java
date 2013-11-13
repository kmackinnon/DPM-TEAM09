package testing;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.comm.RConsole;

public class ClawTest {

	public static void main(String[] args) {

		// RConsole.open();

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

		// RConsole.println(Integer.toString(angle));

		clawMotor.rotateTo(0); // set the block back down

		System.exit(0);
	}

}
