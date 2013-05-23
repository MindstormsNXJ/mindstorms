package de.fh.zwickau.mindstorms.brick.sensors;

import ch.aplu.nxtsim.*;
import de.fh.zwickau.mindstorms.brick.Robot;
import de.fh.zwickau.mindstorms.brick.navigation.Direction;

public class SensorManager {
	
	private Robot robot;
	private boolean isWorking;
	private int distance;
	
	public SensorManager(Robot robot) {
		this.robot = robot;
		isWorking = false;
		System.out.println("moving forward");
		robot.rightMotor.forward();
		robot.leftMotor.forward();
		RangeFeatureDetector scanner = new RangeFeatureDetector(robot.ultrasonicSensor, 50, 10);
		scanner.addListener2(new MyFeatureListener());
	}
	
	class MyFeatureListener implements FeatureListener {

		@Override
		public void featureDetected(Feature feature, FeatureDetector detector) {
			distance = (int) feature.getRangeReading();
			if (!isWorking) {
				robot.rightMotor.stop();
				robot.leftMotor.stop();
				isWorking = true;
				System.out.println("Moving to target");
				moveToTarget();
				System.out.println("Target reached - scanning");
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						scan();
						isWorking = false;
					}
					
				}).start();
			}
		}
		
		private void moveToTarget() {
			robot.positionManager.move(20); //TODO problem 1: always 20cm?!
		}
		
		private void scan() {
			boolean change = false;
			int startDistance = distance;
			int startDirection = (int) robot.compassSensor.getDegrees();
			System.out.println("start direction: " + startDirection);
			while (!change) {
				robot.positionManager.rotate(2, Direction.LEFT);
				if (Math.abs(distance - startDistance) >= 5)
					change = true;
			}
			int leftBorder = (int) robot.compassSensor.getDegrees();
			System.out.println("left border: " + leftBorder);
			robot.positionManager.rotateTo(startDirection);
			change = false;
			while (!change) {
				robot.positionManager.rotate(2, Direction.RIGHT);
				if (Math.abs(distance - startDistance) >= 5)
					change = true;
			}
			int rightBorder = (int) robot.compassSensor.getDegrees();
			System.out.println("right direction: " + rightBorder);
			int middle = ((leftBorder + rightBorder) % 360) / 2;
			System.out.println("middle: " + middle);
//			Sound.beep();
			robot.positionManager.rotateTo(middle);
		}
		
	}
	
}