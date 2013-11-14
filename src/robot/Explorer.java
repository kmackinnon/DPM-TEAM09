package robot;

import lejos.nxt.Sound;

/**
 * Explorer looks for styrofoam blocks. As soon as it finds a styrofoam block,
 * it takes a rest. It does not move styrofoam blocks or lift them.
 */

public class Explorer extends MobileRobot {

	int rowNumber;
	int columnNumber;
	int rowCounter;

	public Explorer() {

	}

	/**
	 * This contains the searching algorithm
	 */
	public void lookForStyrofoamBlocks() {

		if (getXStart() == getYStart()) {
			rowNumber = getYStart();
			columnNumber = getXStart();
		}

		else {
			rowNumber = getXStart();
			columnNumber = getYStart();
		}

		rowCounter = 0;

		for (int initialRow = rowNumber; forLoopCondition(rowNumber, initialRow); forLoopAfterthought(
				'R', initialRow)) {

			for (int initialColumn = columnNumber; forLoopCondition(
					columnNumber, initialColumn); forLoopAfterthought('C',
					initialColumn)) {

				Intersection neighbor = useCorrectXYToGetIntersection(initialRow);

				travelToNeighbor(neighbor);

			}

			if (columnNumber == (Map.NUM_OF_INTERSECTIONS)) {
				columnNumber--;
			}

			else {
				columnNumber++;
			}

		}

	}

	private Intersection useCorrectXYToGetIntersection(int initialRow) {

		if (initialRow == getYStart()) {

			return Map.getIntersection(columnNumber, rowNumber);

		}

		else {

			return Map.getIntersection(rowNumber, columnNumber);

		}

	}

	private boolean forLoopCondition(int currentPosition, int startPosition) {

		if (startPosition < (Map.NUM_OF_INTERSECTIONS - 1)) {
			return currentPosition < Map.NUM_OF_INTERSECTIONS;
		}

		else {
			return currentPosition >= 0;
		}

	}

	private void forLoopAfterthought(char rowOrColumn, int startPosition) {

		if (rowOrColumn == 'R') {

			if (startPosition < (Map.NUM_OF_INTERSECTIONS - 1)) {
				rowNumber++;
			}

			else {
				rowNumber--;
			}

			rowCounter++;

		}

		else if (rowOrColumn == 'C') {

			if (startPosition < (Map.NUM_OF_INTERSECTIONS - 1)) {
				columnNumber++;
			}

			else {
				columnNumber--;
			}

		}

	}

}
