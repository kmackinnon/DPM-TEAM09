package robot;

import lejos.nxt.comm.RConsole;

public class OdometeryCorrection extends Thread{
	private Odometer odo;
	private Object lock = new Object();
	public OdometeryCorrection(Odometer odo){
		this.odo = odo;
	}
	/**
	 * This method helps the odometer get the correct position of the robot.
	 */
	public void run() {
		double[] initPos = new double[3];
		double[] destination = new double[3];
		
		while(true){
			if(MobileRobot.isTurning!=true){
				while (SensorMotorUser.lineDetected(SensorMotorUser.leftCS,true) || SensorMotorUser.lineDetected(SensorMotorUser.rightCS,false));
					
				odo.getPosition(initPos);
					
				if (SensorMotorUser.lineDetected(SensorMotorUser.leftCS,true)) {
					double prevRightTacho = SensorMotorUser.rightMotor.getTachoCount();
						
					while (SensorMotorUser.lineDetected(SensorMotorUser.rightCS,false));
						
					odo.getPosition(destination);
						
					double lastRightTacho = SensorMotorUser.rightMotor.getTachoCount();
					double length = 2 * Math.PI * SensorMotorUser.rightRadius
							* ((lastRightTacho - prevRightTacho) / 360);
						
					double angleOff = + Math.atan(length / SensorMotorUser.sensorWidth);
						
					synchronized (lock) {
						odo.x = initPos[0] + (length)* Math.sin(angleOff);
						odo.y = initPos[1] + (length)* Math.cos(angleOff);
						odo.theta = odo.theta + Math.toDegrees(angleOff);
						RConsole.println("x: " + odo.x + "y: " + odo.y + " theta: " + odo.theta);
						}
					} 
				else {
					double prevLeftTacho = SensorMotorUser.leftMotor.getTachoCount();
						
					while (SensorMotorUser.lineDetected(SensorMotorUser.leftCS,true));
					
					odo.getPosition(destination);
					
					double lastLeftTacho = SensorMotorUser.leftMotor.getTachoCount();
					double length = 2 * Math.PI * SensorMotorUser.leftRadius
						* ((lastLeftTacho - prevLeftTacho) / 360);
						
					double angleOff = -Math.atan(length / SensorMotorUser.sensorWidth);
					synchronized (lock) {
						odo.theta = odo.theta + Math.toDegrees(angleOff);
						odo.x = initPos[0] + (length)* Math.sin(angleOff);
						odo.y = initPos[1] + (length)* Math.cos(angleOff);
						RConsole.println("x: " + odo.x + "y: " + odo.y + " theta: " + odo.theta);
					}
				}
			}
		}
	}
}
