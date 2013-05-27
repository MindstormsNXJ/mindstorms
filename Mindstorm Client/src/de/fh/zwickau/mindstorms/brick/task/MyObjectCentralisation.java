package de.fh.zwickau.mindstorms.brick.task;

import lejos.util.Delay;
import de.fh.zwickau.mindstorms.brick.Robot;
import de.fh.zwickau.mindstorms.brick.navigation.Direction;

/**
 * This class is my proposal for object centralisation which should be done before picking and 
 * dropping the ball.
 * 
 * @author Tobias Schießl
 * @version 1.0
 */
public class MyObjectCentralisation {

	private Robot robot;
	private int distance;
	private boolean isdetected;
	private int startAngle;

	/**
	 * Initialises a new ObjectCentralisation and starts the centralisation process.
	 * At the end, the robot will have the target directly in front.
	 * 
	 * @param robot the robot with the sensors and position manager
	 * @throws IllegalStateException if there is no object to centralise on
	 */
	public MyObjectCentralisation(Robot robot) throws IllegalStateException {
		this.robot = robot;
		startAngle = (int) robot.compassSensor.getDegrees();
		initDistance();
		isdetected = true;
		int leftBorder = scanLeft();
		reset();
		int rightBorder = scanRight();
		int targetAngle = calcAngle(leftBorder, rightBorder);
		System.out.println(targetAngle);
		robot.positionManager.rotateTo(targetAngle);
	}
	
	/**
	 * Tries to initialise the distance to the target.
	 * Note, that if the distance is still 255 after 10 seconds, an IllegalStateException will be thrown.
	 * 
	 * @throws IllegalStateException if no object could be detected in front of the robot
	 */
	private void initDistance() throws IllegalStateException {
		long startTime = System.currentTimeMillis();
		while ((distance = robot.ultrasonicSensor.getDistance()) == 255) {
			if (System.currentTimeMillis() - startTime >= 10000) //10 seconds
				throw new IllegalStateException("Nothing to centralize on");
			Delay.msDelay(50);
		}
		System.out.println(distance);
	}
	
	/**
	 * Resets the robot to it's initial position and set's the isDetected flag to true.
	 */
	private void reset() {
		robot.positionManager.rotateTo(startAngle);
		isdetected = true;
	}

	/**
	 * Calculates the target angle from the left and right border.
	 * 
	 * @param leftAngle the left border angle
	 * @param rightAngle the right border angle
	 * @return the target angle
	 */
	private int calcAngle(int leftAngle, int rightAngle) {
		int centralAngle = 0;
		centralAngle = (leftAngle + rightAngle) / 2;
		if (leftAngle > rightAngle) 
			centralAngle += 180;
		return centralAngle;
	}

	/**
	 * Finds the right border angle of the object.
	 * 
	 * @return the right border angle.
	 */
	private int scanRight() {
		while (isdetected) {
			robot.positionManager.rotate(3, Direction.RIGHT);
			int newDistance = robot.ultrasonicSensor.getDistance();
			if (Math.abs(newDistance - distance) >= 5) 
				isdetected = checkReallyOutOfRange();
			Delay.msDelay(50);
		}
		Delay.msDelay(5000); //to stop the vibration of the compass sensor
		int rightBorderAngle = (int) robot.compassSensor.getDegrees();
		return rightBorderAngle;
	}

	/**
	 * Finds the left border angle of the object.
	 * 
	 * @return the left border angle
	 */
	private int scanLeft() {
		while (isdetected) {
			robot.positionManager.rotate(3, Direction.LEFT);
			int newDistance = robot.ultrasonicSensor.getDistance();
			if (Math.abs(newDistance - distance) >= 5) 
				isdetected = checkReallyOutOfRange();
			Delay.msDelay(50);
		}
		Delay.msDelay(5000); //to stop the vibration of the compass sensor
		int leftBorderAngle = (int) robot.compassSensor.getDegrees();
		return leftBorderAngle;
	}
	
	/**
	 * Checks, if the object is really out of the ultrasonic sensors range.
	 * Therefore, the robot will stop and take 10 values, from whom 5 must be out of range.
	 * 
	 * @return false, if the object is no longer in the ultrasonic sensors range
	 */
	private boolean checkReallyOutOfRange() {
		int out = 0;
		for (int i = 0; i < 10; ++i) {
			int newDistance = robot.ultrasonicSensor.getDistance();
			if (Math.abs(newDistance - distance) >= 5)
				++out;
		}
		if (out > 4)
			return false;
		else
			return true;
	}

}
