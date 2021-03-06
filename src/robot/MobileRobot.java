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

	/**
	 * The x coordinate of the previous target. Used mainly to determine if the
	 * robot should move backwards (after finding an obstacle for example).
	 */
	private static double xPrevTarget;

	/**
	 * The y coordinate of the previous target. Used mainly to determine if the
	 * robot should move backwards (after finding an obstacle for example).
	 */
	private static double yPrevTarget;

	/**
	 * counts how many moves the robot has made.
	 */
	private static int travelCounter = 0;
	// private static boolean isPathSafe = false;

	private final int ANGLE_ERROR_THRESHOLD = 1; // measured in degrees
	private final int POSITION_ERROR_THRESHOLD = 1;

	/**
	 * if the change in angle is greater than this, the robot should turn on
	 * point instead of while moving.
	 */
	private final int TURN_ON_POINT_ANGLE_THRESHOLD = 20;

	private final int POINT_IS_BEHIND_ANGLE_THRESHOLD = 45;
	// private final double PATH_SCAN_ANGLE = 15;
	// private final int PATH_IS_SAFE_THRESHOLD = 20;

	/**
	 * localize after this many moves
	 */
	private final int LOCALIZE_PERIODICALLY = 6;

	/**
	 * Default Constructor
	 * <p>
	 * Starts odometer of the MobileRobot
	 */
	public MobileRobot() {
	}

	/**
	 * Gets the robot to travel to a destination using the shortest path. If the
	 * robot finds an obstacle, it will find a new shortest path. It will
	 * continue doing this until it determines that the destination is
	 * unreachable, at which point it simply quits.
	 * 
	 * @param destination
	 *            the destination the robot should travel to.
	 */
	public void travelTo(Intersection destination) {

		try {
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

					listOfWayPoints = AStar.algorithm(source, destination);
				}

				isSuccess = travelToWaypoints(listOfWayPoints);

				if (!isSuccess) {

					if (!blockDetector.isObjectStyrofoam()) {
						moveBackToPreviousIntersection();
						removePath(listOfWayPoints);
					}

					else {

						if (pickUpStyrofoamBlock()) {
							return;
						}

						else {
							moveBackToPreviousIntersection();
							removePath(listOfWayPoints);
						}

					}

				}
			}
		} catch (IndexOutOfBoundsException e) {
			specialMoveAround();
			return;
		}

	}

	/**
	 * gets the robot to travel to the target zone using the shortest path.
	 */
	public void travelToTargetZone() {

		travelTo(new Intersection(INT_SPECIAL_FLAG, INT_SPECIAL_FLAG));

	}

	/**
	 * travels to an intersection directly, without looking for shortest path.
	 * 
	 * @param x
	 *            the x tile coordinate
	 * @param y
	 *            the y tile coordinate
	 */
	public void travelTileCoordinate(int x, int y) {

		double xInCm = x * Map.TILE_SIZE;
		double yInCm = y * Map.TILE_SIZE;

		travelCoordinate(xInCm, yInCm, true);

	}

	/**
	 * Gets the robot to travel straight to a coordinate
	 * 
	 * @param xTarget
	 *            the x coordinate in centimeters
	 * @param yTarget
	 *            the y coordinate in centimeters
	 * @param stopAtTarget
	 *            true if the robot should stop at the target
	 * @return true if the robot successfully arrived at the target, false
	 *         otherwise
	 */
	public boolean travelCoordinate(double xTarget, double yTarget,
			boolean stopAtTarget) {

		// this happens when the robot sees an obstacle and should back up to
		// previous point. if the previous point ends up being in front of the
		// robot, then it will move forward.
		if (xTarget == xPrevTarget && yTarget == yPrevTarget) {
			travelCoordinateBackwards(xTarget, yTarget, true);
		}

		double deltaTheta;

		// keep looping while the difference between the current position and
		// target position is greater than the position error threshold
		while (!isAtPoint(xTarget, yTarget)) {

			if (blockDetector.isObjectInFront()
					&& !isAtPoint(xPrevTarget, yPrevTarget)) {
				return false;
			}

			deltaTheta = findAngle(xTarget, yTarget);

			howToMoveDecider(xTarget, yTarget, deltaTheta);
		}

		if (stopAtTarget) {
			stopMoving();
		}

		xPrevTarget = xTarget;
		yPrevTarget = yTarget;

		return true;

	}

	/**
	 * Gets the robot to travel straight to coordinate, but moving backwards. If
	 * its not practical to move backwards, then turn around to face the target,
	 * and move forward
	 * 
	 * @param xTarget
	 *            the x coordinate in centimeters
	 * @param yTarget
	 *            the y coordinate in centimeters
	 * @param stopAtTarget
	 *            true if the robot should stop at the target
	 */
	public void travelCoordinateBackwards(double xTarget, double yTarget,
			boolean stopAtTarget) {

		double deltaTheta;
		double backwardDeltaTheta;

		while (!isAtPoint(xTarget, yTarget)) {

			deltaTheta = findAngle(xTarget, yTarget);

			backwardDeltaTheta = getMinAngle(deltaTheta - 180);

			// is the point behind the robot
			if (Math.abs(backwardDeltaTheta) < POINT_IS_BEHIND_ANGLE_THRESHOLD) {

				if (backwardDeltaTheta > ANGLE_ERROR_THRESHOLD) {

					if (Math.abs(backwardDeltaTheta) > TURN_ON_POINT_ANGLE_THRESHOLD) {

						onPointTurnBy(backwardDeltaTheta);
					}

					else {
						whileMovingBackwardTurnBy(backwardDeltaTheta);
					}
				}

				else {
					moveBackward();
				}
			}

			else {
				howToMoveDecider(xTarget, yTarget, deltaTheta);
			}
		}

	}

	/**
	 * Moves the specified distance
	 * 
	 * @param magnitudeInCm
	 *            the distance the robot should move. Negative means backwards.
	 */
	public void travelMagnitude(double magnitudeInCm) {

		travelMagnitude(magnitudeInCm, FORWARD_SPEED);
	}

	/**
	 * Moves the specified distance slowly.
	 * 
	 * @param magnitudeInCm
	 *            the distance the robot should move. Negative means backwards.
	 */
	public void travelMagnitudeSlow(double magnitudeInCm) {

		travelMagnitude(magnitudeInCm, SLOW_FORWARD_SPEED);
	}

	/**
	 * Moves the specified distance at the specified speed.
	 * 
	 * @param magnitudeInCm
	 *            the distance the robot should move. Negative means backwards.
	 * @param speed
	 *            the speed at which the robot should move
	 */
	public void travelMagnitude(double magnitudeInCm, int speed) {

		int leftAmount = convertDistance(LEFT_RADIUS, magnitudeInCm);

		int rightAmount = convertDistance(RIGHT_RADIUS, magnitudeInCm);

		leftMotor.setSpeed(speed);
		rightMotor.setSpeed(speed);

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

	/**
	 * turn to the specified angle while moving forward.
	 * 
	 * @param targetTheta
	 *            the angle to turn to
	 */
	public void turnToWhileMoving(double targetTheta) {
		double angleToRotateBy = targetTheta - odo.getTheta();

		// turn a minimal angle
		angleToRotateBy = getMinAngle(angleToRotateBy);

		// turn while travelling by adjusting motor speeds
		whileMovingTurnBy(angleToRotateBy);

	}

	/**
	 * turn to the specified angle while staying in one position
	 * 
	 * @param targetTheta
	 *            the angle to turn to
	 */
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

	public void moveForwardSlow() {

		leftMotor.setSpeed(SLOW_FORWARD_SPEED);
		rightMotor.setSpeed(SLOW_FORWARD_SPEED);

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

	/**
	 * closes the claw and lifts it
	 */
	public void liftClaw() {
		clawMotor.setSpeed(LIFTING_SPEED);
		clawMotor.rotateTo(LIFTING_DEGREE);
	}

	/**
	 * lowers the claw and opens it
	 */
	public void dropClaw() {
		clawMotor.setSpeed(LIFTING_SPEED);
		clawMotor.rotateTo(DROPPING_DEGREE);
	}

	// override this
	/**
	 * Explorer and BlockMover override this method
	 * 
	 * @return true if the robot should pick up the styrofoam block, false if
	 *         the robot should move around the styrofoam block.
	 */
	public boolean pickUpStyrofoamBlock() {

		return true;
	}

	/**
	 * Turns left, right, and then back to the original orientation. Turns by
	 * the specified angle.
	 * 
	 * @param scanAngle
	 *            half of the total range to scan
	 */
	public void scanArea(double scanAngle) {

		blockDetector.turnOnMinDistanceScanMode();

		onPointTurnBy(-scanAngle);
		onPointTurnBy(2 * scanAngle);
		onPointTurnBy(-scanAngle);

		blockDetector.turnOffMinDistanceScanMode();

	}

	/**
	 * this is used to localize periodically
	 */
	public void performRotationCorrection() {

		double closestRightAngle = 90 * Math.round(odo.getTheta() / 90);

		turnToOnPoint(closestRightAngle);

		for (int i = 0; i < 4; i++) {
			onPointTurnBy(-90);
			if (!blockDetector.isObjectInFront()) {
				break;
			}
		}

		onPointTurnBy(90);

		corr.turnOffCorrection();
		moveForwardSlow();
		corr.rotateCorrection();

		travelMagnitudeSlow(-SENSOR_TO_WHEEL_DISTANCE);

		onPointTurnBy(90);

		corr.turnOffCorrection();
		moveForwardSlow();
		corr.rotateCorrection();

		travelMagnitudeSlow(-SENSOR_TO_WHEEL_DISTANCE);

	}

	public void performRotationCorrectionLocalization() {

		corr.turnOffCorrection();
		moveForwardSlow();
		corr.rotateCorrection();

		travelMagnitudeSlow(-SENSOR_TO_WHEEL_DISTANCE);

		onPointTurnBy(90);

		corr.turnOffCorrection();
		moveForwardSlow();
		corr.rotateCorrection();

		travelMagnitudeSlow(-SENSOR_TO_WHEEL_DISTANCE);

	}

	/**
	 * Travels to all the waypoints in the input list.
	 * 
	 * @param listOfWayPoints
	 *            the waypoints the robot should travel to
	 * @return true if the robot successfully finished travelling to all
	 *         waypoints
	 */
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

			if (isSuccess) {
				travelCounter++;
				if (travelCounter % LOCALIZE_PERIODICALLY == 0) {
					performRotationCorrection();
				}
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

	/**
	 * 
	 * @param input
	 *            the input angle
	 * @return the smallest equivalent angle
	 */
	private double getMinAngle(double input) {

		double output = input;

		if (output > 180) {
			output -= 360;
		} else if (output < -180) {
			output += 360;
		}

		return output;

	}

	/**
	 * 
	 * @param xTarget
	 *            x coordinate in centimeters
	 * @param yTarget
	 *            y coordinate in centimeters
	 * @return true if the robot is at the specified coordinates
	 */
	private boolean isAtPoint(double xTarget, double yTarget) {

		if (Math.abs(xTarget - odo.getX()) < POSITION_ERROR_THRESHOLD
				&& Math.abs(yTarget - odo.getY()) < POSITION_ERROR_THRESHOLD) {
			return true;
		}

		else {
			return false;
		}

	}

	/**
	 * decides whether the robot needs to turn or just move forward. If the
	 * robot needs to turn, this method will determine if it needs to turn while
	 * moving or while on point.
	 * 
	 * @param xTarget
	 * @param yTarget
	 * @param deltaTheta
	 */
	private void howToMoveDecider(double xTarget, double yTarget,
			double deltaTheta) {

		if (Math.abs(deltaTheta) > ANGLE_ERROR_THRESHOLD) {
			if (Math.abs(deltaTheta) > TURN_ON_POINT_ANGLE_THRESHOLD) {
				onPointTurnBy(deltaTheta);
			} else {
				whileMovingTurnBy(deltaTheta);
			}

		} else {

			moveForward(); // the heading is good
		}

	}

	/**
	 * Removes the path between the intersection the robot is at and the next
	 * intersetion in the list of way points.
	 * 
	 * @param listOfWayPoints
	 */
	private void removePath(ArrayList<Intersection> listOfWayPoints) {

		Intersection prevIntersection = Map.getIntersection(xPrevTarget,
				yPrevTarget);

		int indexOfNextIntersection = listOfWayPoints.indexOf(prevIntersection) + 1;

		Intersection nextIntersection = listOfWayPoints
				.get(indexOfNextIntersection);

		Map.removeEdge(prevIntersection, nextIntersection);

	}

	private double findAngle(double xTarget, double yTarget) {
		double xDiff;
		double yDiff;
		double targetTheta;
		double deltaTheta;

		xDiff = xTarget - odo.getX();
		yDiff = yTarget - odo.getY();
		targetTheta = odo.fixDegAngle(90 - Math.toDegrees(Math.atan2(yDiff,
				xDiff)));

		deltaTheta = targetTheta - odo.getTheta();

		deltaTheta = getMinAngle(deltaTheta);

		return deltaTheta;

	}

	// returns the number of degrees the wheels must turn over a distance
	private int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	// returns the distance from tacho count
	private int convertTachoCount(double radius, int tachoCount) {
		return (int) ((2 * Math.PI * radius) / (360.0 * tachoCount));
	}

	// returns the number of degrees to turn a certain angle
	private int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}

	/**
	 * this is used in the case of the exception that may occur if the robot
	 * thinks it has no paths out of its current intersection.
	 */
	private void specialMoveAround() {

		int initialLineCount = corr.getLineCount();
		double initialX = odo.getX();
		double initialY = odo.getY();

		int initialLeftTacho = leftMotor.getTachoCount();
		int initialRightTacho = rightMotor.getTachoCount();

		blockDetector.turnOffBlockDetection();

		travelMagnitude(Map.TILE_SIZE * 2);

		int changeInLineCount = corr.getLineCount() - initialLineCount;
		int currentLineCount = corr.getLineCount();

		int leftTachoDifference = leftMotor.getTachoCount() - initialLeftTacho;
		int rightTachoDifference = rightMotor.getTachoCount()
				- initialRightTacho;

		while ((currentLineCount < (initialLineCount + 2 * changeInLineCount))
				&& (convertTachoCount(LEFT_RADIUS, leftTachoDifference) < Map.TILE_SIZE * 2)
				&& (convertTachoCount(RIGHT_RADIUS, rightTachoDifference) < Map.TILE_SIZE * 2)) {

			moveBackward();
			currentLineCount = corr.getLineCount();

		}

		stopMoving();

		odo.setY(initialY);
		odo.setX(initialX);

		// performRotationCorrection();

		blockDetector.turnOnBlockDetection();

	}

}
