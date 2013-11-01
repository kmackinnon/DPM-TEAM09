
public class MobileRobot {
	private static Odometer odo;
	double [] pos = new double [3];
	private double forwardSpeed, rotationSpeed;
	
	
	public void travelMag(double distance){
		double[] initPos = new double [3], currPos = new double [3];
		setRotationSpeed(0.0);
		odo.getPosition(initPos);
		odo.getPosition(currPos);
		if (distance < 0.0)
			setForwardSpeed(-HardwareInfo.FORWARD_SPEED);
		else
			setForwardSpeed(HardwareInfo.FORWARD_SPEED);
		while (Math.pow((currPos[0]-initPos[0]), 2) + Math.pow((currPos[1]-initPos[1]), 2) < distance * distance) {//pythagoros to determine how far left to go
			odo.getPosition(currPos);
		}
		setForwardSpeed(0.0);
	}
	public void travelCoordinate(double x, double y){
		odo.getPosition(pos);
		while (Math.sqrt(Math.pow((x-pos[0]), 2) + Math.pow((y-pos[1]), 2)) > 3){	
			odo.getPosition(pos);
	
				if(Math.abs(y - pos[1]) < 0.6){//if you only need to move horizontally
					if(x<pos[0])
					turnTo(-90);//position is to left
					else
						turnTo(90);
					}
				//position is to right
				else{
					if(y>pos[1])
					turnTo(Math.toDegrees(Math.atan((x-pos[0])/(y-pos[1]))));	
					else{
						if(x<pos[0])
						turnTo(-1*Math.toDegrees(Math.atan((y-pos[1])/(x-pos[0]))) - 90);//counter clockwise
						else
						turnTo(-1*Math.toDegrees(Math.atan((y-pos[1])/(x-pos[0]))) + 90);//clockwise
					}
				}

				
				setRotationSpeed(0.0);
				
				travelMag(Math.sqrt(Math.pow((x-pos[0]), 2) + Math.pow((y-pos[1]), 2)));//after orientation travel there
		}
		setSpeeds(0.0, 0.0);
		setForwardSpeed(0.0);
	}
	public void turnTo(double angle) {
		
		double [] currPos = new double [3];
		double currSpeed = 30;
		double angDiff;
		
		setForwardSpeed(0.0);
		
		odo.getPosition(currPos);
		
		angDiff = Odometer.minimumAngleFromTo(currPos[2], angle);//find minimum angle between current angle and where we need to go
		
		if (angDiff > 0.0)
			setRotationSpeed(currSpeed);//clockwise
		else
			setRotationSpeed(currSpeed *= -1);//counterclockwise
		
		
		while (Math.abs(angDiff) > 1) {//move to angle
			if (currSpeed > 0.0 && angDiff < 0.0)
				setRotationSpeed(currSpeed *= -0.5);
			else if (currSpeed < 0.0 && angDiff > 0.0)
				setRotationSpeed(currSpeed *= -0.5);
			
			odo.getPosition(currPos);
			angDiff = Odometer.minimumAngleFromTo(currPos[2], angle);}
	}
	
	public double getDisplacement() {
		return (HardwareInfo.leftMotor.getTachoCount() * HardwareInfo.leftRadius +
				HardwareInfo.rightMotor.getTachoCount() * HardwareInfo.rightRadius) *
				Math.PI / 360.0;
	}
	
	public double getHeading() {
		return (HardwareInfo.leftMotor.getTachoCount() * HardwareInfo.leftRadius -
				HardwareInfo.rightMotor.getTachoCount() * HardwareInfo.rightRadius) / HardwareInfo.width;
	}
	
	public void getDisplacementAndHeading(double [] data) {
		int leftTacho, rightTacho;
		leftTacho = HardwareInfo.leftMotor.getTachoCount();
		rightTacho = HardwareInfo.rightMotor.getTachoCount();
		
		data[0] = (leftTacho * HardwareInfo.leftRadius + rightTacho * HardwareInfo.rightRadius) *	Math.PI / 360.0;
		data[1] = (leftTacho * HardwareInfo.leftRadius - rightTacho * HardwareInfo.rightRadius) / HardwareInfo.width;
	}
	
	// mutators
	public void setForwardSpeed(double speed) {
		forwardSpeed = speed;
		rotationSpeed = 0;
		setSpeeds(forwardSpeed, rotationSpeed);
	}
	
	public void setRotationSpeed(double speed) {
		forwardSpeed = 0;
		rotationSpeed = speed;
		setSpeeds(forwardSpeed, rotationSpeed);
	}
	
	public void setSpeeds(double forwardSpeed, double rotationalSpeed) {//clockwise
		double leftSpeed, rightSpeed;

		this.forwardSpeed = forwardSpeed;
		this.rotationSpeed = rotationalSpeed; 

		leftSpeed = (forwardSpeed + rotationalSpeed * HardwareInfo.width * Math.PI / 360.0) *
				180.0 / (HardwareInfo.leftRadius * Math.PI);
		rightSpeed = (forwardSpeed - rotationalSpeed * HardwareInfo.width * Math.PI / 360.0) *
				180.0 / (HardwareInfo.rightRadius * Math.PI);

		// set motor directions
		if (leftSpeed > 0.0)
			HardwareInfo.leftMotor.forward();
		else {
			HardwareInfo.leftMotor.backward();
			leftSpeed = -leftSpeed;
		}
		
		if (rightSpeed > 0.0)
			HardwareInfo.rightMotor.forward();
		else {
			HardwareInfo.rightMotor.backward();
			rightSpeed = -rightSpeed;
		}
		
		// set motor speeds
		if (leftSpeed > 900.0)
			HardwareInfo.leftMotor.setSpeed(900);
		if(leftSpeed == 0.0)//!!!!!! If the speed is set to 0 then the Motors come to a complete stop and can't be restarted except from twoWheeledRobot, this means Navigation cant work
			HardwareInfo.leftMotor.setSpeed(1);//Setting speed to 1 makes it bascially stop but allows the motors to be restarted
		else
			HardwareInfo.leftMotor.setSpeed((int)leftSpeed);
		
		if (rightSpeed > 900.0)
			HardwareInfo.rightMotor.setSpeed(900);
		else
		if(rightSpeed == 0.0)
			HardwareInfo.rightMotor.setSpeed(1);
		else
			HardwareInfo.rightMotor.setSpeed((int)rightSpeed);
		
	}
	public void stopMotors() {
		HardwareInfo.rightMotor.stop();
		HardwareInfo.leftMotor.stop();
	}
	
	public void startMotors() {
		HardwareInfo.rightMotor.forward();
		HardwareInfo.leftMotor.forward();
	}
}
