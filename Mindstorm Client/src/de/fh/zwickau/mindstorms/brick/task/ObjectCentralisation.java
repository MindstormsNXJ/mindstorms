package de.fh.zwickau.mindstorms.brick.task;

import lejos.nxt.Button;
import lejos.robotics.objectdetection.Feature;
import lejos.robotics.objectdetection.FeatureDetector;
import lejos.robotics.objectdetection.FeatureListener;
import lejos.robotics.objectdetection.RangeFeatureDetector;
import lejos.util.Delay;
import de.fh.zwickau.mindstorms.brick.Robot;
import de.fh.zwickau.mindstorms.brick.navigation.Direction;

public class ObjectCentralisation implements FeatureListener {

	private Robot robot;
	private int distance;
	private boolean isdetected=true;
	private float maxDistance = 50;
	private int interval = 50;

	public ObjectCentralisation(Robot robot) {
		this.robot = robot;
		new RangeFeatureDetector(robot.ultrasonicSensor, maxDistance, interval)
				.addListener(this);
		robot.positionManager.rotate(10,Direction.LEFT);
		Button.ENTER.waitForPressAndRelease();
		
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
		distance = robot.ultrasonicSensor.getDistance();
		while (isdetected) {
			robot.positionManager.rotate(3, Direction.LEFT);
		}
		while (!isdetected) {
			robot.positionManager.rotate(1, Direction.RIGHT);
		}
		return (int) robot.compassSensor.getDegrees();
	}

	private int scanLeft() {
		distance = robot.ultrasonicSensor.getDistance();
		while (isdetected) {
			robot.positionManager.rotate(3, Direction.RIGHT);
		}
		while (!isdetected) {
			robot.positionManager.rotate(1, Direction.LEFT);
		}
		return (int) robot.compassSensor.getDegrees();
	}

	@Override
	public void featureDetected(Feature feature, FeatureDetector detector) {
		if (5 > (feature.getRangeReading().getRange() - distance)) {
			isdetected = false;
		} else {
			isdetected = true;
		}
	}
}
