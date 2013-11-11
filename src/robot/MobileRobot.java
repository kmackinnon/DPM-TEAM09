package robot;

import java.util.ArrayList;


/**MobileRobot contains all the methods needed for the robot to move to a location, and to turn to an angle. 
 * This includes traveling to a point while navigating around obstacles.  
 * 
 * @author Simon Lee, Sidney Ng
 *
 */

public class MobileRobot extends SensorMotorUser{
	public static Odometer odo;
	public static BlockDetector blockDetector;
	
	double [] pos = new double [3];
	private double forwardSpeed, rotationSpeed;
	
	/**
	 * Default Constructor
	 * <p>
	 * Starts odometer of the MobileRobot
	 */
	public MobileRobot() {
		odo = new Odometer(true);
	}
	
	
	public void travelTo(Intersection destination){
		
		Intersection source = Intersection.convertToIntersection(odo.getX(), odo.getY());
		
		ArrayList<Intersection> listOfWayPoints = Dijkstra.algorithm(source,destination);
		
		for(Intersection intersection : listOfWayPoints){
			
			travelCoordinate(intersection.getXInCm(), intersection.getYInCm());
			
		}
			
	}
	
	
	public void travelToNeighbor(Intersection destination){
		
		travelCoordinate(destination.getXInCm(), destination.getYInCm());
		
	}
	
	public void travelToTargetZone(){
		
		Intersection source = Intersection.convertToIntersection(odo.getX(), odo.getY());
		
		ArrayList<Intersection> listOfWayPoints = Dijkstra.algorithmForTargetZone(source);
		
		for(Intersection intersection : listOfWayPoints){
			
			travelCoordinate(intersection.getXInCm(), intersection.getYInCm());
			
		}
		
	}
	
	
	
	
	/**
	 * Robot travels a certain distance
	 * 
	 * @param distance distance to travel
	 */
	public void travelMag(double distance){
		double[] initPos = new double [3], currPos = new double [3];
		setRotationSpeed(0.0);
		odo.getPosition(initPos);
		odo.getPosition(currPos);
		if (distance < 0.0)
			setForwardSpeed(-FORWARD_SPEED);
		else
			setForwardSpeed(FORWARD_SPEED);
		while (Math.pow((currPos[0]-initPos[0]), 2) + Math.pow((currPos[1]-initPos[1]), 2) < distance * distance) {//pythagoros to determine how far left to go
			odo.getPosition(currPos);
		}
		setForwardSpeed(0.0);
	}
	
	/**
	 * Move robot to position at (x,y)
	 * <p>
	 * Will orient robot to face coordinate before moving in a straight line
	 * 
	 * @param x x coordinate to move to
	 * @param y y coordinate to move to
	 */
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
	}
	
	/**
	 * Turns robot by specified value.
	 * <p>
	 * Negative angle values turn robot counterclockwise. Positive angle values turn robot clockwise.
	 * 
	 * @param angle angle to turn
	 */
	public void turnTo(double angle) {
		
		double [] currPos = new double [3];
		double currSpeed = ROTATION_SPEED;
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
			angDiff = Odometer.minimumAngleFromTo(currPos[2], angle);
		}
		setSpeeds(0.0, 0.0);
	}
	
	// mutators
	/**
	 * Sets linear speed. Calls setSpeeds(double forwardSpeed, double rotationalSpeed) with rotationalSpeed equal to 0
	 * @param speed
	 */
	public void setForwardSpeed(double speed) {
		forwardSpeed = speed;
		rotationSpeed = 0;
		setSpeeds(forwardSpeed, rotationSpeed);
	}
	
	/**
	 * Sets rotational speed. Calls setSpeeds(double forwardSpeed, double rotationalSpeed) with forwardSpeed equal to 0
	 * 
	 * @param speed rotational speed to set motors
	 */
	public void setRotationSpeed(double speed) {
		forwardSpeed = 0;
		rotationSpeed = speed;
		setSpeeds(forwardSpeed, rotationSpeed);
	}
	
	/**
	 * Sets forward and rotational speed.
	 * <p>
	 * Positive rotational speed designated in clockwise direction. Allows for traveling in an arc
	 * <p>
	 * For speeds of zero, the speed is actually set to 1. Setting motor speed to 0 does not allow for further increases to speed.
	 * 
	 * @param forwardSpeed the forward speed to set the motors
	 * @param rotationalSpeed the rotational speed to set the motors
	 */
	public void setSpeeds(double forwardSpeed, double rotationalSpeed) {//clockwise
		double leftSpeed, rightSpeed;

		this.forwardSpeed = forwardSpeed;
		this.rotationSpeed = rotationalSpeed; 

		leftSpeed = (forwardSpeed + rotationalSpeed * width * Math.PI / 360.0) *
				180.0 / (leftRadius * Math.PI);
		rightSpeed = (forwardSpeed - rotationalSpeed * width * Math.PI / 360.0) *
				180.0 / (rightRadius * Math.PI);

		// set motor directions
		if (leftSpeed > 0.0)
			leftMotor.forward();
		else {
			leftMotor.backward();
			leftSpeed = -leftSpeed;
		}
		
		if (rightSpeed > 0.0)
			rightMotor.forward();
		else {
			rightMotor.backward();
			rightSpeed = -rightSpeed;
		}
		
		// set motor speeds
		if (leftSpeed > 900.0)
			leftMotor.setSpeed(900);
		if(leftSpeed == 0.0)
			// If the speed is set to 0 then the Motors come to a complete stop and can't be restarted
			// Setting speed to 1 virtually stops motor movement, but allows for further changes in motor speed
			leftMotor.setSpeed(1);
		else
			leftMotor.setSpeed((int)leftSpeed);
		
		if (rightSpeed > 900.0)
			rightMotor.setSpeed(900);
		else
		if(rightSpeed == 0.0)
			rightMotor.setSpeed(1);
		else
			rightMotor.setSpeed((int)rightSpeed);
		
	}
	
	/**
	 * Stops both motors. Calls NXTRegulatedMotor.stop()
	 */
	public void stopMotors() {
		rightMotor.stop();
		leftMotor.stop();
	}

	/**
	 * Start both motors. Calls NXTRegulatedMotor.forward()
	 */
	public void startMotors() {
		rightMotor.forward();
		leftMotor.forward();
	}
}
