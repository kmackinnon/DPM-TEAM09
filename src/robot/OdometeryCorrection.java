package robot;

import lejos.robotics.Color;

public class OdometeryCorrection extends Thread{
	private Odometer odo;
	
	public OdometeryCorrection(Odometer odo){
		this.odo = odo;
	}
	public void correction() {
		double[] initPos = new double[3];
		double[] destination = new double[3];

		while ((odo.getFilteredData(odo.leftCS) == Color.BLACK)
				|| (odo.getFilteredData(odo.rightCS) == Color.BLACK));
		
		odo.getPosition(initPos);
		
		if (odo.getFilteredData(odo.leftCS) == Color.BLACK) {
			double prevRightTacho = odo.rightMotor.getTachoCount();
			
			while (odo.getFilteredData(odo.rightCS) == Color.BLACK);
			
			odo.getPosition(destination);
			
			double lastRightTacho = odo.rightMotor.getTachoCount();
			double length = 2 * Math.PI * odo.rightRadius
					* ((lastRightTacho - prevRightTacho) / 360);
			
			double angleOff = Math.atan(length / odo.sensorWidth);
			
			synchronized (odo.lock) {
				odo.theta = odo.theta - angleOff;
				odo.x = initPos[0] - (initPos[0] - destination[0])
						* Math.sin(angleOff);
				odo.y = initPos[1] - (initPos[1] - destination[1])
						* Math.cos(angleOff);
			}
			
		} else {
			double prevLeftTacho = odo.leftMotor.getTachoCount();
			
			while (odo.getFilteredData(odo.leftCS) == Color.BLACK);
			odo.getPosition(destination);
			
			double lastLeftTacho = odo.leftMotor.getTachoCount();
			double length = 2 * Math.PI * odo.leftRadius
					* ((lastLeftTacho - prevLeftTacho) / 360);
			
			double angleOff = Math.atan(length / odo.sensorWidth);
			synchronized (odo.lock) {
				odo.theta = odo.theta + angleOff;
				odo.x = initPos[0] + (initPos[0] - destination[0])
						* Math.sin(angleOff);
				odo.y = initPos[1] + (initPos[1] - destination[1])
						* Math.cos(angleOff);
			}
		}
	}
}
