package de.fh.zwickau.mindstorms.brick.task;

import lejos.nxt.Button;
import lejos.robotics.objectdetection.Feature;
import lejos.robotics.objectdetection.FeatureDetector;
import lejos.robotics.objectdetection.FeatureListener;
import lejos.robotics.objectdetection.RangeFeatureDetector;
import de.fh.zwickau.mindstorms.brick.Robot;
import de.fh.zwickau.mindstorms.brick.navigation.Direction;

public class ObjectCentralisation implements FeatureListener {

	private Robot robot;
	private int startangle, distance;
	private float maxDistance = 50;
	private int interval = 50;

	public ObjectCentralisation(Robot robot) {
		this.robot = robot;
		new RangeFeatureDetector(robot.ultrasonicSensor, maxDistance, interval)
				.addListener(this);
		Button.ENTER.waitForPressAndRelease();
		//initialize scan
		startangle = robot.getDirection();
		distance=robot.ultrasonicSensor.getDistance();
		//scanning
		int left = scanLeft();
		int right = scanRight();
		int targetAngle = calcAngle(left, right);
		// rotate to target angle
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
		robot.positionManager.rotate(90, Direction.RIGHT);
		int right = robot.getDirection();
		robot.positionManager.rotateTo(startangle);
		return right;
	}

	private int scanLeft() {
		robot.positionManager.rotate(90, Direction.LEFT);
		int left = robot.getDirection();
		robot.positionManager.rotateTo(startangle);
		return left;
	}

	@Override
	public void featureDetected(Feature feature, FeatureDetector detector) {
		
		//TODO make detection more flexible
		
		if (5 > (feature.getRangeReading().getRange() - distance)) {
		} else {

		}
	}
}
