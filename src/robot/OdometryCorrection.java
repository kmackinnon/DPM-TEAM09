package robot;

import lejos.nxt.ColorSensor;
import lejos.nxt.Sound;
import lejos.util.Timer;
import lejos.util.TimerListener;

public class OdometryCorrection extends SensorMotorUser implements
		TimerListener {

	private static final int DEFAULT_PERIOD = 25;

	private final double MIN_DISTANCE_BETWEEN_DETECTIONS = 25;
	private final double MAX_DISTANCE_BETWEEN_DIAGONAL_DETECTIONS = 14;
	private final double DIAGONAL_IN_RADIANS = Math.toRadians(45);

	private Timer correctionTimer;

	private Odometer odo;

	private boolean doCorrection;

	// straight line detection variables
	private boolean leftSensorDetected;
	private boolean rightSensorDetected;

	private double rightTachoAtDetection;
	private double leftTachoAtDetection;

	private double prevRightTachoAtDetection = 0;
	private double prevLeftTachoAtDetection = 0;

	private double distanceTravelledByLaggingWheel = 0;
	private double angleOff = 0;

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

	public OdometryCorrection(Odometer odo) {
		this.odo = odo;

		correctionTimer = new Timer(DEFAULT_PERIOD, this);

	}

	public void startCorrectionTimer() {
		correctionTimer.start();
		leftCS.setFloodlight(true);
		rightCS.setFloodlight(true);
	}

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

			double currentTheta = odo.getTheta();

			if (Math.abs(currentTheta - 45) < 10
					|| Math.abs(currentTheta - 135) < 10
					|| Math.abs(currentTheta - 225) < 10
					|| Math.abs(currentTheta - 315) < 10) {
				diagonalCorrection();
			}

			else {
				straightLineCorrection();
			}

		}

		/*
		 * LCD.clear(); LCD.drawInt((int)(System.currentTimeMillis() - start),
		 * 0, 4);
		 */

	}

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

				Sound.beep();

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

	private void xyDiagonalCorrection(double measuredDistance, ColorSensor cs) {

		boolean left = (cs == leftCS);
		double currentTheta = odo.getTheta();

			if (Math.abs(currentTheta - 45) < 10) {

				double distanceFromIntersection = actualDistanceFromIntersection(measuredDistance);

				if(left){
					double nearestIntersectionCoordinate = Map.nearestIntersectionCoordinate(yLeftAtFirstDetectDiagonal);
					odo.setY(nearestIntersectionCoordinate + distanceFromIntersection);
				}
				
				else{
					double nearestIntersectionCoordinate = Map.nearestIntersectionCoordinate(xRightAtFirstDetectDiagonal);
					odo.setX(nearestIntersectionCoordinate + distanceFromIntersection);
				}
				
			}

			else if (Math.abs(currentTheta - 135) < 10) {
				
				
				double distanceFromIntersection = actualDistanceFromIntersection(measuredDistance);

				if(left){
					double nearestIntersectionCoordinate = Map.nearestIntersectionCoordinate(xLeftAtFirstDetectDiagonal);
					odo.setX(nearestIntersectionCoordinate + distanceFromIntersection);
				}
				
				else{
					double nearestIntersectionCoordinate = Map.nearestIntersectionCoordinate(yRightAtFirstDetectDiagonal);
					odo.setY(nearestIntersectionCoordinate - distanceFromIntersection);
				}
				

			}

			else if (Math.abs(currentTheta - 225) < 10) {

				double distanceFromIntersection = actualDistanceFromIntersection(measuredDistance);

				if(left){
					double nearestIntersectionCoordinate = Map.nearestIntersectionCoordinate(yLeftAtFirstDetectDiagonal);
					odo.setY(nearestIntersectionCoordinate - distanceFromIntersection);
				}
				
				else{
					double nearestIntersectionCoordinate = Map.nearestIntersectionCoordinate(xRightAtFirstDetectDiagonal);
					odo.setX(nearestIntersectionCoordinate - distanceFromIntersection);

				}
			}

			else if (Math.abs(currentTheta - 315) < 10) {

				double distanceFromIntersection = actualDistanceFromIntersection(measuredDistance);

				if(left){
					double nearestIntersectionCoordinate = Map.nearestIntersectionCoordinate(xLeftAtFirstDetectDiagonal);
					odo.setX(nearestIntersectionCoordinate - distanceFromIntersection);
				}
				
				else{
					double nearestIntersectionCoordinate = Map.nearestIntersectionCoordinate(yRightAtFirstDetectDiagonal);
					odo.setY(nearestIntersectionCoordinate + distanceFromIntersection);

				}	
			}


	}

	private int prevValueL = 0;
	private int prevValueR = 0;
	private boolean negativeDiffL = false;
	private boolean negativeDiffR = false;
	private final int LINE_DIFF = 20;

	private boolean lineDetected(ColorSensor cs) {

		boolean left = (cs == leftCS);

		int value = cs.getRawLightValue();

		// LCD.clear();
		// LCD.drawInt((int)value, 0, 7);

		int diff = (left) ? (value - prevValueL) : (value - prevValueR);

		// RConsole.println("Diff: " + diff);
		if (diff < -LINE_DIFF) {
			if (left) {
				negativeDiffL = true;
			} else {
				negativeDiffR = true;
			}
		}

		if (left) {
			prevValueL = value;
		} else {
			prevValueR = value;
		}

		if (diff > LINE_DIFF) {
			if (negativeDiffL && left) {
				// RConsole.println("Ldetected");
				// Sound.beep();
				negativeDiffL = false;
				return true;
			} else if (negativeDiffR && !left) {
				// RConsole.println("Rdetected");
				// Sound.beep();
				negativeDiffR = false;
				return true;
			}
		}

		return false;
	}

	private void correctXY() {

		double currentTheta = odo.getTheta();

		if (Math.abs(currentTheta - 90) < 20) {
			odo.setX(xAtFirstDetection + (distanceTravelledByLaggingWheel)
					* Math.cos(angleOff));

			odo.setY(yAtFirstDetection - (distanceTravelledByLaggingWheel)
					* Math.sin(angleOff));
		}

		else if (Math.abs(currentTheta - 180) < 20) {
			odo.setX(xAtFirstDetection - (distanceTravelledByLaggingWheel)
					* Math.sin(angleOff));

			odo.setY(yAtFirstDetection - (distanceTravelledByLaggingWheel)
					* Math.cos(angleOff));
		}

		else if (Math.abs(currentTheta - 270) < 20) {
			odo.setX(xAtFirstDetection - (distanceTravelledByLaggingWheel)
					* Math.cos(angleOff));

			odo.setY(yAtFirstDetection + (distanceTravelledByLaggingWheel)
					* Math.sin(angleOff));
		}

		else if (currentTheta < 20 || currentTheta > 340) {

			odo.setX(xAtFirstDetection + (distanceTravelledByLaggingWheel)
					* Math.sin(angleOff));

			odo.setY(yAtFirstDetection + (distanceTravelledByLaggingWheel)
					* Math.cos(angleOff));
		}

	}

	private void correctAngle() {

		double currentAngleOff = 0;
		double adjustment;

		double currentTheta = odo.getTheta();

		if (Math.abs(currentTheta - 90) < 20) {
			currentAngleOff = currentTheta - 90;
		}

		else if (Math.abs(currentTheta - 180) < 20) {
			currentAngleOff = currentTheta - 180;
		}

		else if (Math.abs(currentTheta - 270) < 20) {
			currentAngleOff = currentTheta - 270;
		}

		else if (currentTheta < 20) {
			currentAngleOff = currentTheta;
		}

		else if (currentTheta > 340) {
			currentAngleOff = currentTheta - 360;
		}

		adjustment = Math.toDegrees(angleOff) - currentAngleOff;

		double newTheta = odo.fixDegAngle(currentTheta + adjustment);

		odo.setTheta(newTheta);

	}

	// This is for the first light sensor that detects the line.
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

	private double actualDistanceFromIntersection(double measuredDistance) {

		return measuredDistance - ((SENSOR_WIDTH / 2) * Math.cos(45))
				+ (SENSOR_TO_WHEEL_DISTANCE * Math.cos(45));

	}

}
