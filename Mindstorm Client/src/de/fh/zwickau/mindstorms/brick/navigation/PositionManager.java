package de.fh.zwickau.mindstorms.brick.navigation;

import de.fh.zwickau.mindstorms.brick.Robot;
import de.fh.zwickau.mindstorms.brick.util.Manager;
import lejos.robotics.navigation.Pose;

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

	public boolean isPositioning() {
		return directionManager.isRotating() || movementManager.isMoving();
	}

	/**
	 * rotate to an angle
	 * 
	 * @param deg
	 */
	public void rotateTo(int deg) {
		int startdegrees = (int) robot.compassSensor.getDegrees();
		int toRotate = Math.abs(angleCorrection(startdegrees, deg));
		boolean left = false;
		if (angleCorrection(startdegrees, deg) <= 0) {
			directionManager.rotateInDirection(toRotate, Direction.LEFT);
		}
		if ((angleCorrection(startdegrees, deg)) > 0) {
			directionManager.rotateInDirection(toRotate, Direction.RIGHT);
		}
	}

	public void rotate(int degree, Direction direction) {
		directionManager.rotateInDirection(degree, direction);
	}

	/**
	 * Rotate in ...steps in an direction
	 * 
	 * @param steps
	 * @param direction
	 * @param stepWide
	 *            , default stepWide should be 45
	 */
	public void rotateStepwise(int steps, Direction direction, int stepWide) {
		directionManager.rotateInDirection(steps * stepWide, direction);
	}

	private void updateRotation(int i) {
		pose.setHeading(pose.getHeading() + i);
	}

	public void move(int distance) {
		movementManager.move(distance);
		// updatePosition(distance);
	}

	private void updatePosition(int distance) {
		float x = (float) (Math.cos(pose.getHeading()) * distance);
		float y = (float) (Math.sin(pose.getHeading()) * distance);
		pose.setLocation(x, y);
	}

	@Override
	public int stop() {
		int distance = movementManager.stop();
		updatePosition(distance);
		directionManager.stop();
		updateRotation((int) robot.compassSensor.getDegrees());
		return 0;
	}

	private int angleCorrection(int currentDegree, int newDegree) {
		int c = newDegree - currentDegree;
		if (c >= 180) {
			c = c - 360;
		}
		if (c < -180) {
			c = c + 360;
		}
		return c;

	}
}