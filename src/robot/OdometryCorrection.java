package robot;

import lejos.nxt.ColorSensor;
import lejos.nxt.LCD;
import lejos.nxt.Sound;
import lejos.util.Timer;
import lejos.util.TimerListener;

public class OdometryCorrection extends SensorMotorUser implements
		TimerListener {

	private static final int DEFAULT_PERIOD = 25;

	private final double MIN_DISTANCE_BETWEEN_DETECTIONS = 25;

	private Timer correctionTimer;

	private Odometer odo;

	private boolean doStraightLineCorrection;
	private boolean doDiagonalCorrection;
	private boolean doRotationalCorrection;

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

	public OdometryCorrection(Odometer odo) {
		this.odo = odo;

		correctionTimer = new Timer(DEFAULT_PERIOD, this);

	}

	public void turnOnCorrection() {

		leftCS.setFloodlight(true);
		rightCS.setFloodlight(true);

		leftSensorDetected = false;
		rightSensorDetected = false;

		// straight line correction is the default
		doStraightLineCorrection();

		correctionTimer.start();

	}

	public void turnOffCorrection() {

		leftCS.setFloodlight(false);
		rightCS.setFloodlight(false);

		correctionTimer.stop();

	}

	public void doStraightLineCorrection() {

		// set the prevTacho counts to values that guarantee that the next line
		// will be considered.
		prevLeftTachoAtDetection = -Double.MAX_VALUE;
		prevRightTachoAtDetection = -Double.MAX_VALUE;

		doStraightLineCorrection = true;
		doDiagonalCorrection = false;
		doRotationalCorrection = false;

	}

	public void doDiagonalCorrection() {

		doDiagonalCorrection = true;
		doStraightLineCorrection = false;
		doRotationalCorrection = false;

	}

	public void doRotationalCorrection() {

		doRotationalCorrection = true;
		doStraightLineCorrection = false;
		doDiagonalCorrection = false;
	}

	public void timedOut() {

		//long start = System.currentTimeMillis();
		
		if (doStraightLineCorrection) {

			straightLineCorrection();
			
		}
		
		else if(doDiagonalCorrection){
			
		}
		
		else if(doRotationalCorrection){
			//rotationalCorrection();
		}
		
		/*LCD.clear();
		LCD.drawInt((int)(System.currentTimeMillis() - start), 0, 4);*/

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

	private int prevValueL = 0;
	private int prevValueR = 0;
	private boolean negativeDiffL = false;
	private boolean negativeDiffR = false;
	private static final int LINE_DIFF = 20;

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

		odo.setTheta(currentTheta + adjustment);

	}

	// This is for the first light sensor that detects the line.
	private void firstDetection(ColorSensor cs) {

		xAtFirstDetection = odo.getX();
		yAtFirstDetection = odo.getY();

		if (cs == leftCS) {

			rightTachoAtDetection = rightMotor.getTachoCount();

			Sound.beep();
			leftSensorDetected = true;
		}

		else {
			leftTachoAtDetection = leftMotor.getTachoCount();

			Sound.beep();
			rightSensorDetected = true;
		}

	}

	// This is when the light sensor detects the line after the other light
	// sensor.
	private void secondDetection(ColorSensor cs) {

		if (cs == rightCS) {
			double currentRightTacho = rightMotor.getTachoCount();

			distanceTravelledByLaggingWheel = 2 * Math.PI * RIGHT_RADIUS
					* ((currentRightTacho - rightTachoAtDetection) / 360);

			if (distanceTravelledByLaggingWheel > 10) {
				leftSensorDetected = false;
			}

			else if (lineDetected(rightCS)) {

				angleOff = Math.atan(distanceTravelledByLaggingWheel
						/ SENSOR_WIDTH);
				Sound.beep();
				rightSensorDetected = true;

			}
		}

		else {

			double currentLeftTacho = leftMotor.getTachoCount();

			distanceTravelledByLaggingWheel = 2 * Math.PI * LEFT_RADIUS
					* ((currentLeftTacho - leftTachoAtDetection) / 360);

			if (distanceTravelledByLaggingWheel > 10) {
				rightSensorDetected = false;
			}

			else if (lineDetected(leftCS)) {

				angleOff = -Math.atan(distanceTravelledByLaggingWheel
						/ SENSOR_WIDTH);

				Sound.beep();
				leftSensorDetected = true;

			}

		}

	}

	private boolean isOneTileFromPreviousDetection(ColorSensor cs) {

		if (cs == leftCS) {
			return ((leftMotor.getTachoCount() - prevLeftTachoAtDetection) > ((MIN_DISTANCE_BETWEEN_DETECTIONS * 360) / (2 * Math.PI * LEFT_RADIUS)));
		}

		else {
			return ((rightMotor.getTachoCount() - prevRightTachoAtDetection) > ((MIN_DISTANCE_BETWEEN_DETECTIONS * 360) / (2 * Math.PI * RIGHT_RADIUS)));
		}

	}

}
