package robot;

import lejos.nxt.ColorSensor;
import lejos.nxt.LCD;
import lejos.nxt.Sound;
import lejos.util.Timer;
import lejos.util.TimerListener;

public class OdometryCorrection extends SensorMotorUser implements
		TimerListener {

	private static final int DEFAULT_PERIOD = 25;

	private final double MIN_DISTANCE_BETWEEN_DETECTIONS = 20;

	private Timer correctionTimer;

	private Odometer odo;

	private boolean leftSensorDetected;
	private boolean rightSensorDetected;

	private double rightTachoAtDetection;
	private double leftTachoAtDetection;

	private double prevRightTachoAtDetection = 0;
	private double prevLeftTachoAtDetection = 0;

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

		correctionTimer.start();

	}

	public void turnOffCorrection() {

		leftCS.setFloodlight(false);
		rightCS.setFloodlight(false);

		correctionTimer.stop();

	}

	public void timedOut() {

		double distanceTravelledByLaggingWheel = 0;
		double angleOff = 0;

		if (!MobileRobot.isTurning) {

			if ((!leftSensorDetected) && (!rightSensorDetected)) {

				if (lineDetected(leftCS)) {

					// if left has detected, then this is a new line; take
					// position
					// and tacho count
					// odo.getPosition(positionAtFirstDetection);

					xAtFirstDetection = odo.getX();
					yAtFirstDetection = odo.getY();

					rightTachoAtDetection = rightMotor.getTachoCount();
					leftTachoAtDetection = leftMotor.getTachoCount();

					/*if ((leftTachoAtDetection - prevLeftTachoAtDetection) > ((MIN_DISTANCE_BETWEEN_DETECTIONS * 360)
							/ (2 * Math.PI * LEFT_RADIUS))){*/
						Sound.beep();
						leftSensorDetected = true;
					//}
					
					/*else{
						leftSensorDetected = false;
					}*/

						
				}

				if (lineDetected(rightCS)) {

					// if right has detected, then this is a new line; take
					// position
					// and tacho count
					// odo.getPosition(positionAtFirstDetection);

					xAtFirstDetection = odo.getX();
					yAtFirstDetection = odo.getY();

					leftTachoAtDetection = leftMotor.getTachoCount();
					rightTachoAtDetection = rightMotor.getTachoCount();
					
				/*	if ((rightTachoAtDetection - prevRightTachoAtDetection) > ((MIN_DISTANCE_BETWEEN_DETECTIONS * 360)
							/ (2 * Math.PI * RIGHT_RADIUS))){*/
						Sound.beep();
						rightSensorDetected = true;
						//}
					
					/*else{
						rightSensorDetected = false;
					}*/
					
				}

			}

			if (leftSensorDetected && (!rightSensorDetected)) {

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

			if ((!leftSensorDetected) && (rightSensorDetected)) {

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

			if (leftSensorDetected && rightSensorDetected) {

				if (distanceTravelledByLaggingWheel != 0) {

					correctXY(distanceTravelledByLaggingWheel, angleOff);

					correctAngle(angleOff);

				}

				rightSensorDetected = false;
				leftSensorDetected = false;

			}

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
				//Sound.beep();
				negativeDiffL = false;
				return true;
			} else if (negativeDiffR && !left) {
				// RConsole.println("Rdetected");
				//Sound.beep();
				negativeDiffR = false;
				return true;
			}
		}

		return false;
	}

	private void correctXY(double distanceTravelledByLaggingWheel,
			double angleOff) {

		if (Math.abs(odo.getTheta() - 90) < 20) {
			odo.setX(xAtFirstDetection + (distanceTravelledByLaggingWheel)
					* Math.cos(angleOff));

			odo.setY(yAtFirstDetection - (distanceTravelledByLaggingWheel)
					* Math.sin(angleOff));
		}

		else if (Math.abs(odo.getTheta() - 180) < 20) {
			odo.setX(xAtFirstDetection - (distanceTravelledByLaggingWheel)
					* Math.sin(angleOff));

			odo.setY(yAtFirstDetection - (distanceTravelledByLaggingWheel)
					* Math.cos(angleOff));
		}

		else if (Math.abs(odo.getTheta() - 270) < 20) {
			odo.setX(xAtFirstDetection - (distanceTravelledByLaggingWheel)
					* Math.cos(angleOff));

			odo.setY(yAtFirstDetection + (distanceTravelledByLaggingWheel)
					* Math.sin(angleOff));
		}

		else if (odo.getTheta() < 20 || odo.getTheta() > 340) {

			odo.setX(xAtFirstDetection + (distanceTravelledByLaggingWheel)
					* Math.sin(angleOff));

			odo.setY(yAtFirstDetection + (distanceTravelledByLaggingWheel)
					* Math.cos(angleOff));
		}

	}

	private void correctAngle(double angleOff) {

		double currentAngleOff = 0;
		double adjustment;

		if (Math.abs(odo.getTheta() - 90) < 20) {
			currentAngleOff = odo.getTheta() - 90;
		}

		else if (Math.abs(odo.getTheta() - 180) < 20) {
			currentAngleOff = odo.getTheta() - 180;
		}

		else if (Math.abs(odo.getTheta() - 270) < 20) {
			currentAngleOff = odo.getTheta() - 270;
		}

		else if (odo.getTheta() < 20) {
			currentAngleOff = odo.getTheta();
		}

		else if (odo.getTheta() > 340) {
			currentAngleOff = odo.getTheta() - 360;
		}

		adjustment = Math.toDegrees(angleOff) - currentAngleOff;

		odo.setTheta(odo.getTheta() + adjustment);

	}

}
