package robot;

import lejos.nxt.comm.RConsole;

public class OdometeryCorrection extends Thread{
	private Odometer odo;
	private Object lock = new Object();
	private boolean leftSensor = false;
	private boolean rightSensor = false;
	
	private double prevRightTacho;
	private double prevLeftTacho;
	private double angleOff;
	private double length;
	
	public OdometeryCorrection(Odometer odo){
		this.odo = odo;
		SensorMotorUser.leftCS.setFloodlight(true);
		SensorMotorUser.rightCS.setFloodlight(true);
	}
	/**
	 * This method helps the odometer get the correct position of the robot.
	 */
	public void run() {
		double[] initPos = new double[3];
		double[] destination = new double[3];
		
		while(true) {
			if(MobileRobot.isTurning!=true) {
				if (!leftSensor) { // left sensor has not detected since last line
					if (!rightSensor) {
						if (SensorMotorUser.lineDetected(SensorMotorUser.leftCS,true)) {
							// if left has detected, then this is a new line; take position and tacho count
							odo.getPosition(initPos);
							prevRightTacho = SensorMotorUser.rightMotor.getTachoCount();
							leftSensor = true;
						}
					} else if (SensorMotorUser.lineDetected(SensorMotorUser.leftCS,true)) {
						// this is an old line, take the position and tacho count so we can calc the dist traveled
						odo.getPosition(destination);

						double lastLeftTacho = SensorMotorUser.leftMotor.getTachoCount();
						length = 2 * Math.PI * SensorMotorUser.leftRadius
								* ((lastLeftTacho - prevLeftTacho) / 360);
							
						angleOff = -Math.atan(length / SensorMotorUser.sensorWidth);
						rightSensor = true;
					}
				}
				if (!rightSensor) { // right sensor has not detected since last line
					if (!leftSensor) {
						if (SensorMotorUser.lineDetected(SensorMotorUser.rightCS,false)) {
							// if right has detected, then this is a new line; take position and tacho count
							odo.getPosition(initPos);
							prevLeftTacho = SensorMotorUser.rightMotor.getTachoCount();
							rightSensor = true;
						}
					} else if (SensorMotorUser.lineDetected(SensorMotorUser.rightCS,false)) {
						// this is an old line, take the position and tacho count so we can calc the dist traveled
						odo.getPosition(destination);
						
						double lastRightTacho = SensorMotorUser.rightMotor.getTachoCount();
						length = 2 * Math.PI * SensorMotorUser.rightRadius
								* ((lastRightTacho - prevRightTacho) / 360);
						
						angleOff = + Math.atan(length / SensorMotorUser.sensorWidth);
						leftSensor = true;
					}
				}
				
				if (rightSensor && leftSensor) {
					synchronized (lock) {
						odo.theta = odo.theta + Math.toDegrees(angleOff);
						odo.x = initPos[0] + (length)* Math.sin(angleOff);
						odo.y = initPos[1] + (length)* Math.cos(angleOff);
						RConsole.println("x: " + odo.x + "y: " + odo.y + " theta: " + odo.theta);
					}
					rightSensor = false;
					leftSensor = false;
				}
				
				
//				while (SensorMotorUser.lineDetected(SensorMotorUser.leftCS,true) || SensorMotorUser.lineDetected(SensorMotorUser.rightCS,false));
//					
//				odo.getPosition(initPos);
//					
//				if (SensorMotorUser.lineDetected(SensorMotorUser.leftCS,true)) {
//					double prevRightTacho = SensorMotorUser.rightMotor.getTachoCount();
//						
//					while (SensorMotorUser.lineDetected(SensorMotorUser.rightCS,false));
//						
//					odo.getPosition(destination);
//						
//					double lastRightTacho = SensorMotorUser.rightMotor.getTachoCount();
//					double length = 2 * Math.PI * SensorMotorUser.rightRadius
//							* ((lastRightTacho - prevRightTacho) / 360);
//						
//					double angleOff = + Math.atan(length / SensorMotorUser.sensorWidth);
//						
//					synchronized (lock) {
//						odo.x = initPos[0] + (length)* Math.sin(angleOff);
//						odo.y = initPos[1] + (length)* Math.cos(angleOff);
//						odo.theta = odo.theta + Math.toDegrees(angleOff);
//						RConsole.println("x: " + odo.x + "y: " + odo.y + " theta: " + odo.theta);
//						}
//					} 
//				else {
//					double prevLeftTacho = SensorMotorUser.leftMotor.getTachoCount();
//						
//					while (SensorMotorUser.lineDetected(SensorMotorUser.leftCS,true));
//					
//					odo.getPosition(destination);
//					
//					double lastLeftTacho = SensorMotorUser.leftMotor.getTachoCount();
//					double length = 2 * Math.PI * SensorMotorUser.leftRadius
//						* ((lastLeftTacho - prevLeftTacho) / 360);
//						
//					double angleOff = -Math.atan(length / SensorMotorUser.sensorWidth);
//					synchronized (lock) {
//						odo.theta = odo.theta + Math.toDegrees(angleOff);
//						odo.x = initPos[0] + (length)* Math.sin(angleOff);
//						odo.y = initPos[1] + (length)* Math.cos(angleOff);
//						RConsole.println("x: " + odo.x + "y: " + odo.y + " theta: " + odo.theta);
//					}
//				}
			}
		}
	}
}
