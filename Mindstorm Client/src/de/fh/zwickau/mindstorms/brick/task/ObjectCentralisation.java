package de.fh.zwickau.mindstorms.brick.task;

import java.lang.annotation.Target;

import lejos.nxt.Button;
import lejos.util.Delay;
import de.fh.zwickau.mindstorms.brick.Robot;

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

	private void rotate(int targetAngle) {
		System.out.println(targetAngle);
		robot.positionManager.rotateTo(targetAngle);
	}

	private int scan() {
		int left = scanLeft();
		int right = scanRight();
		return calcAngle(left, right);

	}

	private void initialize() {
		startangle = robot.getDirection();
		do {
			distance = robot.ultrasonicSensor.getDistance();
		} while (distance > 50);
		detector = new Runnable() {
			@Override
			public void run() {
				System.out.println("run");
				while (centralizing) {

					if (scanning) {
						int variance = (int) robot.ultrasonicSensor.getRange()
								- distance;
						System.out.println(distance + "\t" + variance);
						// TODO make detection more flexible by reset distance
						if (scanVariance < variance) {
							System.out.println(" stop");
							stop();
							// robot.positionManager.stop();
						} else {
							System.out.println(" not stop");
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
		robot.positionManager.rotateTo(startangle);
		return right;
	}

	private int scanLeft() {
		scanning = true;
		left();
		// robot.positionManager.rotate(90, Direction.LEFT);
		scanning = false;
		int left = robot.getDirection();
		robot.positionManager.rotateTo(startangle);
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
