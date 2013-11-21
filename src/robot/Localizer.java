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
	private final int US_OFFSET = 2;

	public Localizer() {

	}

	public void localize() {

		liftClaw();
		
		ultrasonicLocalization();
		xyUltrasonicCorrection();

		travelTileCoordinate(getXStart(), getYStart());
		
		if(getXStart()==0 && getYStart()==0){
			turnToOnPoint(0);
		}
		
		else if(getXStart() == (Map.NUM_OF_INTERSECTIONS-1) && getYStart() == 0){
			turnToOnPoint(270);
		}
		
		else if(getXStart() == (Map.NUM_OF_INTERSECTIONS-1) && getYStart() == (Map.NUM_OF_INTERSECTIONS-1)){
			turnToOnPoint(180);
		}
		
		else if(getXStart() == 0 && getYStart() == (Map.NUM_OF_INTERSECTIONS-1)){
			turnToOnPoint(90);
		}
		
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
		
		if(getXStart()==0 && getYStart()==0){
			odo.setTheta(deltaTheta + angleB);
		}
		
		else if(getXStart() == (Map.NUM_OF_INTERSECTIONS-1) && getYStart() == 0){
			odo.setTheta(deltaTheta + angleB - 90);
		}
		
		else if(getXStart() == (Map.NUM_OF_INTERSECTIONS-1) && getYStart() == (Map.NUM_OF_INTERSECTIONS-1)){
			odo.setTheta(deltaTheta + angleB - 180);
		}
		
		else if(getXStart() == 0 && getYStart() == (Map.NUM_OF_INTERSECTIONS-1)){
			odo.setTheta(deltaTheta + angleB - 270);
		}


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

		//face first wall
		if(getXStart() == 0 && getYStart() == 0){
			turnToOnPoint(180);
			odo.setY(getUSDistance() - Map.TILE_SIZE + US_OFFSET);
		}
		
		else if(getXStart() == (Map.NUM_OF_INTERSECTIONS-1) && getYStart() == 0){
			turnToOnPoint(90);
			odo.setX(Map.NUM_OF_INTERSECTIONS * Map.TILE_SIZE - getUSDistance() - US_OFFSET);
		}
		
		else if(getXStart() == (Map.NUM_OF_INTERSECTIONS-1) && getYStart() == (Map.NUM_OF_INTERSECTIONS-1)){
			turnToOnPoint(0);
			odo.setY(Map.NUM_OF_INTERSECTIONS * Map.TILE_SIZE - getUSDistance() - US_OFFSET);
		}
		
		else if(getXStart() == 0 && getYStart() == (Map.NUM_OF_INTERSECTIONS-1)){
			turnToOnPoint(270);
			odo.setX(getUSDistance() - Map.TILE_SIZE + US_OFFSET);
		}
		
		
		//face second wall
		if(getXStart() == 0 && getYStart() == 0){
			turnToOnPoint(270);
			odo.setX(getUSDistance() - Map.TILE_SIZE + US_OFFSET);
		}
		
		else if(getXStart() == (Map.NUM_OF_INTERSECTIONS-1) && getYStart() == 0){
			turnToOnPoint(180);
			odo.setY(getUSDistance() - Map.TILE_SIZE + US_OFFSET);
			
		}
		
		else if(getXStart() == (Map.NUM_OF_INTERSECTIONS-1) && getYStart() == (Map.NUM_OF_INTERSECTIONS-1)){
			turnToOnPoint(90);
			odo.setX(Map.NUM_OF_INTERSECTIONS * Map.TILE_SIZE - getUSDistance() - US_OFFSET);
		}
		
		else if(getXStart() == 0 && getYStart() == (Map.NUM_OF_INTERSECTIONS-1)){
			turnToOnPoint(0);
			odo.setY(Map.NUM_OF_INTERSECTIONS * Map.TILE_SIZE - getUSDistance() - US_OFFSET);
		}
		

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
