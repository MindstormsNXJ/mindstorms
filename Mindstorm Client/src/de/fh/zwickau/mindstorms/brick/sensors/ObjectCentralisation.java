package de.fh.zwickau.mindstorms.brick.sensors;

import lejos.robotics.RangeFinder;
import lejos.robotics.objectdetection.Feature;
import lejos.robotics.objectdetection.FeatureDetector;
import lejos.robotics.objectdetection.FeatureListener;
import lejos.robotics.objectdetection.RangeFeatureDetector;
import de.fh.zwickau.mindstorms.brick.Robot;
import de.fh.zwickau.mindstorms.brick.navigation.Direction;

public class ObjectCentralisation implements FeatureListener {

	private Robot robot;
	private int distance;
	private boolean isdetected;

	public ObjectCentralisation(Robot robot) {
		this.robot = robot;
		new RangeFeatureDetector(
				robot.ultrasonicSensor, 50, 20).addListener(this);
		int targetAngel = calcAngel(scanLeft(), scanRight());
		System.out.println(targetAngel);
		robot.positionManager.rotateTo(targetAngel);
	}

	private int calcAngel(int leftAngel, int rightAngel) {
		int centralAngel = 0;
		centralAngel = (leftAngel + rightAngel) / 2;
		if (leftAngel > rightAngel) {
			centralAngel += 180;
		}
		return centralAngel;
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
		if (5 >(feature.getRangeReading().getRange()-distance)) {
			isdetected = false;
		} else {
			isdetected = true;
		}
	}
}
