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
		this.movementManager = new MovementManager(robot);
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
		int right = (startdegrees + deg) % 360;
		int left = (startdegrees - deg) % 360;
		if (right <= left) {
			directionManager.rotateInDirection(right, Direction.RIGHT);
		}
		if (left < right) {
			directionManager.rotateInDirection(left, Direction.LEFT);
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

}