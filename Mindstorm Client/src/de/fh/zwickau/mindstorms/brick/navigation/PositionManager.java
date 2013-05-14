package de.fh.zwickau.mindstorms.brick.navigation;

import de.fh.zwickau.mindstorms.brick.Robot;
import de.fh.zwickau.mindstorms.brick.util.Manager;
import lejos.robotics.navigation.Pose;

/**
 * With the PositionManager the position of the robot can be changed. It's
 * responsible for moving and rotating.
 * 
 * @author Philip Laube
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
	}

	/**
	 * @return the current pose
	 */

	public Pose getPose() {
		return pose;
	}

	/**
	 * Returns true if the robot is rotating or moving.
	 * 
	 * @return whether the robot is currently moving
	 */
	public boolean isPositioning() {
		return directionManager.isRotating() || movementManager.isMoving();
	}

	/**
	 * Rotates the robot to a absolute direction in degrees.
	 * 
	 * @param degree
	 *            the direction to rotate to
	 */
	public void rotateTo(int degree) {
		int startdegrees = robot.getDirection();
		int calculatedAngle = calculateAngle(startdegrees, degree);
		int toRotate = Math.abs(calculatedAngle);
		if (calculatedAngle <= 0) {
			rotate(toRotate, Direction.LEFT);
		} else {
			rotate(toRotate, Direction.RIGHT);
		}
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

	private void updateRotation() {
		if (!isPositioning()) {
			pose.setHeading(robot.getDirection());
		} else {
			System.err.println("Still rotating... something went wrong!");
		}
	}

	public void updatePosition(int distance) {
		if (!isPositioning()) {
			float x = (float) (Math.sin(Math.toRadians(pose.getHeading())) * distance);
			float y = (float) (Math.cos(Math.toRadians(pose.getHeading())) * distance);
			pose.setLocation(pose.getX() + x, pose.getY() + y);
		} else {
			System.err.println("Still moving... something went wrong!");
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
		// TODO updatePosition for the movement
		directionManager.stop();
		updateRotation();
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
			angleDiff -= 360;
		} else if (angleDiff < -180) {
			angleDiff += 360;
		}
		return angleDiff;
	}
}