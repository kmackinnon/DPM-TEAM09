package robot;

/**
 * BlockMover moves the styrofoam block to either the red or green zone. It
 * grabs the styrofoam block, travels to one of the zones, and stacks the block
 * if necessary.
 */

public class BlockMover extends MobileRobot {

	public BlockMover() {

	}

	/**
	 * Contains all the steps necessary for moving a block to a zone: grabbing,
	 * traveling to destination and releasing or stacking the block
	 */
	public void moveBlockToZone() {

		grabBlock();

		// travel to appropriate zone

		if (isBuilder()) {
			stackBlock();
		}

		else {
			releaseBlock();
		}
	}

	private void grabBlock() {

		clawMotor.setSpeed(LIFTING_SPEED);
		
		//lower claw until ultrasonic sensor sees it.
	
		//this sets the claw to the ground.
		
		//this is the idea:
		//while(ultrasonicSensor.getDistance > CLOSE_DISTANCE){
		//clawMotor.backward();
		//}
		
		
		clawMotor.resetTachoCount();
		
		//from lowest point to highest point
		//number to be determined
		clawMotor.rotateTo(340);

	}

	private void releaseBlock() {

		clawMotor.setSpeed(LIFTING_SPEED);

		// we should check the current position of the claw somehow, before
		// rotating the motor.

		//this is the idea:
		//while(ultrasonicSensor.getDistance > CLOSE_DISTANCE){
		//clawMotor.backward();
		//}
		
	}

	private void stackBlock() {

		
		
	}

}
