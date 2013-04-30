package de.fh.zwickau.mindstorms.brick.navigation;

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

	// int motorSpeed = 200; i think this isn't needed
	private Robot robot;
	private boolean isRotating;
	private int startDirection; // direction when robot starts rotation
	private int endDirection; // the direction the robot should rotate to
	private int degrees; // the degrees to rotate
	// private int stepWide = 45;// the stepwide for stepwise rotating i think
	// this isn't needed

	/**
	 * 
	 * @param robot
	 * @return
	 */
	public DirectionManager(Robot robot) {
		this.robot = robot;
	}

	/**
	 * rotate with an angle in a specific direction
	 * 
	 * @param deg
	 *            the angle in degrees
	 * @param dir
	 *            the direction, left or right
	 */
	public void rotateInDirection(int deg, Direction dir) {
		robot.setModeRotate();
		isRotating = true;
		startDirection = (int) robot.compassSensor.getDegrees();
		if (dir == Direction.RIGHT) {
			robot.leftMotor.forward();
			robot.rightMotor.backward();
			degrees = deg;
		}
		if (dir == Direction.LEFT) {
			robot.leftMotor.backward();
			robot.rightMotor.forward();
			degrees = -deg;
		}

		// check when the target angle is reached and stop rotating
		Thread check = new Thread(new Runnable() {

			@Override
			public void run() {
				while (isRotating == true) {
					endDirection = (int) robot.compassSensor.getDegrees();
					int targetdirection = (startDirection + degrees) % 360;
					if (targetdirection < 0) {
						targetdirection = 360 + targetdirection;
					}
					// System.out.println(targetdirection); // just for
					// debugging
					if (endDirection == targetdirection) {
						isRotating = false;
					}
				}
				stop();
			}

		});
		check.start();
		try {
			check.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	// i don't know if stepWide is important, because it's never used

	// public void setStepWide(int stepWide) {
	// this.stepWide = stepWide;
	// }
	//
	// public int getStepWide() {
	// return stepWide;
	// }

	public boolean isRotating() {
		return isRotating;
	}

	@Override
	public int stop() {
		robot.leftMotor.stop(true);
		robot.rightMotor.stop();
		isRotating = false;

		return 0; // get the current heading from the compasssensor
	}
}