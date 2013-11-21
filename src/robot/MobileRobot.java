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

	private double xPrevTarget;
	private double yPrevTarget;

	private final int ANGLE_ERROR_THRESHOLD = 1; // measured in degrees
	private final int POSITION_ERROR_THRESHOLD = 1;
	private final int TURN_ON_POINT_ANGLE_THRESHOLD = 30;

	/**
	 * Default Constructor
	 * <p>
	 * Starts odometer of the MobileRobot
	 */
	public MobileRobot() {
	}

	// returns false when it stops trying to get to the destination. This may be
	// because the destination is impossible to get to, or because it has
	// detected a styrofoam block and does not know whether to pick it up or
	// avoid it 
	public void travelTo(Intersection destination) {
		boolean isSuccess = false;

		while (!isSuccess) {

			Intersection source = Map.getIntersection(odo.getX(), odo.getY());

			if (destination.getAdjacencyList().isEmpty()) {
				return;
			}

			ArrayList<Intersection> listOfWayPoints = Dijkstra.algorithm(
					source, destination);

			isSuccess = travelToWaypoints(listOfWayPoints);

			if (!isSuccess) {

				if (!blockDetector.objectIsStyrofoam()) {
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
					
					if(avoidStyrofoamBlock()){
						return;
					}

				}

			}
		}

	}

	public void travelToTargetZone() {
		Intersection source = Map.getIntersection(odo.getX(), odo.getY());

		ArrayList<Intersection> listOfWayPoints = Dijkstra
				.algorithmForTargetZone(source);

		travelToWaypoints(listOfWayPoints);

	}

	public void travelTileCoordinate(int x, int y) {

		double xInCm = x * Map.TILE_SIZE;
		double yInCm = y * Map.TILE_SIZE;

		travelCoordinate(xInCm, yInCm, true);

	}

	public boolean travelCoordinate(double xTarget, double yTarget,
			boolean stopAtTarget) {

		double xDiff;
		double yDiff;
		double targetTheta;
		double deltaTheta;

		// keep looping while the difference between the current position and
		// target position is greater than the position error threshold
		while (!isAtPoint(xTarget, yTarget)) {

			if (blockDetector.objectInFrontOfRobot()
					&& !(xTarget == xPrevTarget && yTarget == yPrevTarget)) {
				return false;
			}

			// this means there is a wooden block in the way, and we want to
			// move backwards to the previous intersection.
			if (xTarget == xPrevTarget && yTarget == yPrevTarget) {
				moveBackward();
			}

			else {

				// Determine whether to turn or not
				xDiff = xTarget - odo.getX();
				yDiff = yTarget - odo.getY();
				targetTheta = 90 - Math.toDegrees(Math.atan2(yDiff, xDiff));

				// RConsole.println("targetTheta" + targetTheta);

				// change in theta is target minus current

				deltaTheta = targetTheta - odo.getTheta();

				deltaTheta = getMinAngle(deltaTheta);

				// if the heading is off by more than acceptable error, we must
				// correct
				if (Math.abs(deltaTheta) > ANGLE_ERROR_THRESHOLD) {

					if (isAtPoint(xPrevTarget, yPrevTarget)
							&& (Math.abs(deltaTheta) > TURN_ON_POINT_ANGLE_THRESHOLD)) {
						onPointTurnBy(deltaTheta);
					} else {
						whileMovingTurnBy(deltaTheta);
					}

				} else {

					moveForward(); // the heading is good
				}

			}
		}

		if (stopAtTarget) {
			stopMoving();
		}

		xPrevTarget = xTarget;
		yPrevTarget = yTarget;

		return true;

	}

	public void initializePrevTarget(double x, double y) {
		xPrevTarget = x;
		yPrevTarget = y;
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

	public void stopMoving() {

		leftMotor.setSpeed(0);
		rightMotor.setSpeed(0);

	}

	public void liftClaw() {
		clawMotor.setSpeed(120);
		clawMotor.rotateTo(310);
	}
	
	//override this
	public boolean avoidStyrofoamBlock(){
		
		return true;
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

	// returns the number of degrees the wheels must turn over a distance
	private int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	// returns the number of degrees to turn a certain angle
	private int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}
	
	
	
	private void lightLocalization(){
		
		corr.doRotationalCorrection();
		
		rotateByAngle(-360);
		
		corr.doStraightLineCorrection();
		
	}
	

}
