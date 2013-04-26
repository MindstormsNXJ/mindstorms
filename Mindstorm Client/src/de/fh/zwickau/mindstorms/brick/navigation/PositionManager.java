package de.fh.zwickau.mindstorms.brick.navigation;

import de.fh.zwickau.mindstorms.brick.Robot;
import de.fh.zwickau.mindstorms.brick.util.Manager;
import lejos.robotics.navigation.Pose;

public class PositionManager implements Manager {

	private Pose pose;
	private Robot robot;
	private DirectionManager directionManager;
	private MovementManager movementManager;

	public PositionManager(Pose pose, Robot robot) {
		this.pose = pose;
		this.robot = robot;
		this.directionManager = new DirectionManager(robot);
		this.movementManager = new MovementManager(robot,this);
		robot.positionManager = this;
	}

	public Pose getPose() {
		return pose;
	}
	
	public boolean isPositioning(){
		return directionManager.isRotating() || movementManager.isMoving();
	}

	/**
	 * rotate to an angle
	 * 
	 * @param deg
	 */
	public void rotateTo(int deg) {
		int startdegrees = (int) robot.compassSensor.getDegrees();
		int toRotate=Math.abs(angelCorrection(startdegrees, deg));
		if (angelCorrection(startdegrees, deg)<=0) {
			directionManager.rotateInDirection(toRotate, Direction.LEFT);
		}
		if ((angelCorrection(startdegrees, deg))>0) {
			directionManager.rotateInDirection(toRotate, Direction.RIGHT);
		}
	}
	
	public void rotate(int degree, Direction direction){
		directionManager.rotateInDirection(degree, direction);
	}

	/**
	 * Rotate in ...steps in an direction
	 * 
	 * @param steps
	 * @param direction
	 * @param stepWide, default stepWide should be 45
	 */
	public void rotateStepwise(int steps, Direction direction, int stepWide) {
		directionManager.rotateInDirection(steps * stepWide, direction);
	}
	
	public void move(int distance){
		movementManager.move(distance);
	}

	@Override
	public void stop() {
		movementManager.stop();
		directionManager.stop();
	}

	int angelCorrection(int currentDegree,int newDegree){
		int c =newDegree-currentDegree;
		if(c>=180){
			c=c-360;
		}
		if(c<-180){
			c=c+360;
		}
		return c;
		
	}
}