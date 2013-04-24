package de.fh.zwickau.mindstorms.brick.sensors;

import lejos.nxt.Sound;
import lejos.robotics.objectdetection.Feature;
import lejos.robotics.objectdetection.FeatureDetector;
import lejos.robotics.objectdetection.FeatureListener;
import lejos.robotics.objectdetection.RangeFeatureDetector;
import de.fh.zwickau.mindstorms.brick.Robot;
import de.fh.zwickau.mindstorms.brick.navigation.Direction;

public class SensorManager {
	
	private Robot robot;
	private boolean isWorking;
	private int distance;
	
	public SensorManager(Robot robot) {
		this.robot = robot;
		isWorking = false;
		FeatureDetector scanner = new RangeFeatureDetector(robot.ultrasonicSensor, 50, 10);
		scanner.addListener(new MyFeatureListener());
	}
	
	private class MyFeatureListener implements FeatureListener {

		@Override
		public void featureDetected(Feature feature, FeatureDetector detector) {
			distance = (int) feature.getRangeReading().getRange();
			if (!isWorking) {
				isWorking = true;
				moveToTarget();
				scan();
				isWorking = false;
			}
		}
		
		private void moveToTarget() {
			robot.positionManager.move(20); //TODO problem 1: always 20cm?!
		}
		
		private void scan() {
			boolean change = false;
			int startDistance = distance;
			int startDirection = (int) robot.compassSensor.getDegrees();
			while (!change) {
				robot.positionManager.rotate(2, Direction.LEFT);
				if (Math.abs(distance - startDistance) >= 5)
					change = true;
			}
			int leftBorder = (int) robot.compassSensor.getDegrees();
			robot.positionManager.rotateTo(startDirection);
			change = false;
			while (!change) {
				robot.positionManager.rotate(2, Direction.RIGHT);
				if (Math.abs(distance - startDistance) >= 5)
					change = true;
			}
			int rightBorder = (int) robot.compassSensor.getDegrees();
			int middle = ((leftBorder + rightBorder) % 360) / 2;
			System.out.println("middle: " + middle);
			Sound.beep();
			robot.positionManager.rotateTo(middle);
		}
		
	}
	
}