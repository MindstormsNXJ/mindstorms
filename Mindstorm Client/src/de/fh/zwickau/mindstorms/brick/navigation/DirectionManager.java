package de.fh.zwickau.mindstorms.brick.navigation;

import lejos.util.Delay;
import de.fh.zwickau.mindstorms.brick.Robot;
import de.fh.zwickau.mindstorms.brick.util.Manager;

public class DirectionManager implements Manager{

	int motSpeed = 200;// Speed how fast the Robot should Rotate
	private Robot robot;// the Robot who is to rotate
	private boolean rotate;// boolean if robot is rotating
	private int startDirection;// the direction where the robot stands at
									// start
	private int directioner;// the direction where the Robot should move
	private int degrees;// the dergree what to move
	private int stepWide = 45;// the stepwide for stepwise rotating

	/**
	 * 
	 * @param robot
	 * @return
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
		rotate = true;
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
		Thread check = new Thread(new Runnable() {

			@Override
			public void run() {
				while (rotate == true) {
					directioner = (int) robot.compassSensor.getDegrees();
					if (directioner ==( startDirection + degrees) % 360) {
						rotate = false;
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

	public void setStepWide(int stepWide) {
		this.stepWide = stepWide;
	}

	public int getStepWide() {
		return stepWide;
	}

	public boolean isRotating() {
		return rotate;
	}

	@Override
	public void stop() {
		robot.leftMotor.stop();
		robot.rightMotor.stop();
		rotate = false;
	}

}