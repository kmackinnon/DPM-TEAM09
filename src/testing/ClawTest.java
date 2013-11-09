package testing;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.comm.RConsole;

public class ClawTest {

	public static void main(String[] args) {
		
		NXTRegulatedMotor clawMotor = Motor.C;
		
		//RConsole.open();
		
		Button.waitForAnyPress();
		
		clawMotor.setSpeed(50);
		
		int angle = clawMotor.getTachoCount();
		
		LCD.drawInt(angle, 0, 0);
		
		clawMotor.rotate(-200);
		
		clawMotor.resetTachoCount();
		
		//RConsole.println(Integer.toString(angle));
		
		try {
			Thread.sleep(1000); // sleep for second
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		clawMotor.rotateTo(300);
		
		try {
			Thread.sleep(1000); // sleep for second
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		clawMotor.rotateTo(0);
	
		
		
		
		
		
		Button.waitForAnyPress();
		System.exit(0);
	}
	
	
}
