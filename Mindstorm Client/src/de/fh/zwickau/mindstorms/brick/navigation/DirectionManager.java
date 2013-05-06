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

	// int motorSpeed = 200; i think this isn't needed
	/** robot which is rotating */
	private Robot robot;
	/** if the robot currently rotates */
	private boolean isRotating;
	/** the degree to rotate */
	private int degrees;

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
	 * @param degree
	 *            the angle in degrees
	 * @param direction
	 *            left or right (@see {@link Direction})
	 */
	public void rotateInDirection(int degree, Direction direction) {
		robot.setModeRotate();
		isRotating = true;
		
		checkMotors(degree, direction);
		// check when the target angle is reached and stop rotating
		Thread check = new Thread(new Runnable() {

			@Override
			public void run() {
				//the direction the robot stands at start
				int startDirection = (int) robot.compassSensor.getDegrees();
				//the direction where the robot should head to at the end
				int targetdirection = (startDirection + degrees) % 360;
				if (targetdirection < 0) {
					targetdirection = 360 + targetdirection;
				}
				while (isRotating == true) {
					int currentDirection = (int) robot.compassSensor.getDegrees();
					// System.out.println(targetdirection);
					if (currentDirection == targetdirection) {
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

	private void checkMotors(int degree, Direction direction) {
		NXTRegulatedMotor forwardMotor;
		NXTRegulatedMotor backwardMotor;
		if (direction == Direction.RIGHT) {
			forwardMotor = robot.leftMotor;
			backwardMotor = robot.rightMotor;
			degrees = degree;
		} else {
			forwardMotor = robot.rightMotor;
			backwardMotor = robot.leftMotor;
			degrees = -degree;
		}
		forwardMotor.forward();
		backwardMotor.backward();
	}

	public boolean isRotating() {
		return isRotating;
	}

	@Override
	public int stop() {
		robot.stop();
		isRotating = false;
		return 0; // get the current heading from the compasssensor
	}
}