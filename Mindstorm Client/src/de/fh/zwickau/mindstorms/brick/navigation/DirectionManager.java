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

		while (isRotating == true) {
			int currentDirection = robot.getDirection();
			if (currentDirection == targetdirection) {
				isRotating = false;
			}
		}
		stop();
	}

	private int activateMotors(int degrees, Direction direction) {
		NXTRegulatedMotor forwardMotor;
		NXTRegulatedMotor backwardMotor;
		if (degrees <= 15) { // use lower value of rotationSpeed for 15 degree
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
		robot.stop();
		isRotating = false;
		return 0; // get the current heading from the compass sensor
	}
}