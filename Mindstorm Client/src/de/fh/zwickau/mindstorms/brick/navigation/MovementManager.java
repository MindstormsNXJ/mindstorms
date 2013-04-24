package de.fh.zwickau.mindstorms.brick.navigation;

import de.fh.zwickau.mindstorms.brick.Robot;
import de.fh.zwickau.mindstorms.brick.util.Manager;

public class MovementManager implements Manager {

	double driveTranslation;
	Robot robot;
	private float startdegrees;
	private int robotspeed;
	private boolean driving;

	public MovementManager(Robot robot) {
		this.robot = robot;
		this.driveTranslation = robot.driveTranslation;
	}

	public void move(int dist) {
		driving = true;
		startdegrees = robot.compassSensor.getDegrees();
		robot.rightMotor.rotate((int) (dist * driveTranslation));
		robot.leftMotor.rotate((int) (dist * driveTranslation));
		Thread check = new Thread(new Runnable() {

			@Override
			public void run() {
				while (driving) {
					if (Math.abs(startdegrees)
							- Math.abs(robot.compassSensor.getDegrees()) > 2) {

					}
				}
			}

		});
		check.start();
		try {
			check.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (robot.rightMotor.isMoving() & robot.leftMotor.isMoving() == false) {

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