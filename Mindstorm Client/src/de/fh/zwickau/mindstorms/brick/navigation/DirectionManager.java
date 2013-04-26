package de.fh.zwickau.mindstorms.brick.navigation;

import lejos.util.Delay;
import de.fh.zwickau.mindstorms.brick.Robot;
import de.fh.zwickau.mindstorms.brick.util.Manager;

public class DirectionManager implements Manager{

	/** speed how fast the robot should rotate */
	int motSpeed = 200;
	/** robot which is rotating */
	private Robot robot;
	/** if the robot currently rotates */
	private boolean isrotating;
	/** the direction the robot stands at start */
	private int startDirection;
	/** the degree to rotate */
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
		robot.setModeRotate();
		isrotating = true;
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
				while (isrotating == true) {
					int directioner = (int) robot.compassSensor.getDegrees();
					int targetdirection = (startDirection + degrees)%360;
					if(targetdirection<0){
						targetdirection=360+targetdirection;
					}
					System.out.println(targetdirection);
					if (directioner ==targetdirection) {
						isrotating = false;
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
		return isrotating;
	}

	@Override
	public int stop() {
		robot.leftMotor.stop(true);
		robot.rightMotor.stop();
		isrotating = false;
		
		Delay.msDelay(1000);
		return degrees;
	}
	
	

}