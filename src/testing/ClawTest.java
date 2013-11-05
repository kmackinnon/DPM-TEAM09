package testing;

import lejos.nxt.Button;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;

public class ClawTest {

	public static void main(String[] args) {
		
		NXTRegulatedMotor clawMotor = Motor.A;
		
		Button.waitForAnyPress();
		
		clawMotor.setSpeed(50);
		
		clawMotor.rotate(250);
		
		try {
			Thread.sleep(1000); // sleep for second
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		clawMotor.rotate(-100);
		
		
		
		
		
		Button.waitForAnyPress();
		System.exit(0);
	}
	
	
}
