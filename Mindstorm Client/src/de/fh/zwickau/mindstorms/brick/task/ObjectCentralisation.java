package de.fh.zwickau.mindstorms.brick.task;

import lejos.util.Delay;
import de.fh.zwickau.mindstorms.brick.Robot;
import de.fh.zwickau.mindstorms.brick.navigation.Direction;

public class ObjectCentralisation {

	private Robot robot;
	private int startangle, distance;
	private int interval = 20;
	private int scanVariance = 3;
	private boolean scanning, centralizing;
	private int speed = 25;
	private Runnable detector;

	public ObjectCentralisation(Robot robot) {
		this.robot = robot;
	}

	public void centralize() {
		centralizing = true;
		initialize();
		int targetAngle = scan();
		centralizing = false;
		rotate(targetAngle);

	}

	private void preScan() {
		distance = robot.ultrasonicSensor.getDistance();
		startangle = robot.getDirection();
//		System.out.println(distance);
		for (int i = 1; ((distance > 40) && i<=4); i++) {
			System.out.println(i);
			if (distance > 40) {
				robot.positionManager.rotateTo((startangle -i*10)%360);
				distance = robot.ultrasonicSensor.getDistance();
//				System.out.println(distance);
			}
			if (distance > 40) {
				robot.positionManager.rotateTo((startangle+i*10)%360);
				distance = robot.ultrasonicSensor.getDistance();
//				System.out.println(distance);
			}
		}
		startangle = robot.getDirection();
	}

	private void rotate(int targetAngle) {
		System.out.println(targetAngle);
		robot.positionManager.rotateTo(targetAngle);
	}

	private int scan() {
		int left = scanLeft();
		robot.positionManager.rotateTo(startangle);
		int right = scanRight();
		return calcAngle(left, right);
	}

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

	private int calcAngle(int leftAngle, int rightAngle) {
		int centralAngle = 0;
		centralAngle = (leftAngle + rightAngle) / 2;
		if (leftAngle > rightAngle) {
			centralAngle += 180;
		}
		return centralAngle;
	}

	private int scanRight() {
		scanning = true;
		right();
		// robot.positionManager.rotate(90, Direction.RIGHT);

		scanning = false;
		int right = robot.getDirection();
		return right;
	}

	private int scanLeft() {
		scanning = true;
		left();
		// robot.positionManager.rotate(90, Direction.LEFT);
		scanning = false;
		int left = robot.getDirection();
		return left;
	}

	private void setspeed() {
		robot.leftMotor.setSpeed(speed);
		robot.rightMotor.setSpeed(speed);
	}

	private void left() {
		setspeed();
		robot.leftMotor.backward();
		robot.rightMotor.forward();
		while (robot.leftMotor.isMoving() && robot.rightMotor.isMoving()) {
			Delay.msDelay(10);
		}
	}

	private void right() {
		setspeed();
		robot.leftMotor.forward();
		robot.rightMotor.backward();
		while (robot.leftMotor.isMoving() && robot.rightMotor.isMoving()) {
			Delay.msDelay(10);
		}
	}

	private void stop() {
		robot.leftMotor.stop(true);
		robot.rightMotor.stop(false);
	}
}
