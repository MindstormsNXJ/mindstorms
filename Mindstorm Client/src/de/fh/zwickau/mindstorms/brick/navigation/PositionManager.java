package de.fh.zwickau.mindstorms.brick.navigation;

import de.fh.zwickau.mindstorms.brick.Robot;
import de.fh.zwickau.mindstorms.brick.util.Manager;
import lejos.robotics.navigation.Pose;

public class PositionManager implements Manager {

	private Pose pose;
	private Robot robot;
	private DirectionManager directionManager;
	private MovementManager movementManager;

	public PositionManager(Pose pose, Robot robot,
			DirectionManager directionManager, MovementManager movementManager) {
		this.pose = pose;
		this.robot = robot;
		this.directionManager = directionManager;
		this.movementManager = movementManager;
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
			directionManager.rotateInDirection(right, direction.RIGHT);
		}
		if (left < right) {
			directionManager.rotateInDirection(left, direction.LEFT);
		}
	}
	
	public void rotate(int deg, direction direction){
		directionManager.rotateInDirection(deg, direction);
	}

	/**
	 * Rotate in ...steps in an direction
	 * 
	 * @param steps
	 * @param dir
	 */
	public void rotateStepwise(int steps, direction dir) {
		directionManager.rotateInDirection(steps * directionManager.getStepWide(), dir);
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