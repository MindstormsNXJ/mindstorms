package de.fh.zwickau.mindstorms.brick.task;

import de.fh.zwickau.mindstorms.brick.Robot;
import de.fh.zwickau.mindstorms.brick.navigation.direction;

public class Rotate {

	int motSpeed = 200;//Speed how fast the Robot should Rotate
	private Robot robot;//the Robot who is to rotate
	private boolean rotate;//boolean if robot is rotating
	private float startDirection;//the direction where the robot stands at start
	private float directioner;//the direction where the Robot should move
	private float degrees;//the dergree what to move
	private int stepWide=45;//the stepwide for stepwise rotating


	/**
	 * 
	 * @param robot
	 */
	public Rotate(Robot robot) {
		robot.leftMotor.setSpeed(motSpeed);
		robot.leftMotor.setSpeed(motSpeed);
	}

	/**
	 * 
	 * @param deg
	 * @param dir
	 */
	public void rotate(int deg, direction dir) {
		rotate=true;
		startDirection = robot.compassSensor.getDegrees();
		if(dir==direction.RIGHT){
			robot.leftMotor.forward();
			robot.rightMotor.backward();
			degrees=deg;
		}
		if(dir==direction.LEFT){
			robot.leftMotor.forward();
			robot.rightMotor.backward();
			degrees=-deg;
		}
		Thread check = new Thread(new Runnable() {


			@Override
			public void run() {
				while(rotate==true){
				directioner=robot.compassSensor.getDegrees();
				if(directioner==startDirection+degrees%360){
					rotate=false;
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
	}
	
	
	/**
	 * 
	 * @param steps
	 * @param dir
	 */
	public void rotateStepwise(int steps, direction dir){
		rotate(steps*stepWide, dir);
		
	}
	
	public void setMotSpeed(int motSpeed) {
		this.motSpeed = motSpeed;
		robot.leftMotor.setSpeed(this.motSpeed);
		robot.leftMotor.setSpeed(this.motSpeed);
	}
	public void setStepWide(int stepWide) {
		this.stepWide = stepWide;
	}
}