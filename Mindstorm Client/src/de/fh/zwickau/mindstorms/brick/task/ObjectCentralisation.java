package de.fh.zwickau.mindstorms.brick.task;

import lejos.util.Delay;
import de.fh.zwickau.mindstorms.brick.Robot;

/**
 * This class realize the centralization of the robot by an object if this
 * object is in an 90 degrees area and roughly 30 cm in front of the robot
 * 
 * 
 * @author Markus Krummnacker
 * @version 1.4
 */
public class ObjectCentralisation {

	private Robot robot;
	private int startangle, distance;
	private int speed = 25;
	private int interval = 20;
	private int scanVariance = 3;
	private int preScanSteps=5; // degrees
	private boolean scanning, centralizing;
	private Runnable detector;

	/**
	 * @param robot
	 */
	public ObjectCentralisation(Robot robot) {
		this.robot = robot;
	}

	/**
	 * Centralize an object in front of the robot by an 90 degrees radius
	 */
	public void centralize() {
		centralizing = true;
		initialize();
		int targetAngle = scan();
		centralizing = false;
		rotateTo(targetAngle);

	}

	/**
	 * roughly adjust the robot to the object
	 */
	private void preScan() {
		distance = getDistance();
		startangle = robot.getDirection();
		// System.out.println(distance);
		for (int i = 1; ((distance > 40) && i <= 45/preScanSteps); i++) {
			System.out.println(i);
			if (distance > 40) {
				rotateTo((startangle - i * preScanSteps) % 360);
				distance = getDistance();
				// System.out.println(distance);
			}
			if (distance > 40) {
				rotateTo((startangle + i * preScanSteps) % 360);
				distance = getDistance();
				// System.out.println(distance);
			}
		}
		startangle = robot.getDirection();
	}

	/**
	 * scan the area in front of the robot
	 * 
	 * @return the angle where the object is
	 */
	private int scan() {
		int left = scanLeft();
		rotateTo(startangle);
		int right = scanRight();
		return calcAngle(left, right);
	}

	/**
	 * set up the the centralization
	 */
	private void initialize() {
		preScan();
		detector = new Runnable() {
			@Override
			public void run() {
				while (centralizing) {
					if (scanning) {
						int variance = (int) robot.ultrasonicSensor.getRange()
								- distance;
						if (scanVariance < variance) {
							stop();
							// robot.positionManager.stop();
						} else {
						}
					}
					Delay.msDelay(interval);
				}

			}
		};
		new Thread(detector).start();
	}

	/**
	 * calculate angle in the middle of the left and right angle
	 * 
	 * @param leftAngle
	 *            angle left
	 * @param rightAngle
	 *            angle right
	 * @return middle angle
	 */
	private int calcAngle(int leftAngle, int rightAngle) {
		int centralAngle = 0;
		centralAngle = (leftAngle + rightAngle) / 2;
		if (leftAngle > rightAngle) {
			centralAngle += 180;
		}
		return centralAngle;
	}

	/**
	 * scan objects right side
	 * 
	 * @return the angle at losing the object from the scan rage
	 */
	private int scanRight() {
		scanning = true;
		right();
		// robot.positionManager.rotate(90, Direction.RIGHT);
		scanning = false;
		int right = robot.getDirection();
		return right;
	}

	/**
	 * scan objects left side
	 * 
	 * @return the angle at losing the object from the scan rage
	 */
	private int scanLeft() {
		scanning = true;
		left();
		// robot.positionManager.rotate(90, Direction.LEFT);
		scanning = false;
		int left = robot.getDirection();
		return left;
	}

	/**
	 * @return the actual distance from the nearest object in scan rage
	 */
	private int getDistance() {
		return robot.ultrasonicSensor.getDistance();
	}

	/**
	 * rotate the robot to an specific angle
	 * 
	 * @param targetAngle
	 *            to rotate to
	 */
	private void rotateTo(int targetAngle) {
		robot.positionManager.rotateTo(targetAngle);
	}

	/**
	 * set motor speed
	 */
	private void setspeed() {
		robot.leftMotor.setSpeed(speed);
		robot.rightMotor.setSpeed(speed);
	}

	/**
	 * rotate the robot left
	 */
	private void left() {
		setspeed();
		robot.leftMotor.backward();
		robot.rightMotor.forward();
		while (robot.leftMotor.isMoving() && robot.rightMotor.isMoving()) {
			Delay.msDelay(10);
		}
	}

	/**
	 * rotate the robot left
	 */
	private void right() {
		setspeed();
		robot.leftMotor.forward();
		robot.rightMotor.backward();
		while (robot.leftMotor.isMoving() && robot.rightMotor.isMoving()) {
			Delay.msDelay(10);
		}
	}

	/**
	 * stops the motor rotation
	 */
	private void stop() {
		robot.leftMotor.stop(true);
		robot.rightMotor.stop(false);
	}
}
