package de.fh.zwickau.mindstorms.server.navigation;

import java.util.HashMap;

import de.fh.zwickau.mindstorms.server.view.View;

import lejos.geom.Point;
import lejos.robotics.navigation.Waypoint;
import lejos.robotics.pathfinding.Path;

/**
 * This class is responsible for managing the target points the robot should move to.
 * Targets are the real physical targets (ball and finish) as well as the waypoints which where calculated by the path finder.
 * 
 * @author Tobias Schießl, Patrick Rosenkranz
 * @version 2.0
 */
public class TargetManager {
	
	private View observer;
	private HashMap<String, Path> robotPaths;
	private HashMap<String, Integer> currentRobotWaypointNumber;
	private Waypoint ball;
	private Waypoint target;
	
	/**
	 * Initialises a new target manager.
	 */
	public TargetManager() {
		robotPaths = new HashMap<String, Path>();
		currentRobotWaypointNumber = new HashMap<String, Integer>();
		initPickerRobot();
		initTargets();
	}
	
	/**
	 * Initialises the ArrayList and target number for the robot "Picker".
	 */
	private void initPickerRobot() {
		robotPaths.put("Picker", new Path());
		currentRobotWaypointNumber.put("Picker", 0);
	}
	
	/**
	 * Initialises the ball and the finish target.
	 */
	private void initTargets() {
		ball = new Waypoint(10, 10);
		target = new Waypoint(-10, -10);
	}
	
	private boolean isBallWaypoint(Waypoint waypoint) {
		if ((int) waypoint.x == (int) ball.x && (int) waypoint.y == (int) ball.y)
			return true;
		return false;
	}
	
	private boolean isFinalTarget(Waypoint waypoint) {
		if ((int) waypoint.x == (int) target.x && (int) waypoint.y == (int) target.y)
			return true;
		return false;
	}
	
	/**
	 * Says whether there are more waypoints or not.
	 * 
	 * @param robotName the robots friendly name
	 * @return true if there are more waypoints, false otherwise
	 */
	public boolean hasMoreWaypoints(String robotName) {
		return !(currentRobotWaypointNumber.get(robotName) == robotPaths.get(robotName).size());
	}
	
	/**
	 * Returns the current target as a point. Note, that this method 
	 * will return the same target until targetReached is called.
	 * 
	 * @param robotName the robots friendly name
	 * @return the target point
	 */
	public Point getCurrentWaypoint(String robotName) {
		return robotPaths.get(robotName).get(currentRobotWaypointNumber.get(robotName));
	}
	
	/**
	 * Returns all known Targets
	 * 
	 * @param robotName the robots friendly name
	 * @return the targets as a path
	 */
	public Path getWaypoint(String robotName) {
		return robotPaths.get(robotName);
	}
	
	/**
	 * Method that has to be called once a target is reached. getCurrentTarget()
	 * will return the next one after this call, as far as there are more.
	 * 
	 * @param robotName the robot's friendly name
	 */
	public void waypointReached(String robotName) {
		currentRobotWaypointNumber.put(robotName, currentRobotWaypointNumber.get(robotName) + 1);
	}
	
	/**
	 * Sets a new path for the specified robot. The remaining waypoints of the old path will be removed and the fiven path
	 * appended to the already reached waypoints.
	 * 
	 * @param path the path to append
	 * @param robotName the robot's friendly name
	 */
	public void setNewPath(Path path, String robotName) {
		Path currentPath = robotPaths.get(robotName);
		//remove old waypoints
		for (int i = currentRobotWaypointNumber.get(robotName); i < currentPath.size(); ++i)
			currentPath.remove(i);
		//add new path
		currentPath.addAll(path);
		robotPaths.put(robotName, currentPath);
		observer.targetChanged(); // notify view that new targets arrived
	}
		
	public void setObserverView(View observer) {
		this.observer = observer;
	}
	
}
