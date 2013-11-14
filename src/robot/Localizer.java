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
	
	int distanceArray[] = new int[5];
	int medianDistance;
	
	long correctionStart, correctionEnd;

	public Localizer() {

	}

	public void localize() {

		clawMotor.setSpeed(120);
		clawMotor.rotateTo(340);

		ultrasonicLocalization();
		xyUltrasonicCorrection();
		
		travelCoordinate(0,0);
		turnTo(0);
	}

	private void ultrasonicLocalization() {

		boolean isFacingWall;
		int countWall = 0;
		double deltaTheta;
		
		Arrays.fill(distanceArray,255);

		// determine initial position of looking at or away from wall
		for (int i = 0; i < 5; i++) {
			
			shiftArrayByOne(distanceArray,getUSDistance());
			
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

			int count255 = 0;

			while (count255 < 5) {
				setRotationSpeed(ROTATION_SPEED);
				
				shiftArrayByOne(distanceArray,getUSDistance());
				
				medianDistance = getMedian(distanceArray);
				
				if (medianDistance == 255) {
					count255++;
				}
			}

			fallingEdge();

		}

		// to stop the rotation
		setRotationSpeed(0);

		// calculates the corrected angle
		if (angleB > angleA) {
			deltaTheta = 225 - ((angleA + angleB) / 2);
		} else {
			deltaTheta = 45 - ((angleA + angleB) / 2);
		}

		// update the odometer position (example to follow:)
		odo.setPosition(new double[] { 0.0, 0.0, deltaTheta + angleB },
				new boolean[] { true, true, true });

	}

	private void fallingEdge() {
		boolean isLatched = false; // whether angle is recorded
		int count255 = 0;
		
		
		// head to right wall
		while (!isLatched) {
			correctionStart = System.currentTimeMillis();
			
			setRotationSpeed(ROTATION_SPEED);
			
			shiftArrayByOne(distanceArray,getUSDistance());
			
			medianDistance = getMedian(distanceArray);

			// right wall detected
			if (medianDistance < 30) {
				angleA = odo.getAng(); // latch angle
				isLatched = true;
				break;
			}
			
			threadSleep();
			
		}

		// to reset isLatched
		while (isLatched) {
			correctionStart = System.currentTimeMillis();
			
			setRotationSpeed(-ROTATION_SPEED);
			
			shiftArrayByOne(distanceArray,getUSDistance());
			
			medianDistance = getMedian(distanceArray);

			// ensure facing away from walls before attempting to detect angles
			if (medianDistance == 255) {
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

		// head to left wall
		while (!isLatched) {
			correctionStart = System.currentTimeMillis();
			
			setRotationSpeed(-ROTATION_SPEED);
			
			shiftArrayByOne(distanceArray,getUSDistance());
			
			medianDistance = getMedian(distanceArray);

			// left wall detected
			if (medianDistance < 30) {
				angleB = odo.getAng(); // latch angle
				break;
			}
			
			threadSleep();
		}
	}
	
	
	private void xyUltrasonicCorrection(){
		
		turnTo(180);
        odo.setY(getUSDistance()  - 28);

        turnTo(270);
        odo.setX(getUSDistance() - 28);
		
	}
	
	private void threadSleep(){
		
		correctionEnd = System.currentTimeMillis();
        if (correctionEnd - correctionStart < SLEEP_PERIOD) {
                try {
                        Thread.sleep(SLEEP_PERIOD
                                        - (correctionEnd - correctionStart));
                } catch (InterruptedException e) {
                        // there is nothing to be done here because it is not
                        // expected that the localization will be
                        // interrupted by another thread
                }
        }
		
	}
	
	

	/*private void lightLocalization() {

		double rightAngle1 = -1; // initialized to impossible values
		double rightAngle2 = -1;
		double rightAngle3 = -1;
		double rightAngle4 = -1;

		double leftAngle1 = -1; // initialized to impossible values
		double leftAngle2 = -1;
		double leftAngle3 = -1;
		double leftAngle4 = -1;

		double angle1 = -1; // initialized to impossible values
		double angle2 = -1;
		double angle3 = -1;
		double angle4 = -1;

		boolean allLinesDetected = false;

		while (!allLinesDetected) {

			setRotationSpeed(-ROTATION_SPEED);

			if (lineDetected(rightCS)) {
				Sound.beep(); // to aid in debugging -- to test for lines
				if (rightAngle1 == -1) {
					rightAngle1 = odo.getAng();
				} else if (rightAngle2 == -1) {
					rightAngle2 = odo.getAng();
				} else if (rightAngle3 == -1) {
					rightAngle3 = odo.getAng();
				} else if (rightAngle4 == -1) {
					rightAngle4 = odo.getAng();
				}
			}

			// if the robot is crossing a line, get respective angles
			if (lineDetected(leftCS)) {
				Sound.beep(); // to aid in debugging -- to test for lines
				if (leftAngle1 == -1) {
					leftAngle1 = odo.getAng();
				} else if (leftAngle2 == -1) {
					leftAngle2 = odo.getAng();
				} else if (leftAngle3 == -1) {
					leftAngle3 = odo.getAng();
				} else if (leftAngle4 == -1) {
					leftAngle4 = odo.getAng();
				}
			}

			if (leftAngle4 != -1 && rightAngle4 != -1) {
				allLinesDetected = true;
			}

		}

		setRotationSpeed(0);
		
		angle1 = (leftAngle1+rightAngle1)/2;
		angle2 = (leftAngle2+rightAngle2)/2;
		angle3 = (leftAngle3+rightAngle3)/2;
		angle4 = (leftAngle4+rightAngle4)/2;

		double x, y, thetaY, thetaX;

		thetaY = Math.abs(angle1 - angle3);
		thetaX = 360 - Math.abs(angle4 - angle2);

		if (thetaY > 180) {
			thetaY = 360 - thetaY;
		}

		if (thetaX > 180) {
			thetaX = 360 - thetaX;
		}

		// calculate correct x and y positions
		x = -lightSensorToWheel * Math.cos(Math.toRadians(thetaY / 2));
		y = -lightSensorToWheel * Math.cos(Math.toRadians(thetaX / 2));
		
		odo.setPosition(new double[] { x, y, 0},
				new boolean[] { true, true, false });

		// navigate to point (0,0) and then set heading to 0 degrees
		travelCoordinate(0, 0);
		turnTo(0);

	}*/

}
