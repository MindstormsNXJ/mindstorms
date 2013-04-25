package de.fh.zwickau.mindstorms.brick.navigation;

import lejos.util.Delay;
import de.fh.zwickau.mindstorms.brick.Robot;
import de.fh.zwickau.mindstorms.brick.util.Manager;

public class DirectionManager implements Manager {

	/** speed how fast the robot should rotate */
	int motSpeed = 200;
	/** robot which is rotating */
	private Robot robot;
	/** robot is rotating */
	private boolean isRotating;
	/** the direction the robot stands at start */
	private int startDirection;
	/** the degree to rotate */
	private int degrees;

	/**
	 * Constructor for the DirectionManager
	 * 
	 * @param robot
	 */
	public DirectionManager(Robot robot) {
		this.robot = robot;
	}

	/**
	 * rotate with an angle in a specific direction
	 * 
	 * @param deg
	 * @param dir
	 */
	public void rotateInDirection(int deg, Direction dir) {
		robot.setModeRotate();
		isRotating = true;
		startDirection = (int) robot.compassSensor.getDegrees();
		if (dir == Direction.RIGHT) {
			robot.leftMotor.forward();
			robot.rightMotor.backward();
			degrees = deg;
		}
		if (dir == Direction.LEFT) {
			robot.leftMotor.backward();
			robot.rightMotor.forward();
			degrees = -deg;
		}
		System.out.println(degrees);
		Thread check = new Thread(new Runnable() {

			@Override
			public void run() {
				while (isRotating == true) {
					int directioner = (int) robot.compassSensor.getDegrees();
					if (directioner == (startDirection + degrees) % 360) {
						isRotating = false;
					}
				}

				stop();
			}

		});
		check.start();
		try {
			check.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public boolean isRotating() {
		return isRotating;
	}

	@Override
	public void stop() {
		robot.leftMotor.stop(true);
		robot.rightMotor.stop();
		isRotating = false;
		System.out.println((int) robot.compassSensor.getDegrees());
		Delay.msDelay(1000);
	}

}