package robot;

import lejos.nxt.ColorSensor;
import lejos.nxt.Sound;
import lejos.nxt.comm.RConsole;
import lejos.util.Timer;
import lejos.util.TimerListener;

/**
 * OdometryCorrection makes sure the odometer accurately reflects the position
 * and orientation of the robot. It uses line detections to accomplish this.
 * Diagonal correction was written and has not been deleted. However, we decided
 * not to use it since it did not seem to help.
 * 
 * @author Kevin Musgrave, Simon Lee, Sidney Ng, Keith MacKinnon
 */

public class OdometryCorrection extends SensorMotorUser implements
		TimerListener {

	/**
	 * The robot ignores line detections that happen within this distance of the
	 * previous line detections
	 */
	private final double MIN_DISTANCE_BETWEEN_DETECTIONS = 25;

	private final double MAX_DISTANCE_BETWEEN_DIAGONAL_DETECTIONS = 14;
	private final double DIAGONAL_IN_RADIANS = Math.toRadians(45);

	private final int LARGE_ANGLE_RANGE_TOLERANCE = 40;

	private Timer correctionTimer;

	private Odometer odo;

	private int lineCounter = 0;
	private int lineDetectIgnoreCounter = 0;

	private boolean doCorrection = false;

	// straight line detection variables

	/**
	 * true if the left sensor has detected a line
	 */
	private boolean leftSensorDetected;

	/**
	 * true if the right sensor has detected a line
	 */
	private boolean rightSensorDetected;

	private double rightTachoAtDetection;
	private double leftTachoAtDetection;

	private double prevRightTachoAtDetection;
	private double prevLeftTachoAtDetection;

	/**
	 * the distance travelled by the side of the robot which detects a line
	 * after the other side
	 */
	private double distanceTravelledByLaggingWheel;
	private double angleOff;

	private double xAtFirstDetection;
	private double yAtFirstDetection;
	// end of straight line detection variables

	// diagonal detection variables
	private boolean leftFirstDiagonalDetect;
	private boolean rightFirstDiagonalDetect;

	private double leftFirstTachoDiagonal;
	private double rightFirstTachoDiagonal;

	private double xLeftAtFirstDetectDiagonal;
	private double yLeftAtFirstDetectDiagonal;

	private double xRightAtFirstDetectDiagonal;
	private double yRightAtFirstDetectDiagonal;
	// end of diagonal detection variables

	// rotate correction variables

	/**
	 * the right color sensor has detected a line during rotational correction,
	 * and the right wheel has stopped moving
	 */
	private boolean rightCSDone;

	/**
	 * the left color sensor has detected a line during rotational correction,
	 * and the left wheel has stopped moving
	 */
	private boolean leftCSDone;

	/**
	 * the x value of the odometer when rotational correction begins.
	 */
	private double initialX;

	/**
	 * the y value of the odometer when rotational correction begins.
	 */
	private double initialY;

	/**
	 * the theta value of the odometer when rotational correction begins.
	 */
	private double initialTheta;

	public OdometryCorrection(Odometer odo) {
		this.odo = odo;

		correctionTimer = new Timer(DEFAULT_TIMER_PERIOD, this);

	}

	/**
	 * turns on the two color sensors
	 */
	public void turnOnLightSensors() {
		leftCS.setFloodlight(true);
		rightCS.setFloodlight(true);
	}

	/**
	 * stars the timer so that the timedOut method is called every period
	 */
	public void startCorrectionTimer() {
		correctionTimer.start();
	}

	/**
	 * turns on straight line odometry correction
	 * 
	 */
	public void turnOnCorrection() {

		leftSensorDetected = false;
		rightSensorDetected = false;

		leftFirstDiagonalDetect = false;
		rightFirstDiagonalDetect = false;

		// set the prevTacho counts to values that guarantee that the next line
		// will be considered.
		prevLeftTachoAtDetection = -Double.MAX_VALUE;
		prevRightTachoAtDetection = -Double.MAX_VALUE;

		doCorrection = true;

	}

	public void turnOffCorrection() {

		doCorrection = false;

	}

	public void timedOut() {

		// long start = System.currentTimeMillis();

		if (doCorrection) {

			Direction currentDirection = odo.getDirection();

			if (currentDirection == Direction.NORTH
					|| currentDirection == Direction.EAST
					|| currentDirection == Direction.SOUTH
					|| currentDirection == Direction.WEST) {

				straightLineCorrection();

			}

			// Direction currentDirection = odo.getDirection();
			//
			// if (currentDirection == Direction.NORTHEAST
			// || currentDirection == Direction.SOUTHEAST
			// || currentDirection == Direction.NORTHWEST
			// || currentDirection == Direction.SOUTHWEST) {
			// diagonalCorrection();
			// }
			//
			// else {
			//
			// }

		}

		/*
		 * LCD.clear(); LCD.drawInt((int)(System.currentTimeMillis() - start),
		 * 0, 4);
		 */

	}

	/**
	 * performs odometry correction when the robot is traveling on grid lines
	 * (i.e. not diagonally).
	 */
	private void straightLineCorrection() {

		// neither sensor has detected a line
		if ((!leftSensorDetected) && (!rightSensorDetected)) {

			if (lineDetected(leftCS) && isOneTileFromPreviousDetection(leftCS)) {

				firstDetection(leftCS);

			}

			if (lineDetected(rightCS)
					&& isOneTileFromPreviousDetection(rightCS)) {

				firstDetection(rightCS);

			}

		}

		// left sensor has detected, but not the right
		if (leftSensorDetected && (!rightSensorDetected)) {

			secondDetection(rightCS);
		}

		// right sensor has detected, but not the left
		if ((!leftSensorDetected) && (rightSensorDetected)) {
			secondDetection(leftCS);
		}

		// both sensors have detected the line
		if (leftSensorDetected && rightSensorDetected) {

			lineCounter++;

			if (distanceTravelledByLaggingWheel != 0) {

				correctXY();

				correctAngle();

			}

			rightSensorDetected = false;
			leftSensorDetected = false;

			distanceTravelledByLaggingWheel = 0;
			angleOff = 0;

			prevRightTachoAtDetection = rightMotor.getTachoCount();
			prevLeftTachoAtDetection = leftMotor.getTachoCount();
		}

	}

	/**
	 * We did not use diagonal odometry correction
	 */
	private void diagonalCorrection() {

		if (lineDetected(leftCS)) {

			if (!leftFirstDiagonalDetect) {

				leftFirstDiagonalDetect = true;

				leftFirstTachoDiagonal = leftMotor.getTachoCount();

				xLeftAtFirstDetectDiagonal = odo.getX();
				yLeftAtFirstDetectDiagonal = odo.getY();
			}

			else if (isLessThanHalfTileFromPreviousDetection(leftCS)) {

				// Sound.beep();

				double diagonalDistanceTravelled = distanceTravelled(
						leftMotor.getTachoCount() - leftFirstTachoDiagonal,
						true);

				double leftCSMeasuredDistance = diagonalDistanceTravelled
						* Math.cos(DIAGONAL_IN_RADIANS);

				xyDiagonalCorrection(leftCSMeasuredDistance, leftCS);

				leftFirstDiagonalDetect = false;

			}

		}

		if (lineDetected(rightCS)) {

			if (!rightFirstDiagonalDetect) {

				rightFirstDiagonalDetect = true;

				rightFirstTachoDiagonal = rightMotor.getTachoCount();

				xRightAtFirstDetectDiagonal = odo.getX();
				yRightAtFirstDetectDiagonal = odo.getY();
			}

			else if (isLessThanHalfTileFromPreviousDetection(rightCS)) {

				// Sound.beep();

				double diagonalDistanceTravelled = distanceTravelled(
						rightMotor.getTachoCount() - rightFirstTachoDiagonal,
						false);

				double rightCSMeasuredDistance = diagonalDistanceTravelled
						* Math.cos(DIAGONAL_IN_RADIANS);

				xyDiagonalCorrection(rightCSMeasuredDistance, rightCS);

				rightFirstDiagonalDetect = false;

			}
		}

	}

	/**
	 * We did not use diagonal odometry correction
	 * 
	 * @param measuredDistance
	 * @param cs
	 */
	private void xyDiagonalCorrection(double measuredDistance, ColorSensor cs) {

		boolean left = (cs == leftCS);
		Direction currentDirection = odo.getDirection();

		if (currentDirection == Direction.NORTHEAST) {

			double distanceFromIntersection = actualDistanceFromIntersection(measuredDistance);

			if (left) {
				double nearestIntersectionCoordinate = Map
						.nearestIntersectionCoordinate(yLeftAtFirstDetectDiagonal);
				odo.setY(nearestIntersectionCoordinate
						+ distanceFromIntersection);
			}

			else {
				double nearestIntersectionCoordinate = Map
						.nearestIntersectionCoordinate(xRightAtFirstDetectDiagonal);
				odo.setX(nearestIntersectionCoordinate
						+ distanceFromIntersection);
			}

		}

		else if (currentDirection == Direction.SOUTHEAST) {

			double distanceFromIntersection = actualDistanceFromIntersection(measuredDistance);

			if (left) {
				double nearestIntersectionCoordinate = Map
						.nearestIntersectionCoordinate(xLeftAtFirstDetectDiagonal);
				odo.setX(nearestIntersectionCoordinate
						+ distanceFromIntersection);
			}

			else {
				double nearestIntersectionCoordinate = Map
						.nearestIntersectionCoordinate(yRightAtFirstDetectDiagonal);
				odo.setY(nearestIntersectionCoordinate
						- distanceFromIntersection);
			}

		}

		else if (currentDirection == Direction.SOUTHWEST) {

			double distanceFromIntersection = actualDistanceFromIntersection(measuredDistance);

			if (left) {
				double nearestIntersectionCoordinate = Map
						.nearestIntersectionCoordinate(yLeftAtFirstDetectDiagonal);
				odo.setY(nearestIntersectionCoordinate
						- distanceFromIntersection);
			}

			else {
				double nearestIntersectionCoordinate = Map
						.nearestIntersectionCoordinate(xRightAtFirstDetectDiagonal);
				odo.setX(nearestIntersectionCoordinate
						- distanceFromIntersection);

			}
		}

		else if (currentDirection == Direction.NORTHWEST) {

			double distanceFromIntersection = actualDistanceFromIntersection(measuredDistance);

			if (left) {
				double nearestIntersectionCoordinate = Map
						.nearestIntersectionCoordinate(xLeftAtFirstDetectDiagonal);
				odo.setX(nearestIntersectionCoordinate
						- distanceFromIntersection);
			}

			else {
				double nearestIntersectionCoordinate = Map
						.nearestIntersectionCoordinate(yRightAtFirstDetectDiagonal);
				odo.setY(nearestIntersectionCoordinate
						+ distanceFromIntersection);

			}
		}

	}

	private int prevValueL = 0;
	private int prevValueR = 0;
	private boolean negativeDiffL = false;
	private boolean negativeDiffR = false;
	/**
	 * The difference in the color sensor values that signals a line detection
	 * when the robot moves at regular speed.
	 */
	private final int LINE_DIFF = 20;
	/**
	 * The difference in the color sensor values that signals a line detection
	 * when the robot moves slowly.
	 */
	private final int SLOW_LINE_DIFF = 5;

	/**
	 * 
	 * @param cs
	 *            the left or right color sensor
	 * @param lineDiff
	 *            the difference we require to determine if a line has been
	 *            detected
	 * @return true if the input color sensor has detected a line
	 */
	private boolean lineDetected(ColorSensor cs, int lineDiff) {

		lineDetectIgnoreCounter++;

		boolean left = (cs == leftCS);

		int value = cs.getRawLightValue();
		RConsole.println("" + value);

		int diff = (left) ? (value - prevValueL) : (value - prevValueR);

		if (lineDetectIgnoreCounter >= 100) {
			// RConsole.println("Diff: " + diff);
			if (value < 545) {
				if (diff < -lineDiff) {
					if (left) {
						negativeDiffL = true;
					} else {
						negativeDiffR = true;
					}
				}
			}
		}

		if (left) {
			prevValueL = value;
		} else {
			prevValueR = value;
		}

		if (lineDetectIgnoreCounter >= 100) {
			if (diff > lineDiff) {
				if (negativeDiffL && left) {
					// RConsole.print(" Ldetected");
					Sound.beep();
					negativeDiffL = false;
					return true;
				} else if (negativeDiffR && !left) {
					// RConsole.print(" Rdetected");
					Sound.beep();
					negativeDiffR = false;
					return true;
				}
			}
		}

		return false;
	}

	private boolean lineDetected(ColorSensor cs) {
		return lineDetected(cs, LINE_DIFF);
	}

	/**
	 * determines if a line has been detected when the robot is moving slowly
	 * 
	 * @param cs
	 * @return
	 */
	private boolean lineDetectedSlow(ColorSensor cs) {
		return lineDetected(cs, SLOW_LINE_DIFF);
	}

	/**
	 * helper function for straight line odometry correction
	 */
	private void correctXY() {

		Direction currentDirection = odo.getDirection();

		if (currentDirection == Direction.EAST) {
			odo.setX(xAtFirstDetection + (distanceTravelledByLaggingWheel)
					* Math.cos(angleOff));

			odo.setY(yAtFirstDetection - (distanceTravelledByLaggingWheel)
					* Math.sin(angleOff));
		}

		else if (currentDirection == Direction.SOUTH) {
			odo.setX(xAtFirstDetection - (distanceTravelledByLaggingWheel)
					* Math.sin(angleOff));

			odo.setY(yAtFirstDetection - (distanceTravelledByLaggingWheel)
					* Math.cos(angleOff));
		}

		else if (currentDirection == Direction.WEST) {
			odo.setX(xAtFirstDetection - (distanceTravelledByLaggingWheel)
					* Math.cos(angleOff));

			odo.setY(yAtFirstDetection + (distanceTravelledByLaggingWheel)
					* Math.sin(angleOff));
		}

		else if (currentDirection == Direction.NORTH) {

			odo.setX(xAtFirstDetection + (distanceTravelledByLaggingWheel)
					* Math.sin(angleOff));

			odo.setY(yAtFirstDetection + (distanceTravelledByLaggingWheel)
					* Math.cos(angleOff));
		}

	}

	/**
	 * helper function for straight line odometry correction
	 */
	private void correctAngle() {

		double currentAngleOff = 0;
		double adjustment;
		double currentTheta = odo.getTheta();

		Direction currentDirection = odo.getDirection();

		if (currentDirection == Direction.EAST) {
			currentAngleOff = currentTheta - 90;
		}

		else if (currentDirection == Direction.SOUTH) {
			currentAngleOff = currentTheta - 180;
		}

		else if (currentDirection == Direction.WEST) {
			currentAngleOff = currentTheta - 270;
		}

		else if (currentDirection == Direction.NORTH) {
			if (currentTheta < 20) {
				currentAngleOff = currentTheta;
			}

			else if (currentTheta > 340) {
				currentAngleOff = currentTheta - 360;
			}
		}

		adjustment = Math.toDegrees(angleOff) - currentAngleOff;

		double newTheta = odo.fixDegAngle(currentTheta + adjustment);

		odo.setTheta(newTheta);

	}

	// This is for the first light sensor that detects the line.
	/**
	 * helper function for straight line odometry correction
	 */
	private void firstDetection(ColorSensor cs) {

		xAtFirstDetection = odo.getX();
		yAtFirstDetection = odo.getY();

		if (cs == leftCS) {

			rightTachoAtDetection = rightMotor.getTachoCount();

			// Sound.beep();
			leftSensorDetected = true;
		}

		else {
			leftTachoAtDetection = leftMotor.getTachoCount();

			// Sound.beep();
			rightSensorDetected = true;
		}

	}

	// This is when the light sensor detects the line after the other light
	// sensor.
	/**
	 * helper function for straight line odometry correction
	 */
	private void secondDetection(ColorSensor cs) {

		if (cs == rightCS) {
			double currentRightTacho = rightMotor.getTachoCount();

			distanceTravelledByLaggingWheel = distanceTravelled(
					currentRightTacho - rightTachoAtDetection, false);

			if (distanceTravelledByLaggingWheel > 10) {
				leftSensorDetected = false;
			}

			else if (lineDetected(rightCS)) {

				angleOff = Math.atan(distanceTravelledByLaggingWheel
						/ SENSOR_WIDTH);
				// Sound.beep();
				rightSensorDetected = true;

			}
		}

		else {

			double currentLeftTacho = leftMotor.getTachoCount();

			distanceTravelledByLaggingWheel = distanceTravelled(
					currentLeftTacho - leftTachoAtDetection, true);

			if (distanceTravelledByLaggingWheel > 10) {
				rightSensorDetected = false;
			}

			else if (lineDetected(leftCS)) {

				angleOff = -Math.atan(distanceTravelledByLaggingWheel
						/ SENSOR_WIDTH);

				// Sound.beep();
				leftSensorDetected = true;

			}

		}

	}

	/**
	 * 
	 * @param cs
	 *            the left or right color sensor
	 * @return true if the robot is approximately one tile from the previous
	 *         line detection
	 */
	private boolean isOneTileFromPreviousDetection(ColorSensor cs) {

		if (cs == leftCS) {
			return distanceTravelled(leftMotor.getTachoCount()
					- prevLeftTachoAtDetection, true) > MIN_DISTANCE_BETWEEN_DETECTIONS;
		}

		else {
			return distanceTravelled(rightMotor.getTachoCount()
					- prevRightTachoAtDetection, false) > MIN_DISTANCE_BETWEEN_DETECTIONS;
		}

	}

	/**
	 * This was only used for diagonal odometry correction
	 * 
	 * @param cs
	 * @return
	 */
	private boolean isLessThanHalfTileFromPreviousDetection(ColorSensor cs) {

		if (cs == leftCS) {
			return distanceTravelled(leftMotor.getTachoCount()
					- leftFirstTachoDiagonal, true) < MAX_DISTANCE_BETWEEN_DIAGONAL_DETECTIONS;
		}

		else {
			return distanceTravelled(rightMotor.getTachoCount()
					- rightFirstTachoDiagonal, false) < MAX_DISTANCE_BETWEEN_DIAGONAL_DETECTIONS;
		}

	}

	private double distanceTravelled(double tachoCountDifference, boolean left) {
		if (left) {

			return (2 * Math.PI * LEFT_RADIUS * tachoCountDifference) / 360;

		}

		else {

			return (2 * Math.PI * RIGHT_RADIUS * tachoCountDifference) / 360;
		}

	}

	/**
	 * helper for diagonal odometry correction (which we did not end up using)
	 * 
	 * @param measuredDistance
	 * @return
	 */
	private double actualDistanceFromIntersection(double measuredDistance) {

		return measuredDistance - ((SENSOR_WIDTH / 2) * Math.cos(45))
				+ (SENSOR_TO_WHEEL_DISTANCE * Math.cos(45));

	}

	/**
	 * This is used periodically to do major correction on the odometer. The
	 * robot slowly moves forward until both sides detect the line. When one
	 * side of the robot detects the line, that side's motor stops while the
	 * other motor keeps moving until the other sensor detects the line as well.
	 */
	public void rotateCorrection() {

		lineDetectIgnoreCounter = 0;

		initialX = odo.getX();
		initialY = odo.getY();
		initialTheta = odo.getTheta();

		while ((!rightCSDone) || (!leftCSDone)) {

			if ((!rightCSDone) && lineDetectedSlow(rightCS)) {
				rightCSDone = true;
				rightMotor.setSpeed(0);
			}

			if ((!leftCSDone) && lineDetectedSlow(leftCS)) {
				leftCSDone = true;
				leftMotor.setSpeed(0);
			}

			if (rightCSDone && leftCSDone) {
				Sound.beep();
				if (odo.getTheta() - initialTheta > LARGE_ANGLE_RANGE_TOLERANCE) {
					break;
				}

				odo.setTheta(90 * Math.round(odo.getTheta() / 90));

				Direction currentDirection = odo.getDirection();

				if (currentDirection == Direction.NORTH) {
					odo.setY(Map.nearestIntersectionCoordinate(initialY)
							+ SENSOR_TO_WHEEL_DISTANCE);
				}

				else if (currentDirection == Direction.EAST) {
					odo.setX(Map.nearestIntersectionCoordinate(initialX)
							+ SENSOR_TO_WHEEL_DISTANCE);
				}

				else if (currentDirection == Direction.SOUTH) {
					odo.setY(Map.nearestIntersectionCoordinate(initialY)
							- SENSOR_TO_WHEEL_DISTANCE);
				}

				else if (currentDirection == Direction.WEST) {
					odo.setX(Map.nearestIntersectionCoordinate(initialX)
							- SENSOR_TO_WHEEL_DISTANCE);
				}

			}
		}

		rightCSDone = false;
		leftCSDone = false;

	}

	public int getLineCount() {
		return lineCounter;
	}

}
