package robot;

import lejos.nxt.ColorSensor;
import lejos.nxt.LCD;
import lejos.nxt.Sound;
import lejos.util.Timer;
import lejos.util.TimerListener;

public class OdometeryCorrection extends SensorMotorUser implements TimerListener{
	
	private static final int DEFAULT_PERIOD = 25;
	
	private Timer correctionTimer;
	
	private Odometer odo;
	
	private boolean leftSensorDetected;
	private boolean rightSensorDetected;

	private double prevRightTacho;
	private double prevLeftTacho;

	private double xAtFirstDetection;
	private double yAtFirstDetection;
	private double thetaAtFirstDetection;
	
	private boolean doCorrection = false;

	
	public OdometeryCorrection(Odometer odo){
		this.odo = odo;
		
		correctionTimer = new Timer(DEFAULT_PERIOD,this);
		
	}

	public void turnOnCorrection() {
		
		leftCS.setFloodlight(true);
		rightCS.setFloodlight(true);

		leftSensorDetected = false;
		rightSensorDetected = false;
		
		correctionTimer.start();

		doCorrection = true;
	}

	public void turnOffCorrection() {
		
		leftCS.setFloodlight(false);
		rightCS.setFloodlight(false);

		doCorrection = false;
		
		correctionTimer.stop();

	}
	
	
	
	public void timedOut(){
		
		double distanceTravelledByLaggingWheel = 0;
		double angleOff = 0;
		

		if ((!leftSensorDetected) && (!rightSensorDetected)) {
			
			if (lineDetected(leftCS)) {
				
				// if left has detected, then this is a new line; take position
				// and tacho count
				//odo.getPosition(positionAtFirstDetection);
				
				xAtFirstDetection = odo.getX();
				yAtFirstDetection = odo.getY();
				thetaAtFirstDetection = odo.getTheta();
				
				prevRightTacho = rightMotor.getTachoCount();
				
				leftSensorDetected = true;
			}

			if (lineDetected(rightCS)) {

				// if right has detected, then this is a new line; take position
				// and tacho count
				//odo.getPosition(positionAtFirstDetection);
				
				xAtFirstDetection = odo.getX();
				yAtFirstDetection = odo.getY();
				thetaAtFirstDetection = odo.getTheta();
				
				prevLeftTacho = leftMotor.getTachoCount();
				
				rightSensorDetected = true;
				
			}

		}
		

		if (leftSensorDetected && (!rightSensorDetected)) {
			
			if (lineDetected(rightCS)) {
				
				double currentRightTacho = rightMotor.getTachoCount();

				distanceTravelledByLaggingWheel = 2 * Math.PI * RIGHT_RADIUS
						* ((currentRightTacho - prevRightTacho) / 360);

				angleOff = Math.atan(distanceTravelledByLaggingWheel
						/ SENSOR_WIDTH);
				rightSensorDetected = true;

			}

		}

		if ((!leftSensorDetected) && (rightSensorDetected)) {

			if (lineDetected(leftCS)) {			

				double currentLeftTacho = leftMotor.getTachoCount();

				distanceTravelledByLaggingWheel = 2 * Math.PI * LEFT_RADIUS
						* ((currentLeftTacho - prevLeftTacho) / 360);
				
				angleOff = -Math.atan(distanceTravelledByLaggingWheel
						/ SENSOR_WIDTH);

				leftSensorDetected = true;

			}

		}

		if (leftSensorDetected && rightSensorDetected) {
			
			if (distanceTravelledByLaggingWheel != 0) {

				odo.setX(xAtFirstDetection
						+ (distanceTravelledByLaggingWheel)
						* Math.sin(angleOff));
				odo.setY(yAtFirstDetection
						+ (distanceTravelledByLaggingWheel)
						* Math.cos(angleOff));
				odo.setTheta(odo.getTheta() + Math.toDegrees(angleOff));

			}

			rightSensorDetected = false;
			leftSensorDetected = false;

		}
		
		
	}
	
	
	
	private int prevValueL = 0;
	private int prevValueR = 0;
	private boolean negativeDiffL = false;
	private boolean negativeDiffR = false;
	private static final int LINE_DIFF = 20;
	
	private boolean lineDetected(ColorSensor cs) {
		
		boolean left = (cs==leftCS);
		
		int value = cs.getRawLightValue();
		
		//LCD.clear();
		//LCD.drawInt((int)value, 0, 7);
		
		int diff = (left) ? (value - prevValueL) : (value - prevValueR);
		
//		RConsole.println("Diff: " + diff);
		if(diff<-LINE_DIFF){
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
		
		if(diff>LINE_DIFF){
			if (negativeDiffL && left) {
//				RConsole.println("Ldetected");
				Sound.beep();
				negativeDiffL = false;
				return true;
			} else if (negativeDiffR && !left) {
//				RConsole.println("Rdetected");
				Sound.beep();
				negativeDiffR = false;
				return true;
			}
		}
		
		return false;
	}
	
	
}
