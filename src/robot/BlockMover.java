package robot;

/**
 * BlockMover moves the styrofoam block to either the red or green zone. It
 * grabs the styrofoam block, travels to one of the zones, and releases the
 * block.
 * 
 * @author Kevin Musgrave, Sidney Ng
 */

public class BlockMover extends MobileRobot {

	/**
	 * The angle used for scanning when finding the best approach for grabbing
	 * the block.
	 */
	private final int SCAN_ANGLE = 45;

	/**
	 * The maximum number of attempts for grabbing the block
	 */
	private final int GIVE_UP_LIMIT = 2;

	public BlockMover() {

	}

	/**
	 * Contains all the steps necessary for moving a block to a zone: scanning
	 * for best approach, grabbing, confirming block grab, traveling to
	 * destination and releasing the block
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

			if (confirmBlockGrab()) {
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

	/**
	 * Moves backward, lowers the claw, moves forward, and then grabs the block
	 * and raises the claw.
	 */
	public void grabBlock() {
		travelMagnitude(-21);

		dropClaw();

		travelMagnitude(23);

		liftClaw();

	}

	/**
	 * Checks to see if there is still an object in front of the robot. If there
	 * is then the block has not been picked up.
	 * 
	 * @return true if the block has been picked up, false otherwise.
	 */
	private boolean confirmBlockGrab() {

		int counter = 0;

		for (int i = 0; i < DEFAULT_NUM_OF_SAMPLES; i++) {
			if (!blockDetector.isObjectDetected()) {

				counter++;

			}
		}

		if (counter >= DEFAULT_CONFIRMATION_MINIMUM) {
			return true;
		}

		else {
			return false;
		}

	}

	/**
	 * Turns to the best angle for grabbing the block.
	 * 
	 * @return True if the robot successfully finds and turns to the best angle,
	 *         false if there is no best angle.
	 */
	private boolean findBestAngleForBlockGrab() {
		scanArea(SCAN_ANGLE);

		double finalAngle = blockDetector.getMinDistanceAngle();

		if (finalAngle == DOUBLE_SPECIAL_FLAG) {
			return false;
		}

		turnToOnPoint(finalAngle);

		return true;
	}

	/**
	 * Lowers the claw, releases the block, moves backward, and raises the claw.
	 */
	private void releaseBlock() {

		dropClaw();

		// back away before lifting claw again
		travelMagnitude(-12);

		liftClaw();

	}

	/**
	 * When at the target zone, the robot must face "inward" to release the
	 * block (i.e. the robot might be at a target zone intersection, but it
	 * could be facing away from the actual target zone.) This method gets the
	 * robot oriented in the correct direction so that the block is placed
	 * inside the target zone.
	 */
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

	/**
	 * When the robot finds a styrofoam block, the robot should not try to pick
	 * it up if it already is carrying one. This method is used to make this
	 * decision. It overrides the method in MobileRobot
	 */
	public boolean pickUpStyrofoamBlock() {
		return false;
	}

}
