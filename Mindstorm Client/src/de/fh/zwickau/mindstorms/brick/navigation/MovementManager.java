package de.fh.zwickau.mindstorms.brick.navigation;

import lejos.util.Delay;
import de.fh.zwickau.mindstorms.brick.Robot;
import de.fh.zwickau.mindstorms.brick.util.Manager;

public class MovementManager implements Manager {

	double driveTranslation;
	Robot robot;
	private int startdegrees;
	private int robotspeed;
	private boolean driving;
	private int tachoRight;

	public MovementManager(Robot robot) {
		this.robot = robot;
		this.driveTranslation = robot.driveTranslation;
	}

	public void move(int dist) {
		driving = true;
		startdegrees = (int) robot.compassSensor.getDegrees();
		robot.rightMotor.rotate((int) (dist * driveTranslation),true);
		robot.leftMotor.rotate((int) (dist * driveTranslation),false);
		tachoRight=robot.rightMotor.getTachoCount();
//		Thread check = new Thread(new Runnable() {
//
//			@Override
//			public void run() {
//				while (driving) {
////					if (Math.abs(startdegrees)
////							- Math.abs(robot.compassSensor.getDegrees()) > 2) {
////
////						
////					}
//				}
//			}
//
//		});
//		check.start();
		while(tachoRight+(dist * driveTranslation)==robot.rightMotor.getTachoCount()) {

			
			driving=false;
		}
		
	}

	@Override
	public void stop() {
		driving = false;
		robot.rightMotor.setSpeed(robotspeed);
		robot.leftMotor.setSpeed(robotspeed);

	}

	public boolean isMoving() {
		return driving;
	}

}