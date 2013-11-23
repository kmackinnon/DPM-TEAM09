package robot;

import lejos.nxt.Sound;

/**
 * BlockMover moves the styrofoam block to either the red or green zone. It
 * grabs the styrofoam block, travels to one of the zones, and stacks the block
 * if necessary.
 */

public class BlockMover extends MobileRobot {

	private final int SCAN_ANGLE = 45;
	private final int GIVE_UP_LIMIT = 2;

	public BlockMover() {

	}

	/**
	 * Contains all the steps necessary for moving a block to a zone: grabbing,
	 * traveling to destination and releasing or stacking the block
	 */
	public void moveBlockToZone() {
		
		corr.turnOffCorrection();
		blockDetector.turnOffBlockDetection();
		
		// find best orientation for grabbing the block
		if (!findBestAngleForBlockGrab()) {
			return;
		}

		// try grabbing the block the twice. if it fails twice, stop trying.
		for (int i = 0; i <= GIVE_UP_LIMIT; i++) {

			if (i == GIVE_UP_LIMIT) {
				return;
			}
			
			grabBlock();
			
			if(confirmBlockGrab()){
				break;
			}
		}

		// go to the last intersection the robot was at before seeing and
		// grabbing the block.
		
		travelCoordinate(getPrevX(), getPrevY(), true);

		corr.turnOnCorrection();
		blockDetector.turnOnBlockDetection();

		travelToTargetZone();

		corr.turnOffCorrection();
		blockDetector.turnOffBlockDetection();

		getReadyForBlockRelease();
		
		releaseBlock();

		liftClaw();

		travelCoordinate(getPrevX(), getPrevY(), true);

		return;
	}

	private void grabBlock() {
		travelMagnitude(-19); // -16, 18

		dropClaw();

		travelMagnitude(21); // 13, 15, 16

		liftClaw();

	}

	private boolean confirmBlockGrab() {
		
		int counter = 0;
		
		for(int i = 0; i<DEFAULT_NUM_OF_SAMPLES; i++){
			if(!blockDetector.isObjectDetected()){
				
				counter++;
				
			}
		}
		
		if(counter>=DEFAULT_CONFIRMATION_MINIMUM){
			Sound.beep();
			return true;
		}
		
		else{
			//Sound.beep();
			return false;
		}

	}

	private boolean findBestAngleForBlockGrab() {
		scanArea(SCAN_ANGLE);

		double finalAngle = blockDetector.getFinalAngle();

		if (finalAngle == DOUBLE_SPECIAL_FLAG) {
			return false;
		}

		turnToOnPoint(finalAngle);

		return true;
	}

	private void releaseBlock() {

		dropClaw();
		
		//back away before lifting claw again
		travelMagnitude(-12);
		
		liftClaw();

	}


	private void getReadyForBlockRelease() {
		Intersection current = Map.getIntersection(odo.getX(), odo.getY());

		double xMax = -Double.MAX_VALUE;
		double yMax = -Double.MAX_VALUE;
		double xMin = Double.MAX_VALUE;
		double yMin = Double.MAX_VALUE;

		for (Intersection intersection : Map.getTargetZone()) {
			if (intersection.getX() > xMax) {
				xMax = intersection.getX();
			}

			if (intersection.getY() > yMax) {
				yMax = intersection.getY();
			}

			if (intersection.getX() < xMin) {
				xMin = intersection.getX();
			}

			if (intersection.getY() < yMin) {
				yMin = intersection.getY();
			}
		}

		if (current.getX() == xMin && current.getY() == yMin) {
			turnToOnPoint(45);
		}

		else if (current.getX() == xMin && current.getY() == yMax) {
			turnToOnPoint(135);
		}

		else if (current.getX() == xMax && current.getY() == yMax) {
			turnToOnPoint(225);
		}

		else if (current.getX() == xMax && current.getY() == yMin) {
			turnToOnPoint(315);
		}

		else if (current.getX() == xMin) {
			turnToOnPoint(90);
		}

		else if (current.getX() == xMax) {
			turnToOnPoint(270);
		}

		else if (current.getY() == yMin) {
			turnToOnPoint(0);
		}

		else if (current.getY() == yMax) {
			turnToOnPoint(180);
		}
	}

	public boolean pickUpStyrofoamBlock() {
		return false;
	}

}
