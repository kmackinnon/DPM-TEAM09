package robot;

import java.util.ArrayList;

/**
 * MobileRobot contains all the methods needed for the robot to move to a
 * location, and to turn to an angle. This includes traveling to a point while
 * navigating around obstacles.
 * 
 * @author Kevin Musgrave
 * 
 */

public class MobileRobot extends SensorMotorUser {
	public static Odometer odo = new Odometer();
	public static OdometryCorrection corr = new OdometryCorrection(odo);
	public static BlockDetector blockDetector = new BlockDetector();

	private static double xPrevTarget;
	private static double yPrevTarget;
	
	private final int ANGLE_ERROR_THRESHOLD = 1; // measured in degrees
	private final int POSITION_ERROR_THRESHOLD = 1;
	private final int TURN_ON_POINT_ANGLE_THRESHOLD = 20;
	private final int POINT_IS_BEHIND_ANGLE_THRESHOLD = 45;

	/**
	 * Default Constructor
	 * <p>
	 * Starts odometer of the MobileRobot
	 */
	public MobileRobot() {
	}

	public void travelTo(Intersection destination) {
		boolean isSuccess = false;
		Intersection source;
		ArrayList<Intersection> listOfWayPoints;

		while (!isSuccess) {

			source = Map.getIntersection(odo.getX(), odo.getY());

			if (destination.getX() == INT_SPECIAL_FLAG
					&& destination.getY() == INT_SPECIAL_FLAG) {
				listOfWayPoints = Dijkstra.algorithmForTargetZone(source);
			}

			else {

				if (destination.getAdjacencyList().isEmpty()) {
					return;
				}

				listOfWayPoints = Dijkstra.algorithm(source, destination);
			}

			isSuccess = travelToWaypoints(listOfWayPoints);

			if (!isSuccess) {

				if (!blockDetector.isObjectStyrofoam()) {
					moveBackToPreviousIntersection();

					Intersection prevIntersection = Map.getIntersection(
							xPrevTarget, yPrevTarget);

					int indexOfNextIntersection = listOfWayPoints
							.indexOf(prevIntersection) + 1;

					Intersection nextIntersection = listOfWayPoints
							.get(indexOfNextIntersection);

					Map.removeEdge(prevIntersection, nextIntersection);
				}

				else {

					if (pickUpStyrofoamBlock()) {
						return;
					}

				}

			}
		}

	}

	public void travelToTargetZone() {

		travelTo(new Intersection(INT_SPECIAL_FLAG, INT_SPECIAL_FLAG));

	}

	public void travelTileCoordinate(int x, int y) {

		double xInCm = x * Map.TILE_SIZE;
		double yInCm = y * Map.TILE_SIZE;

		travelCoordinate(xInCm, yInCm, true);

	}

	public boolean travelCoordinate(double xTarget, double yTarget,
			boolean stopAtTarget) {

		// this happens when the robot sees an obstacle and should back up to
		// previous point. if the previous point ends up being in front of the
		// robot, then it will move forward.
		if (xTarget == xPrevTarget && yTarget == yPrevTarget) {
			travelCoordinateBackwards(xTarget, yTarget, true);
		}

		double xDiff;
		double yDiff;
		double targetTheta;
		double deltaTheta;

		// keep looping while the difference between the current position and
		// target position is greater than the position error threshold
		while (!isAtPoint(xTarget, yTarget)) {

			if (blockDetector.isObjectInFront()&&!isAtPoint(xPrevTarget,yPrevTarget)) {
				return false;
			}

			// Determine whether to turn or not
			xDiff = xTarget - odo.getX();
			yDiff = yTarget - odo.getY();
			targetTheta = odo.fixDegAngle(90 - Math.toDegrees(Math.atan2(yDiff,
					xDiff)));

			// RConsole.println("targetTheta" + targetTheta);

			// change in theta is target minus current

			deltaTheta = targetTheta - odo.getTheta();

			deltaTheta = getMinAngle(deltaTheta);

			howToMoveDecider(xTarget,yTarget,deltaTheta);
		}

		if (stopAtTarget) {
			stopMoving();
		}

		xPrevTarget = xTarget;
		yPrevTarget = yTarget;

		return true;

	}

	public void travelCoordinateBackwards(double xTarget, double yTarget,
			boolean stopAtTarget) {

		double xDiff;
		double yDiff;
		double targetTheta;
		double deltaTheta;
		double backwardDeltaTheta;

		while (!isAtPoint(xTarget, yTarget)) {

			// Determine whether to turn or not
			xDiff = xTarget - odo.getX();
			yDiff = yTarget - odo.getY();
			targetTheta = odo.fixDegAngle(90 - Math.toDegrees(Math.atan2(yDiff,
					xDiff)));

			// RConsole.println("targetTheta" + targetTheta);

			// change in theta is target minus current

			deltaTheta = targetTheta - odo.getTheta();

			deltaTheta = getMinAngle(deltaTheta);
			
			backwardDeltaTheta = getMinAngle(deltaTheta - 180);

			// is the point behind the robot
			if (Math.abs(backwardDeltaTheta) < POINT_IS_BEHIND_ANGLE_THRESHOLD) {

				if(backwardDeltaTheta > ANGLE_ERROR_THRESHOLD){
				
					if (Math.abs(backwardDeltaTheta) > TURN_ON_POINT_ANGLE_THRESHOLD) {
						
						onPointTurnBy(backwardDeltaTheta);
					}
					
					else{
						whileMovingBackwardTurnBy(backwardDeltaTheta);
					}
				}
				
				else{
					moveBackward();
				}
			}

			else{
				howToMoveDecider(xTarget,yTarget,deltaTheta);
			}
		}

	}

	public void travelMagnitude(double magnitudeInCm) {

		int leftAmount = convertDistance(LEFT_RADIUS, magnitudeInCm);

		int rightAmount = convertDistance(RIGHT_RADIUS, magnitudeInCm);

		leftMotor.setSpeed(FORWARD_SPEED);
		rightMotor.setSpeed(FORWARD_SPEED);

		leftMotor.rotate(leftAmount, true);
		rightMotor.rotate(rightAmount, false);
	}

	public void initializePrevTarget(double x, double y) {
		xPrevTarget = x;
		yPrevTarget = y;
	}

	public double getPrevX() {
		return xPrevTarget;
	}

	public double getPrevY() {
		return yPrevTarget;
	}

	public void turnToWhileMoving(double targetTheta) {
		double angleToRotateBy = targetTheta - odo.getTheta();

		// turn a minimal angle
		angleToRotateBy = getMinAngle(angleToRotateBy);

		// turn while travelling by adjusting motor speeds
		whileMovingTurnBy(angleToRotateBy);

	}

	public void turnToOnPoint(double targetTheta) {

		double angleToRotateBy = targetTheta - odo.getTheta();

		// turn a minimal angle
		angleToRotateBy = getMinAngle(angleToRotateBy);

		onPointTurnBy(angleToRotateBy);
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

	public void rotateByAngle(double angle) {

		leftMotor.setSpeed(ROTATION_SPEED);
		rightMotor.setSpeed(ROTATION_SPEED);

		leftMotor.rotate(convertAngle(LEFT_RADIUS, WIDTH, angle), true);
		rightMotor.rotate(-convertAngle(RIGHT_RADIUS, WIDTH, angle), false);

	}

	public void turnLeftWhileMoving() {

		leftMotor.setSpeed(TURNING_SPEED);
		rightMotor.setSpeed(FORWARD_SPEED);

		leftMotor.forward();
		rightMotor.forward();

	}

	public void turnRightWhileMoving() {

		leftMotor.setSpeed(FORWARD_SPEED);
		rightMotor.setSpeed(TURNING_SPEED);

		leftMotor.forward();
		rightMotor.forward();

	}
	
	
	public void turnLeftWhileMovingBackward() {

		leftMotor.setSpeed(FORWARD_SPEED);
		rightMotor.setSpeed(TURNING_SPEED);

		leftMotor.backward();
		rightMotor.backward();

	}

	public void turnRightWhileMovingBackward() {

		leftMotor.setSpeed(TURNING_SPEED);
		rightMotor.setSpeed(FORWARD_SPEED);

		leftMotor.backward();
		rightMotor.backward();

	}

	public void stopMoving() {

		leftMotor.setSpeed(0);
		rightMotor.setSpeed(0);

	}

	public void liftClaw() {
		clawMotor.setSpeed(LIFTING_SPEED);
		clawMotor.rotateTo(LIFTING_DEGREE);
	}

	public void dropClaw() {
		clawMotor.setSpeed(LIFTING_SPEED);
		clawMotor.rotateTo(0);
	}

	// override this
	public boolean pickUpStyrofoamBlock() {

		return true;
	}
	
	public void scanArea(double scanAngle){
		
		blockDetector.turnOnScanMode();
		
		onPointTurnBy(-scanAngle);
		onPointTurnBy(2*scanAngle);
		onPointTurnBy(-scanAngle);
		
		blockDetector.turnOffScanMode();
		
	}
	
	private boolean travelToWaypoints(ArrayList<Intersection> listOfWayPoints) {

		Intersection intersection;
		boolean isSuccess = false;

		for (int i = 0; i < listOfWayPoints.size(); i++) {

			intersection = listOfWayPoints.get(i);

			if (i == listOfWayPoints.size() - 1) {
				isSuccess = travelCoordinate(intersection.getXInCm(),
						intersection.getYInCm(), true);
			}

			else {
				isSuccess = travelCoordinate(intersection.getXInCm(),
						intersection.getYInCm(), false);
			}

			if (!isSuccess) {

				stopMoving();

				return isSuccess;
			}
		}

		return isSuccess;

	}

	private void whileMovingTurnBy(double minimumAngle) {

		if (minimumAngle > 0) {
			turnRightWhileMoving();
		} else if (minimumAngle < 0) {
			turnLeftWhileMoving();
		}

	}
	
	private void whileMovingBackwardTurnBy(double minimumAngle) {

		if (minimumAngle > 0) {
			turnRightWhileMovingBackward();
		} else if (minimumAngle < 0) {
			turnLeftWhileMovingBackward();
		}

	}

	private void onPointTurnBy(double minimumAngle) {
		corr.turnOffCorrection();

		rotateByAngle(minimumAngle);

		corr.turnOnCorrection();
	}

	private void moveBackToPreviousIntersection() {
		corr.turnOffCorrection();

		travelCoordinate(xPrevTarget, yPrevTarget, true);

		corr.turnOnCorrection();
	}

	private double getMinAngle(double input) {

		double output = input;

		if (output > 180) {
			output -= 360;
		} else if (output < -180) {
			output += 360;
		}

		return output;

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

	
	private void howToMoveDecider(double xTarget, double yTarget, double deltaTheta){
		
		if (Math.abs(deltaTheta) > ANGLE_ERROR_THRESHOLD) {
			if (Math.abs(deltaTheta) > TURN_ON_POINT_ANGLE_THRESHOLD){
				onPointTurnBy(deltaTheta);
			}
			else {
				whileMovingTurnBy(deltaTheta);
			}

		} else {

			moveForward(); // the heading is good
		}
		
	}

	// returns the number of degrees the wheels must turn over a distance
	private int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	// returns the number of degrees to turn a certain angle
	private int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}

}
