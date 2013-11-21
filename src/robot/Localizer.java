package robot;

import java.util.Arrays;

/**
 * Localizer calculates the position and orientation of the robot at the start
 * of the game. This has to be done because the odometer initially assumes a
 * (0,0) position and 0 degree heading.
 */

public class Localizer extends MobileRobot {

	private double angleA; // angle of right wall (assuming facing away)
	private double angleB; // angle of left wall

	private int distanceArray[] = new int[5];
	private int medianDistance;

	private boolean isLatched = false;

	private long correctionStart, correctionEnd;
	
	private final int LATCH_ANGLE_DISTANCE = 30;

	public Localizer() {

	}

	public void localize() {

		liftClaw();
		
		ultrasonicLocalization();
		xyUltrasonicCorrection();

		travelTileCoordinate(0, 0);
		turnToOnPoint(0);
	}

	private void ultrasonicLocalization() {

		boolean isFacingWall;
		int countWall = 0;
		double deltaTheta;

		Arrays.fill(distanceArray, US_SENSOR_255);

		// determine initial position of looking at or away from wall
		for (int i = 0; i < 5; i++) {

			shiftArrayByOne(distanceArray, getUSDistance());

			medianDistance = getMedian(distanceArray);

			if (medianDistance <= 50) {
				countWall++;
			}
		}

		isFacingWall = countWall > 3; // either facing wall or away from wall

		if (!isFacingWall) {
			fallingEdge();
		}

		// robot starts by facing the wall
		// only begin falling edge routine once facing away from wall
		else {
			lookForEmptySpace();

			fallingEdge();
		}

		// calculates the corrected angle
		if (angleB > angleA) {
			deltaTheta = 225 - ((angleA + angleB) / 2);
		} else {
			deltaTheta = 45 - ((angleA + angleB) / 2);
		}

		// update the odometer position
		odo.setX(0);
		odo.setY(0);
		odo.setTheta(deltaTheta + angleB);

	}

	private void fallingEdge() {

		isLatched = false; // whether angle is recorded

		// head to right wall
		lookForRightWall();

		// to reset isLatched
		lookForEmptySpace();

		// head to left wall
		lookForLeftWall();

		stopMoving();
	}

	private void xyUltrasonicCorrection() {

		turnToOnPoint(180);
		odo.setY(getUSDistance() - 28);

		turnToOnPoint(270);
		odo.setX(getUSDistance() - 28);

		initializePrevTarget(odo.getX(),odo.getY());
	}

	private void lookForRightWall() {

		lookForWall(true);

	}

	private void lookForLeftWall() {

		lookForWall(false);

	}

	private void lookForEmptySpace() {

		int count255 = 0;

		// isLatched has been set true after finding right wall
		if (isLatched) {
			rotateCounterClockwiseOnPoint();
		}

		// isLatched is not true if we are looking for an empty space
		// without having latched an angle first
		else {
			rotateClockwiseOnPoint();
			isLatched = true;
		}

		while (isLatched) {
			correctionStart = System.currentTimeMillis();

			shiftArrayByOne(distanceArray, getUSDistance());

			medianDistance = getMedian(distanceArray);

			// ensure facing away from walls before attempting to detect angles
			if (medianDistance == US_SENSOR_255) {
				count255++;
			} else {
				count255 = 0;
			}

			// now ready to detect left wall
			if (count255 >= 5) {
				isLatched = false;
			}

			threadSleep();
		}
	}

	private void lookForWall(boolean rightWall) {

		if (rightWall) {

			rotateClockwiseOnPoint();
		}

		else {

			rotateCounterClockwiseOnPoint();
		}

		while (!isLatched) {
			correctionStart = System.currentTimeMillis();

			shiftArrayByOne(distanceArray, getUSDistance());

			medianDistance = getMedian(distanceArray);

			// left wall detected
			if (medianDistance < LATCH_ANGLE_DISTANCE) {

				if (rightWall) {
					angleA = odo.getTheta();
				}

				else {
					angleB = odo.getTheta();
				}

				break;
			}

			threadSleep();
		}

		isLatched = true;

	}

	private void threadSleep() {

		correctionEnd = System.currentTimeMillis();
		if (correctionEnd - correctionStart < DEFAULT_TIMER_PERIOD) {
			try {
				Thread.sleep(DEFAULT_TIMER_PERIOD
						- (correctionEnd - correctionStart));
			} catch (InterruptedException e) {
				// there is nothing to be done here because it is not
				// expected that the localization will be
				// interrupted by another thread
			}
		}

	}
	 

}
