package robot;

/**
 * Explorer looks for styrofoam blocks. As soon as it finds a styrofoam block,
 * it tells Competitor to give control of the robot to BlockMover.
 * 
 * @author Kevin Musgrave
 */

public class Explorer extends MobileRobot {

	/**
	 * true if the robot is done looking for blocks (i.e. the program ends)
	 */
	private boolean isFinishedLooking;

	/**
	 * true if the robot has found a styrofoam block and BlockMover should take
	 * over control of the robot.
	 */
	private boolean giveControlToBlockMover;

	/**
	 * The row that the robot is searching
	 */
	private int rowNumber;
	private int rowCounter;

	public Explorer() {
		isFinishedLooking = false;
		giveControlToBlockMover = false;

		if (getXStart() == getYStart()) {
			rowNumber = getYStart();
		}

		else {
			rowNumber = getXStart();
		}

		rowCounter = 0;

	}

	/**

	 * 
	 */

	/**
	 * Runs the search routine. If BlockMover has just finished releasing a
	 * block, then Explorer continues from where it last was.
	 * 
	 * @return true if explorer has found a styrofoam block, false if the robot
	 *         is done looking for blocks.
	 */
	public boolean lookForStyrofoamBlocks() {

		corr.turnOnCorrection();
		blockDetector.turnOnBlockDetection();

		for (int initialRow = rowNumber; loopCondition(rowNumber, initialRow); loopAfterthought(initialRow)) {

			travelTo(endOfCurrentRow());

			if (giveControlToBlockMover) {
				return true;
			}

			travelTo(nextRow());

			if (giveControlToBlockMover) {
				return true;
			}
		}

		isFinishedLooking = true;
		return false;

	}

	/**
	 * @return the Intersection at the end of the current row. If the end of the
	 *         row is forbidden, then this returns the non-forbidden
	 *         intersection that is closest to the end of the row.
	 */
	private Intersection endOfCurrentRow() {
		return getNextDestination(rowNumber);
	}

	/**
	 * @return the Intersection that the robot should travel to, to start
	 *         searching the next row
	 */
	private Intersection nextRow() {
		if (getXStart() == 0 && getYStart() == 0
				&& rowNumber != Map.NUM_OF_INTERSECTIONS - 1) {
			return getNextDestination(rowNumber + 1);
		}

		else if (getXStart() == 0
				&& getYStart() == Map.NUM_OF_INTERSECTIONS - 1
				&& rowNumber != Map.NUM_OF_INTERSECTIONS - 1) {
			return getNextDestination(rowNumber + 1);
		}

		else if (getXStart() == Map.NUM_OF_INTERSECTIONS - 1
				&& getYStart() == Map.NUM_OF_INTERSECTIONS - 1
				&& rowNumber != 0) {
			return getNextDestination(rowNumber - 1);
		}

		else if (getXStart() == Map.NUM_OF_INTERSECTIONS - 1
				&& getYStart() == 0 && rowNumber != 0) {
			return getNextDestination(rowNumber - 1);
		}

		return Map.getIntersection(0, 0);
	}

	private Intersection getValidIntersection(int tempRowNumber,
			int defaultDestination, boolean yIsRow) {

		Intersection intersection;

		for (int i = 1; i <= Map.NUM_OF_INTERSECTIONS; i++) {

			if (yIsRow) {

				intersection = Map.getIntersection(
						Math.abs(defaultDestination - i), tempRowNumber);

			}

			else {

				intersection = Map.getIntersection(tempRowNumber,
						Math.abs(defaultDestination - i));

			}

			if (intersection != null) {
				return intersection;
			}

		}

		return Map.getIntersection(0, 0);
	}

	private Intersection getNextDestination(int tempRowNumber) {

		if (rowCounter % 2 == 0) {

			if (getXStart() == 0 && getYStart() == 0) {
				return getValidIntersection(tempRowNumber,
						Map.NUM_OF_INTERSECTIONS, true);
			}

			else if (getXStart() == 0
					&& getYStart() == Map.NUM_OF_INTERSECTIONS - 1) {
				return getValidIntersection(tempRowNumber, 1, false);
			}

			else if (getXStart() == Map.NUM_OF_INTERSECTIONS - 1
					&& getYStart() == Map.NUM_OF_INTERSECTIONS - 1) {
				return getValidIntersection(tempRowNumber, 1, true);
			}

			else if (getXStart() == Map.NUM_OF_INTERSECTIONS - 1
					&& getYStart() == 0) {
				return getValidIntersection(tempRowNumber,
						Map.NUM_OF_INTERSECTIONS, false);
			}

		}

		else if (rowCounter % 2 != 0) {

			if (getXStart() == 0 && getYStart() == 0) {
				return getValidIntersection(tempRowNumber, 1, true);
			}

			else if (getXStart() == 0
					&& getYStart() == Map.NUM_OF_INTERSECTIONS - 1) {
				return getValidIntersection(tempRowNumber,
						Map.NUM_OF_INTERSECTIONS, false);
			}

			else if (getXStart() == Map.NUM_OF_INTERSECTIONS - 1
					&& getYStart() == Map.NUM_OF_INTERSECTIONS - 1) {
				return getValidIntersection(tempRowNumber,
						Map.NUM_OF_INTERSECTIONS, true);
			}

			else if (getXStart() == Map.NUM_OF_INTERSECTIONS - 1
					&& getYStart() == 0) {
				return getValidIntersection(tempRowNumber, 1, false);
			}

		}

		return Map.getIntersection(0, 0);

	}

	private boolean loopCondition(int currentPosition, int startPosition) {

		if (startPosition < (Map.NUM_OF_INTERSECTIONS - 1)) {
			return currentPosition < Map.NUM_OF_INTERSECTIONS;
		} else {
			return currentPosition >= 0;
		}
	}

	private void loopAfterthought(int startPosition) {

		if (startPosition < (Map.NUM_OF_INTERSECTIONS - 1)) {
			rowNumber++;
		} else {
			rowNumber--;
		}

		rowCounter++;

	}

	/**
	 * @return true if the robot is done looking for blocks
	 */
	public boolean isFinishedLooking() {
		return isFinishedLooking;
	}


	/**
	 * overrides the MobileRobot method
	 */
	public boolean pickUpStyrofoamBlock() {

		giveControlToBlockMover = true;

		return true;

	}

}
