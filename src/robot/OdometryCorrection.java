package robot;

import lejos.nxt.ColorSensor;
import lejos.nxt.LCD;
import lejos.nxt.Sound;
import lejos.util.Timer;
import lejos.util.TimerListener;

public class OdometryCorrection extends SensorMotorUser implements
		TimerListener {

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

	private double prevRightTachoAtDetection;
	private double prevLeftTachoAtDetection;

	private double laggingWheelDistTravelled;
	private double firstWheelDistTravelled;
	private double angleOff;

	private double xAtFirstDetection;
	private double yAtFirstDetection;

	public OdometryCorrection(Odometer odo) {
		this.odo = odo;

		correctionTimer = new Timer(DEFAULT_TIMER_PERIOD, this);

	}

	public void turnOnCorrection() {

		leftCS.setFloodlight(true);
		rightCS.setFloodlight(true);

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

		resetCorrectionVariables();
		
		doStraightLineCorrection = true;
		doDiagonalCorrection = false;
		doRotationalCorrection = false;

	}

	public void doDiagonalCorrection() {

		resetCorrectionVariables();
		
		doDiagonalCorrection = true;
		doStraightLineCorrection = false;
		doRotationalCorrection = false;

	}

	public void doRotationalCorrection() {
		
		resetCorrectionVariables();

		doRotationalCorrection = true;
		doStraightLineCorrection = false;
		doDiagonalCorrection = false;
	}

	public void timedOut() {

		// long start = System.currentTimeMillis();

		if (doStraightLineCorrection) {

			straightLineCorrection();

		}

		else if (doDiagonalCorrection) {

		}

		else if (doRotationalCorrection) {
			rotationalCorrection();
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

				firstDetectionStraightLine(leftCS);

			}

			if (lineDetected(rightCS)
					&& isOneTileFromPreviousDetection(rightCS)) {

				firstDetectionStraightLine(rightCS);

			}

		}

		// left sensor has detected, but not the right
		if (leftSensorDetected && (!rightSensorDetected)) {

			secondDetectionStraightLine(rightCS);
		}

		// right sensor has detected, but not the left
		if ((!leftSensorDetected) && (rightSensorDetected)) {
			secondDetectionStraightLine(leftCS);
		}

		// both sensors have detected the line
		if (leftSensorDetected && rightSensorDetected) {

			if (laggingWheelDistTravelled != 0) {

				correctXY();

				correctAngle();

			}

			rightSensorDetected = false;
			leftSensorDetected = false;

			laggingWheelDistTravelled = 0;
			angleOff = 0;
			firstWheelDistTravelled = 0;

			prevRightTachoAtDetection = rightMotor.getTachoCount();
			prevLeftTachoAtDetection = leftMotor.getTachoCount();
		}

	}

	private void rotationalCorrection() {

		/*if(!MobileRobot.rotatingClockwise){
			
			if(lineDetected(rightCS)){
				
				double theta = odo.getTheta();
				
				double phi = 90 - theta;
				
				double d = (SENSOR_WIDTH / 2) / Math.tan(phi);
				
				double alpha = d - SENSOR_TO_WHEEL_DISTANCE;
				
				double yDistFromIntersection;
				
				if(alpha < 0){
					
					alpha = SENSOR_TO_WHEEL_DISTANCE - d;
					
					yDistFromIntersection = alpha * Math.sin(phi);
					
					ADD the distance to Y
					
				}
				
				else{
					
					distance from origin = alpha * Math.sin(phi);
					
					SUBTRACT the distance to Y
					
				}
				
			}
			
			if(lineDetected(leftCS)){
				
				d = (SENSOR_WIDTH / 2) * Math.tan(phi);
				
				alpha = d - SENSOR_TO_WHEEL_DISTANCE;
				
				if(alpha < 0){
					
					alpha = SENSOR_TO_WHEEL_DISTANCE - d;
					
					distance from origin = alpha * Math.sin(theta);
					
					SUBTRACT the distance to Y
					
				}
				
				else{
					
					distance from origin = alpha * Math.sin(theta);
					
					ADD the distance to X
					
				}
				
			}
		}*/
			

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

		double distanceTravelled = (laggingWheelDistTravelled + firstWheelDistTravelled) / 2;

		if (Math.abs(currentTheta - 90) < 20) {
			odo.setX(xAtFirstDetection + (distanceTravelled)
					* Math.cos(angleOff));

			odo.setY(yAtFirstDetection - (distanceTravelled)
					* Math.sin(angleOff));
		}

		else if (Math.abs(currentTheta - 180) < 20) {
			odo.setX(xAtFirstDetection - (distanceTravelled)
					* Math.sin(angleOff));

			odo.setY(yAtFirstDetection - (distanceTravelled)
					* Math.cos(angleOff));
		}

		else if (Math.abs(currentTheta - 270) < 20) {
			odo.setX(xAtFirstDetection - (distanceTravelled)
					* Math.cos(angleOff));

			odo.setY(yAtFirstDetection + (distanceTravelled)
					* Math.sin(angleOff));
		}

		else if (currentTheta < 20 || currentTheta > 340) {

			odo.setX(xAtFirstDetection + (distanceTravelled)
					* Math.sin(angleOff));

			odo.setY(yAtFirstDetection + (distanceTravelled)
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
	private void firstDetectionStraightLine(ColorSensor cs) {

		xAtFirstDetection = odo.getX();
		yAtFirstDetection = odo.getY();

		if (cs == leftCS) {

			rightTachoAtDetection = rightMotor.getTachoCount();
			leftTachoAtDetection = leftMotor.getTachoCount();

			Sound.beep();
			leftSensorDetected = true;
		}

		else {
			leftTachoAtDetection = leftMotor.getTachoCount();
			rightTachoAtDetection = rightMotor.getTachoCount();

			Sound.beep();
			rightSensorDetected = true;
		}

	}

	// This is when the light sensor detects the line after the other light
	// sensor.
	private void secondDetectionStraightLine(ColorSensor cs) {

		if (cs == rightCS) {
			double currentRightTacho = rightMotor.getTachoCount();
			double currentLeftTacho = rightMotor.getTachoCount();

			laggingWheelDistTravelled = 2 * Math.PI * RIGHT_RADIUS
					* ((currentRightTacho - rightTachoAtDetection) / 360);

			firstWheelDistTravelled = 2 * Math.PI * LEFT_RADIUS
					* ((currentLeftTacho - leftTachoAtDetection) / 360);

			if (laggingWheelDistTravelled > 10) {
				leftSensorDetected = false;
			}

			else if (lineDetected(rightCS)) {

				angleOff = Math.atan(laggingWheelDistTravelled / SENSOR_WIDTH);
				Sound.beep();
				rightSensorDetected = true;

			}
		}

		else {

			double currentLeftTacho = leftMotor.getTachoCount();
			double currentRightTacho = rightMotor.getTachoCount();

			laggingWheelDistTravelled = 2 * Math.PI * LEFT_RADIUS
					* ((currentLeftTacho - leftTachoAtDetection) / 360);

			firstWheelDistTravelled = 2 * Math.PI * RIGHT_RADIUS
					* ((currentRightTacho - rightTachoAtDetection) / 360);

			if (laggingWheelDistTravelled > 10) {
				rightSensorDetected = false;
			}

			else if (lineDetected(leftCS)) {

				angleOff = -Math.atan(laggingWheelDistTravelled / SENSOR_WIDTH);

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
	
	private void resetCorrectionVariables(){
		
		leftSensorDetected = false;
		rightSensorDetected = false;
		
		prevLeftTachoAtDetection = -Double.MAX_VALUE;
		prevRightTachoAtDetection = -Double.MAX_VALUE;
		
		
	}

}
