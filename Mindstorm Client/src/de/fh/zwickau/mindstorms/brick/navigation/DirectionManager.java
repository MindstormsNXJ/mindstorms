package de.fh.zwickau.mindstorms.brick.navigation;

import lejos.nxt.NXTRegulatedMotor;
import de.fh.zwickau.mindstorms.brick.Robot;
import de.fh.zwickau.mindstorms.brick.util.Manager;

/**
 * The DirectionManager implements the rotation of the robot by an angle in a
 * specific direction.
 * 
 * @author Martin Petzold
 * 
 */

public class DirectionManager implements Manager {

	/** robot which is rotating */
	private Robot robot;
	/** if the robot currently rotates */
	private boolean isRotating;

	/**
	 * 
	 * @param robot
	 * @return
	 */
	public DirectionManager(Robot robot) {
		this.robot = robot;
		isRotating = false;
	}

	/**
	 * rotate with an angle in a specific direction
	 * 
	 * @param degree
	 *            the angle in degrees
	 * @param direction
	 *            left or right (@see {@link Direction})
	 */
	public void rotateInDirection(int degree, Direction direction) {
		if (degree == 0) {
			return;
		}
		robot.setModeRotate();
		isRotating = true;

		// the direction the robot stands at start
		int startDirection = robot.getDirection();
		int degrees = activateMotors(degree, direction);

		// check when the target angle is reached and stop rotating
		// the direction where the robot should head to at the end
		int targetdirection = (startDirection + degrees) % 360;
		if (targetdirection < 0) {
			targetdirection += 360;
		}
		if (targetdirection == 360) {
			targetdirection = 0;
		}

		boolean isRotating2 = true;
		while (isRotating2 == true) {
			int currentDirection = robot.getDirection();
			System.out.println(currentDirection);
			if (currentDirection == targetdirection) {
				isRotating2 = false;
			}
		}
		robot.stop();
	}

	private int activateMotors(int degrees, Direction direction) {
		NXTRegulatedMotor forwardMotor;
		NXTRegulatedMotor backwardMotor;
		if (degrees <= 5) { // use lower value of rotationSpeed for 15 degree
			robot.setMotorSpeed(robot.rotationSpeed / 10);
		}
		if (direction == Direction.RIGHT) {
			forwardMotor = robot.leftMotor;
			backwardMotor = robot.rightMotor;
		} else { // direction == Direction.LEFT
			forwardMotor = robot.rightMotor;
			backwardMotor = robot.leftMotor;
			degrees = -degrees;
		}
		forwardMotor.forward();
		backwardMotor.backward();
		return degrees;
	}

	public boolean isRotating() {
		return isRotating;
	}

	@Override
	public int stop() {
		isRotating = false;
		return robot.getDirection();
	}

	/**
	 * Calculates the direction to rotate from the current position.
	 * 
	 * @param currentDegree
	 * @param targetDegree
	 * @return the angle to rotate (positive for right, negative for left)
	 */
	public int calculateAngle(int currentDegree, int targetDegree) {
		int angleDiff = targetDegree - currentDegree;
		if (angleDiff >= 180) {
			angleDiff -= 360;
		} else if (angleDiff < -180) {
			angleDiff += 360;
		}
		return angleDiff;
	}
}