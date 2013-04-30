package de.fh.zwickau.mindstorms.brick.navigation;

import de.fh.zwickau.mindstorms.brick.Robot;
import de.fh.zwickau.mindstorms.brick.util.Manager;
import lejos.robotics.navigation.Pose;

/**
 * With the PositionManager the position of the robot can be changed. It's
 * responsible for moving and rotating.
 * 
 * @author
 * 
 */
public class PositionManager implements Manager {

	private Pose pose;
	private Robot robot;
	private DirectionManager directionManager;
	private MovementManager movementManager;

	public PositionManager(Pose pose, Robot robot) {
		this.pose = pose;
		this.robot = robot;
		this.directionManager = new DirectionManager(robot);
		this.movementManager = new MovementManager(robot, this);
		robot.positionManager = this;
	}

	public Pose getPose() {
		return pose;
	}

	/**
	 * Returns true if the robot is rotating or moving.
	 * 
	 * @return
	 */
	public boolean isPositioning() {
		return directionManager.isRotating() || movementManager.isMoving();
	}

	/**
	 * Rotates the robot to a absolute direction in degrees.
	 * 
	 * @param deg
	 *            the direction to rotate to
	 */
	public void rotateTo(int deg) {
		int startdegrees = (int) robot.compassSensor.getDegrees();
		int toRotate = Math.abs(calculateAngle(startdegrees, deg));
		if (calculateAngle(startdegrees, deg) <= 0) {
			directionManager.rotateInDirection(toRotate, Direction.LEFT);
		}
		if ((calculateAngle(startdegrees, deg)) > 0) {
			directionManager.rotateInDirection(toRotate, Direction.RIGHT);
		}
		updateRotation();
	}

	/**
	 * Rotates the robot by a given amount in degrees and a direction, starting
	 * by the robots current direction.
	 * 
	 * @param degree
	 * @param direction
	 */
	public void rotate(int degree, Direction direction) {
		directionManager.rotateInDirection(degree, direction);
		updateRotation();
	}

	/**
	 * Rotate in ...steps in an direction
	 * 
	 * @param steps
	 * @param direction
	 * @param stepWide
	 *            default stepWide should be 45
	 */
	public void rotateStepwise(int steps, Direction direction, int stepWide) {
		directionManager.rotateInDirection(steps * stepWide, direction);
		updateRotation();
	}

	private void updateRotation() {
		if (!isPositioning()) {
			pose.setHeading(robot.compassSensor.getDegrees());
		}
	}

	public void updatePosition(int distance) {
		if (!isPositioning()) {
			float x = (float) (Math.cos(Math.toRadians(pose.getHeading())) * distance);
			float y = (float) (Math.sin(Math.toRadians(pose.getHeading())) * distance);
			pose.setLocation(pose.getX() + x, pose.getY() + y);
		}
	}

	/**
	 * Moves the robot in straight direction forward (positive values) or
	 * backwards (negative values)
	 * 
	 * @param distance
	 *            the distance to move in cm
	 */
	public void move(int distance) {
		movementManager.move(distance);
		updatePosition(distance);
	}

	@Override
	public int stop() {
		movementManager.stop();
		directionManager.stop();
		return 0;
	}

	/**
	 * Calculates the direction to rotate from the current position.
	 * 
	 * @param currentDegree
	 * @param targetDegree
	 * @return the angle to rotate (positive for right, negative for left)
	 */
	private int calculateAngle(int currentDegree, int targetDegree) {
		int angleDiff = targetDegree - currentDegree;
		if (angleDiff >= 180) {
			angleDiff = angleDiff - 360;
		}
		if (angleDiff < -180) {
			angleDiff = angleDiff + 360;
		}
		return angleDiff;
	}
}