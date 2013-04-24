package de.fh.zwickau.mindstorms.brick.navigation;

import de.fh.zwickau.mindstorms.brick.Robot;

public class DirectionManager {

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
	 * @return 
	 */
	public DirectionManager (Robot robot) {
		this.robot=robot;
	}

	/**
	 * rotate to an angle 
	 * @param deg
	 */
	public void rotate(int deg) {
		int startdegrees=(int) robot.compassSensor.getDegrees();
		int right = (startdegrees+deg)%360;
		int left  = (startdegrees-deg)%360;
		if (right<=left){
			rotateInDirection(right, direction.RIGHT);
		}
		if (left<right){
			rotateInDirection(left, direction.LEFT);
		}
	}
	/**
	 * rotate with an angle in a specific direction
	 * @param deg
	 * @param dir
	 */
	public void rotateInDirection(int deg, direction dir) {
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
	 * Rotate in ...steps in an direction
	 * @param steps
	 * @param dir
	 */
	public void rotateStepwise(int steps, direction dir){
		rotateInDirection(steps*stepWide, dir);
		
	}
	
	public void setStepWide(int stepWide) {
		this.stepWide = stepWide;
	}
	
}