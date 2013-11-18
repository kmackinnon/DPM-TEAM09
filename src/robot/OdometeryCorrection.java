package robot;

import lejos.nxt.ColorSensor;
import lejos.nxt.Sound;
import lejos.nxt.comm.RConsole;

public class OdometeryCorrection extends Thread {

	private Odometer odo;

	public OdometeryCorrection(Odometer odo) {
		this.odo = odo;
		SensorMotorUser.leftCS.setFloodlight(true);
		SensorMotorUser.rightCS.setFloodlight(true);

		// correctionTimer = new Timer(DEFAULT_PERIOD,this);

	}

	public void run() {
	
		while (true) {
			if(!MobileRobot.isTurning){
				
				//Sound.beep();
	
				double[] initPos = new double[3];
				double[] destination = new double[3];
	
				while (lineDetected(SensorMotorUser.leftCS)
						|| lineDetected(SensorMotorUser.rightCS))
					;
	
				odo.getPosition(initPos);
	
				if (lineDetected(SensorMotorUser.leftCS)) {
					double prevRightTacho = SensorMotorUser.rightMotor
							.getTachoCount();
	
					while (lineDetected(SensorMotorUser.rightCS))
						;
	
					odo.getPosition(destination);
	
					double lastRightTacho = SensorMotorUser.rightMotor
							.getTachoCount();
					double length = 2 * Math.PI * SensorMotorUser.RIGHT_RADIUS
							* ((lastRightTacho - prevRightTacho) / 360);
	
					double angleOff = Math.atan(length/ SensorMotorUser.SENSOR_WIDTH);
	
					odo.setX(initPos[0] + (length) * Math.sin(angleOff));
					odo.setY(initPos[1] + (length) * Math.cos(angleOff));
					odo.setTheta(odo.getTheta() + Math.toDegrees(angleOff));
					while( Math.abs(odo.getTheta() - initPos[2]) >1 ){
						SensorMotorUser.rightMotor.setSpeed(SensorMotorUser.rightMotor.getSpeed()+1);
					}
					SensorMotorUser.rightMotor.setSpeed(SensorMotorUser.rightMotor.getSpeed()-1);
					RConsole.println("x : "  + odo.getX() + "y : " + odo.getY() + "theta = " + odo.getTheta() );
					try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
					}
				} else {
					double prevLeftTacho = SensorMotorUser.leftMotor
							.getTachoCount();
	
					while (lineDetected(SensorMotorUser.leftCS))
						;
	
					odo.getPosition(destination);
	
					double lastLeftTacho = SensorMotorUser.leftMotor
							.getTachoCount();
					double length = 2 * Math.PI * SensorMotorUser.LEFT_RADIUS
							* ((lastLeftTacho - prevLeftTacho) / 360);
	
					double angleOff = Math.atan(length/ SensorMotorUser.SENSOR_WIDTH);
					odo.setX(initPos[0] + (length) * Math.sin(angleOff));
					odo.setY(initPos[1] + (length) * Math.cos(angleOff));
					odo.setTheta(odo.getTheta() - Math.toDegrees(angleOff));
					while( Math.abs(odo.getTheta() - initPos[2]) >1 ){
						SensorMotorUser.leftMotor.setSpeed(SensorMotorUser.leftMotor.getSpeed()+1);
					}
					SensorMotorUser.leftMotor.setSpeed(SensorMotorUser.leftMotor.getSpeed()-1);
					RConsole.println("x : "  + odo.getX() + "y : " + odo.getY() + "theta = " + odo.getTheta() );
					try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
					}
				}
			}
		}
	}

	private int prevValueL = 0;
	private int prevValueR = 0;
	private boolean negativeDiffL = false;
	private boolean negativeDiffR = false;
	private static final int LINE_DIFF = 7;

	public boolean lineDetected(ColorSensor cs) {
		
		boolean left = (cs == SensorMotorUser.leftCS);

		int value = cs.getRawLightValue();
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
				Sound.beep();
				negativeDiffL = false;
				return true;
			} else if (negativeDiffR && !left) {
				// RConsole.println("Rdetected");
				Sound.beep();
				negativeDiffR = false;
				return true;
			}
		}

		return false;
	}

}
