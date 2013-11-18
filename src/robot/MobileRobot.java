package robot;

import java.util.ArrayList;

import lejos.nxt.LCD;

/**
 * MobileRobot contains all the methods needed for the robot to move to a
 * location, and to turn to an angle. This includes traveling to a point while
 * navigating around obstacles.
 * 
 * @author Simon Lee, Sidney Ng
 * 
 */

public class MobileRobot extends SensorMotorUser {
	public static Odometer odo = new Odometer();
	public static OdometeryCorrection corr = new OdometeryCorrection(odo);
	public static BlockDetector blockDetector = new BlockDetector();
	public static boolean isTurning = false;
	
	private double xPrevTarget;
	private double yPrevTarget;

	private final int ANGLE_ERROR_THRESHOLD = 2; // measured in degrees
	private final int POSITION_ERROR_THRESHOLD = 2;

	/**
	 * Default Constructor
	 * <p>
	 * Starts odometer of the MobileRobot
	 */
	public MobileRobot() {
	}

	public void travelTo(Intersection destination) {
		Intersection source = Map.getIntersection(odo.getX(), odo.getY());

		ArrayList<Intersection> listOfWayPoints = Dijkstra.algorithm(source,
				destination);

		for (Intersection intersection : listOfWayPoints) {
			travelCoordinate(intersection.getXInCm(), intersection.getYInCm());
		}

	}

	public void travelToNeighbor(Intersection destination) {
		travelCoordinate(destination.getXInCm(), destination.getYInCm());
	}

	public void travelToTargetZone() {
		Intersection source = Map.getIntersection(odo.getX(), odo.getY());

		ArrayList<Intersection> listOfWayPoints = Dijkstra
				.algorithmForTargetZone(source);

		for (Intersection intersection : listOfWayPoints) {
			travelCoordinate(intersection.getXInCm(), intersection.getYInCm());
		}

	}

	public void travelTileCoordinate(int x, int y) {

		double xInCm = x * Map.TILE_SIZE;
		double yInCm = y * Map.TILE_SIZE;

		travelCoordinate(xInCm, yInCm);

	}

	public void travelCoordinate(double xTarget, double yTarget) {

		double xDiff;
		double yDiff;
		double targetTheta;
		double deltaTheta;

		// keep looping while the difference between the current position and
		// target position is greater than the position error threshold
		while (!isAtPoint(xTarget, yTarget)) {
			// Determine whether to turn or not
			xDiff = xTarget - odo.getX();
			yDiff = yTarget - odo.getY();
			targetTheta = 90 - Math.toDegrees(Math.atan2(yDiff, xDiff));

			// change in theta is target minus current
			
			deltaTheta = targetTheta - odo.getTheta();
			
			if(deltaTheta>180){
				
				deltaTheta -= 360;
				
			}
			
			else if(deltaTheta<-180){
				
				deltaTheta +=360;
				
			}

			
			// if the heading is off by more than acceptable error, we must
			// correct
			if (Math.abs(deltaTheta) > ANGLE_ERROR_THRESHOLD) {
			
				if(isAtPoint(xPrevTarget,yPrevTarget)){
					turnToOnPoint(targetTheta);
				}
				else{
					turnToWhileMoving(targetTheta);
				}
				
			} else {

				
				moveForward();
			}
		}
		
		stopMoving();
		
		xPrevTarget = xTarget;
		yPrevTarget = yTarget;
		
	}

	
	public void initializePrevTarget(double x, double y){
		
		xPrevTarget = x;
		yPrevTarget = y;
		
	}
	
	
	public void turnToWhileMoving(double targetTheta) {
		double rotate = targetTheta - odo.getTheta();

		// turn while travelling by adjusting motor speeds
		if (rotate > 0) {
			
			turnRightWhileMoving();

		} else if (rotate < 0) {
			
			turnLeftWhileMoving();
		}
	}
	
	
	public void turnToOnPoint(double targetTheta){
		
		double angleToRotateBy = targetTheta - odo.getTheta();

		// turn a minimal angle
		if (angleToRotateBy > 180) {
			angleToRotateBy -= 360;
		} else if (angleToRotateBy < -180) {
			angleToRotateBy += 360;
		}

		rotateByAngle(angleToRotateBy);
			
	}
	

	
	
	
	public void moveForward() {

		leftMotor.setSpeed(FORWARD_SPEED);
		rightMotor.setSpeed(FORWARD_SPEED);

		leftMotor.forward();
		rightMotor.forward();

	}

	public void moveBackward() {

		leftMotor.setSpeed(FORWARD_SPEED);
		rightMotor.setSpeed(FORWARD_SPEED);

		leftMotor.backward();
		rightMotor.backward();

	}

	public void rotateClockwiseOnPoint() {

		leftMotor.setSpeed(ROTATION_SPEED);
		rightMotor.setSpeed(ROTATION_SPEED);

		leftMotor.forward();
		rightMotor.backward();

	}

	public void rotateCounterClockwiseOnPoint() {

		leftMotor.setSpeed(ROTATION_SPEED);
		rightMotor.setSpeed(ROTATION_SPEED);

		leftMotor.backward();
		rightMotor.forward();

	}
	
	
	public void rotateByAngle(double angle){
		
		leftMotor.setSpeed(ROTATION_SPEED);
		rightMotor.setSpeed(ROTATION_SPEED);

		leftMotor.rotate(convertAngle(LEFT_RADIUS, WIDTH, angle), true);
		rightMotor.rotate(-convertAngle(RIGHT_RADIUS, WIDTH, angle), false);
		
	}
	
	
	
	public void turnLeftWhileMoving(){
		
		leftMotor.setSpeed(TURNING_SPEED);
		rightMotor.setSpeed(FORWARD_SPEED);
		
		leftMotor.forward();
		rightMotor.forward();
		
	}
	
	public void turnRightWhileMoving(){
		
		leftMotor.setSpeed(FORWARD_SPEED);
		rightMotor.setSpeed(TURNING_SPEED);
		
		leftMotor.forward();
		rightMotor.forward();
		
	}
	
	
	

	public void stopMoving() {

		leftMotor.setSpeed(0);
		rightMotor.setSpeed(0);

	}

	public void liftClaw() {
		clawMotor.setSpeed(120);
		clawMotor.rotateTo(310);
	}

	private boolean isAtPoint(double xTarget, double yTarget) {

		if (Math.abs(xTarget - odo.getX()) < POSITION_ERROR_THRESHOLD
				&& Math.abs(yTarget - odo.getY()) < POSITION_ERROR_THRESHOLD) {
			return true;
		}

		else {
			return false;
		}

	}
	
	
	// returns the number of degrees the wheels must turn over a distance
    private static int convertDistance(double radius, double distance) {
            return (int) ((180.0 * distance) / (Math.PI * radius));
    }

    // returns the number of degrees to turn a certain angle
    private static int convertAngle(double radius, double width, double angle) {
            return convertDistance(radius, Math.PI * width * angle / 360.0);
    }

}
