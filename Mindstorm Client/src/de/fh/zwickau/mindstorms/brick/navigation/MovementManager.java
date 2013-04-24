package de.fh.zwickau.mindstorms.brick.navigation;

import de.fh.zwickau.mindstorms.brick.Robot;
import de.fh.zwickau.mindstorms.brick.util.Manager;

public class MovementManager implements Manager {

	/** value for translating motorcycles to a distance */
	double driveTranslation;
	/** the robot */
	Robot robot;
	/** direction to the beginning */
	private int startdegrees;
	/** default speed for stopping*/
	//TODO Initialize correctly; avoided NullPointer for now
	private int robotspeed = 0;
	/** whether the robot is currently moving */
	private boolean isMoving;
	private int tachoRight;

	public MovementManager(Robot robot) {
		this.robot = robot;
		this.driveTranslation = robot.driveTranslation;
	}

	public void move(int dist) {
		isMoving = true;
		startdegrees = (int) robot.compassSensor.getDegrees();
		robot.rightMotor.rotate((int) (dist * driveTranslation), true);
		robot.leftMotor.rotate((int) (dist * driveTranslation), false);
		tachoRight = robot.rightMotor.getTachoCount();
		// Thread check = new Thread(new Runnable() {
		//
		// @Override
		// public void run() {
		// while (driving) {
		// // if (Math.abs(startdegrees)
		// // - Math.abs(robot.compassSensor.getDegrees()) > 2) {
		// //
		// //
		// // }
		// }
		// }
		//
		// });
		// check.start();
		while (tachoRight + (dist * driveTranslation) == robot.rightMotor
				.getTachoCount()) {
			isMoving = false;
		}
	}

	@Override
	public void stop() {
		isMoving = false;
		robot.rightMotor.setSpeed(robotspeed);
		robot.leftMotor.setSpeed(robotspeed);

	}

	public boolean isMoving() {
		return isMoving;
	}

}