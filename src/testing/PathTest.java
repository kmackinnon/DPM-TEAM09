package testing;

import lejos.nxt.comm.RConsole;
import robot.Intersection;
import robot.Map;
import robot.SensorMotorUser;

/**
 * Tests the simple "search" path that Explorer generates.
 * 
 * @author Sidney Ng
 * 
 */

public class PathTest {

	static int rowNumber;
	static int columnNumber;
	static int rowCounter;

	public static void main(String[] args) {

		int[] forbiddenZone = { 2, 4, 5, 6 };
		int[] startCorner = { 0, 0 };
		RConsole.openUSB(10000);
		Map.initializeMap();

		Map.setForbiddenZone(forbiddenZone);
		SensorMotorUser.setStartCorner(startCorner);

		if (SensorMotorUser.getXStart() == SensorMotorUser.getYStart()) {
			rowNumber = SensorMotorUser.getYStart();
		}

		else {
			rowNumber = SensorMotorUser.getXStart();
		}

		rowCounter = 0;

		for (int initialRow = rowNumber; loopCondition(rowNumber, initialRow); loopAfterthought(initialRow)) {

			System.out.println(endOfCurrentRow().getX() + " "
					+ endOfCurrentRow().getY());

			System.out.println(nextRow().getX() + " " + nextRow().getY());
		}
		RConsole.close();
	}

	private static Intersection endOfCurrentRow() {
		return getNextDestination(rowNumber);
	}

	private static Intersection nextRow() {
		if (SensorMotorUser.getXStart() == 0
				&& SensorMotorUser.getYStart() == 0
				&& rowNumber != Map.NUM_OF_INTERSECTIONS - 1) {
			return getNextDestination(rowNumber + 1);
		}

		else if (SensorMotorUser.getXStart() == 0
				&& SensorMotorUser.getYStart() == Map.NUM_OF_INTERSECTIONS - 1
				&& rowNumber != Map.NUM_OF_INTERSECTIONS - 1) {
			return getNextDestination(rowNumber + 1);
		}

		else if (SensorMotorUser.getXStart() == Map.NUM_OF_INTERSECTIONS - 1
				&& SensorMotorUser.getYStart() == Map.NUM_OF_INTERSECTIONS - 1
				&& rowNumber != 0) {
			return getNextDestination(rowNumber - 1);
		}

		else if (SensorMotorUser.getXStart() == Map.NUM_OF_INTERSECTIONS - 1
				&& SensorMotorUser.getYStart() == 0 && rowNumber != 0) {
			return getNextDestination(rowNumber - 1);
		}

		return Map.getIntersection(0, 0);
	}

	private static Intersection getValidIntersection(int tempRowNumber,
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

	private static Intersection getNextDestination(int tempRowNumber) {

		if (rowCounter % 2 == 0) {

			if (SensorMotorUser.getXStart() == 0
					&& SensorMotorUser.getYStart() == 0) {
				return getValidIntersection(tempRowNumber,
						Map.NUM_OF_INTERSECTIONS, true);
			}

			else if (SensorMotorUser.getXStart() == 0
					&& SensorMotorUser.getYStart() == Map.NUM_OF_INTERSECTIONS - 1) {
				return getValidIntersection(tempRowNumber, 1, false);
			}

			else if (SensorMotorUser.getXStart() == Map.NUM_OF_INTERSECTIONS - 1
					&& SensorMotorUser.getYStart() == Map.NUM_OF_INTERSECTIONS - 1) {
				return getValidIntersection(tempRowNumber, 1, true);
			}

			else if (SensorMotorUser.getXStart() == Map.NUM_OF_INTERSECTIONS - 1
					&& SensorMotorUser.getYStart() == 0) {
				return getValidIntersection(tempRowNumber,
						Map.NUM_OF_INTERSECTIONS, false);
			}

		}

		else if (rowCounter % 2 != 0) {

			if (SensorMotorUser.getXStart() == 0
					&& SensorMotorUser.getYStart() == 0) {
				return getValidIntersection(tempRowNumber, 1, true);
			}

			else if (SensorMotorUser.getXStart() == 0
					&& SensorMotorUser.getYStart() == Map.NUM_OF_INTERSECTIONS - 1) {
				return getValidIntersection(tempRowNumber,
						Map.NUM_OF_INTERSECTIONS, false);
			}

			else if (SensorMotorUser.getXStart() == Map.NUM_OF_INTERSECTIONS - 1
					&& SensorMotorUser.getYStart() == Map.NUM_OF_INTERSECTIONS - 1) {
				return getValidIntersection(tempRowNumber,
						Map.NUM_OF_INTERSECTIONS, true);
			}

			else if (SensorMotorUser.getXStart() == Map.NUM_OF_INTERSECTIONS - 1
					&& SensorMotorUser.getYStart() == 0) {
				return getValidIntersection(tempRowNumber, 1, false);
			}

		}

		return Map.getIntersection(0, 0);

	}

	private static boolean loopCondition(int currentPosition, int startPosition) {

		if (startPosition < (Map.NUM_OF_INTERSECTIONS - 1)) {
			return currentPosition < Map.NUM_OF_INTERSECTIONS;
		} else {
			return currentPosition >= 0;
		}
	}

	private static void loopAfterthought(int startPosition) {

		if (startPosition < (Map.NUM_OF_INTERSECTIONS - 1)) {
			rowNumber++;
		} else {
			rowNumber--;
		}

		rowCounter++;

	}
}
