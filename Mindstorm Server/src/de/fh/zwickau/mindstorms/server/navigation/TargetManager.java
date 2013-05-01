package de.fh.zwickau.mindstorms.server.navigation;

import java.util.ArrayList;

import lejos.geom.Point;

/**
 * This class is responsible for managing the target points the robot 
 * should move to.
 * 
 * @author Tobias Schießl
 * @version 1.0
 */
public class TargetManager {

	private ArrayList<Point> targets;
	private int currentTargetNumber;
	
	/**
	 * Initialises a new target manager.
	 */
	public TargetManager() {
		targets = new ArrayList<Point>();
		currentTargetNumber = 0;
		initTargets();
	}
	
	/**
	 * Initialises all targets, which are represented by Points.
	 * Currently, there are only two targets: the ball and the final target.
	 */
	private void initTargets() {
		Point ball = new Point(10, 10);
		targets.add(ball);
		Point target = new Point(0, 0);
		targets.add(target);
	}
	
	/**
	 * Says whether there are more targets or not.
	 * 
	 * @return true if there are no more targets
	 */
	public boolean hasMoreTargets() {
		return currentTargetNumber == targets.size();
	}
	
	/**
	 * Returns the current target as a point. Note, that this method 
	 * will return the same target until targetReached is called.
	 * 
	 * @return the target point
	 */
	public Point getCurrentTarget() {
		return targets.get(currentTargetNumber);
	}
	
	/**
	 * Method that has to be called once a target is reached. getCurrentTarget()
	 * will return the next one after this call, as far as there are more.
	 */
	public void targetReached() {
		++currentTargetNumber;
	}
	
}
