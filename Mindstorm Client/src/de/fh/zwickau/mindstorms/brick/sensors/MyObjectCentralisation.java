package de.fh.zwickau.mindstorms.brick.sensors;

import lejos.util.Delay;
import de.fh.zwickau.mindstorms.brick.Robot;
import de.fh.zwickau.mindstorms.brick.navigation.Direction;

public class MyObjectCentralisation {

	private Robot robot;
	private int distance;
	private boolean isdetected = true;
	private int startAngle;

	public MyObjectCentralisation(Robot robot) {
		this.robot = robot;
		startAngle = (int) robot.compassSensor.getDegrees();
		int targetAngle = calcAngle(scanLeft(), scanRight());
		System.out.println(targetAngle);
		robot.positionManager.rotateTo(targetAngle);
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
		while (isdetected) {
			robot.positionManager.rotate(3, Direction.RIGHT);
			int newDistance = robot.ultrasonicSensor.getDistance();
			if (Math.abs(newDistance - distance) >= 5) {
				isdetected = checkReallyOutOfRange();
				System.out.println("Out: " + newDistance);
			}
			Delay.msDelay(50);
		}
		int rightBorderAngle = (int) robot.compassSensor.getDegrees();
		return rightBorderAngle;
	}

	private int scanLeft() {
		while ((distance = robot.ultrasonicSensor.getDistance()) == 255)
			;
		System.out.println(distance);
		while (isdetected) {
			robot.positionManager.rotate(3, Direction.LEFT);
			int newDistance = robot.ultrasonicSensor.getDistance();
			if (Math.abs(newDistance - distance) >= 5) {
				isdetected = checkReallyOutOfRange();
				System.out.println("Out: " + newDistance);
			}
			Delay.msDelay(50);
		}
		int leftBorderAngle = (int) robot.compassSensor.getDegrees();
		robot.positionManager.rotateTo(startAngle);
		isdetected = true;
		return leftBorderAngle;
	}
	
	private boolean checkReallyOutOfRange() {
		int out = 0;
		for (int i = 0; i < 10; ++i) {
			int newDistance = robot.ultrasonicSensor.getDistance();
			if (Math.abs(newDistance - distance) >= 5)
				++out;
		}
		if (out > 2)
			return false;
		else
			return true;
			
	}

}
