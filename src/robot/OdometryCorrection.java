package robot;

import lejos.nxt.ColorSensor;
import lejos.nxt.LCD;
import lejos.nxt.Sound;
import lejos.util.Timer;
import lejos.util.TimerListener;

public class OdometryCorrection extends SensorMotorUser implements TimerListener{
	
	private static final int DEFAULT_PERIOD = 25;
	
	private Timer correctionTimer;
	
	private Odometer odo;
	
	private boolean leftSensorDetected;
	private boolean rightSensorDetected;

	private double prevRightTacho;
	private double prevLeftTacho;

	private double xAtFirstDetection;
	private double yAtFirstDetection;
	
	private boolean doCorrection = false;

	
	public OdometryCorrection(Odometer odo){
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
		

		if(!MobileRobot.isTurning){
		
			if ((!leftSensorDetected) && (!rightSensorDetected)) {
				
				if (lineDetected(leftCS)) {
					
					// if left has detected, then this is a new line; take position
					// and tacho count
					//odo.getPosition(positionAtFirstDetection);
					
					xAtFirstDetection = odo.getX();
					yAtFirstDetection = odo.getY();
					
					prevRightTacho = rightMotor.getTachoCount();
					
					leftSensorDetected = true;
				}
	
				if (lineDetected(rightCS)) {
	
					// if right has detected, then this is a new line; take position
					// and tacho count
					//odo.getPosition(positionAtFirstDetection);
					
					xAtFirstDetection = odo.getX();
					yAtFirstDetection = odo.getY();
					
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
	
					double currentAngleOff = 0;
					double adjustment;
						
					if(Math.abs(odo.getTheta() - 90) < 20){
						currentAngleOff = odo.getTheta() - 90;
					}
					
					else if(Math.abs(odo.getTheta() - 180) < 20){
						currentAngleOff = odo.getTheta() - 180;
					}
					
					else if(Math.abs(odo.getTheta() - 270) < 20){
						currentAngleOff = odo.getTheta() - 270;
					}
					
					else if(odo.getTheta() < 20){
						currentAngleOff = odo.getTheta();
					}
					
					else if(odo.getTheta()>340){
						currentAngleOff = odo.getTheta() - 360;
					}
					
					adjustment = Math.toDegrees(angleOff) - currentAngleOff;
					
					
					
					odo.setTheta(odo.getTheta() + adjustment);
					
				}
	
				rightSensorDetected = false;
				leftSensorDetected = false;
	
			}
			
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
