package de.fh.zwickau.mindstorms.brick.task;

import lejos.util.Delay;
import de.fh.zwickau.mindstorms.brick.Robot;
import de.fh.zwickau.mindstorms.brick.navigation.Direction;

public class MyObjectCentralisation {

	private Robot robot;
	private int distance;
	private boolean isdetected = true;
	private int startAngle;

	public MyObjectCentralisation(Robot robot) throws IllegalStateException {
		this.robot = robot;
		startAngle = (int) robot.compassSensor.getDegrees();
		initDistance();
		int leftBorder = scanLeft();
		reset();
		int rightBorder = scanRight();
		int targetAngle = calcAngle(leftBorder, rightBorder);
		System.out.println(targetAngle);
		robot.positionManager.rotateTo(targetAngle);
	}
	
	private void initDistance() throws IllegalStateException {
		long startTime = System.currentTimeMillis();
		while ((distance = robot.ultrasonicSensor.getDistance()) == 255) {
			if (System.currentTimeMillis() - startTime >= 10000) //10 seconds
				throw new IllegalStateException("Nothing to centralize on");
			Delay.msDelay(50);
		}
		System.out.println(distance);
	}
	
	private void reset() {
		robot.positionManager.rotateTo(startAngle);
		isdetected = true;
	}

	private int calcAngle(int leftAngle, int rightAngle) {
		int centralAngle = 0;
		centralAngle = (leftAngle + rightAngle) / 2;
		if (leftAngle > rightAngle) 
			centralAngle += 180;
		return centralAngle;
	}

	private int scanRight() {
		while (isdetected) {
			robot.positionManager.rotate(3, Direction.RIGHT);
			int newDistance = robot.ultrasonicSensor.getDistance();
			if (Math.abs(newDistance - distance) >= 5) 
				isdetected = checkReallyOutOfRange();
			Delay.msDelay(50);
		}
		Delay.msDelay(3000); //to stop the vibration of the compass sensor
		int rightBorderAngle = (int) robot.compassSensor.getDegrees();
		return rightBorderAngle;
	}

	private int scanLeft() {
		while (isdetected) {
			robot.positionManager.rotate(3, Direction.LEFT);
			int newDistance = robot.ultrasonicSensor.getDistance();
			if (Math.abs(newDistance - distance) >= 5) 
				isdetected = checkReallyOutOfRange();
			Delay.msDelay(50);
		}
		Delay.msDelay(3000); //to stop the vibration of the compass sensor
		int leftBorderAngle = (int) robot.compassSensor.getDegrees();
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
