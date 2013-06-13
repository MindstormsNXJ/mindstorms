package de.fh.zwickau.mindstorms.server.navigation;

import java.util.HashMap;

import de.fh.zwickau.mindstorms.server.Server;

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
	
	private Server controller;
	private HashMap<String, Path> robotPaths; //assigns every robot name to it's current path
	private HashMap<String, Integer> currentRobotWaypointNumber; //assigns every robot name to it's current waypoint number
	private Waypoint ball;
	private Waypoint target;
	
	private static TargetManager instance;
	
	/**
	 * Initialises a new target manager.
	 */
	private TargetManager() {
		robotPaths = new HashMap<String, Path>();
		currentRobotWaypointNumber = new HashMap<String, Integer>();
		initTargets();
	}
	
	/**
	 * Returns the TargetManager instance for the current session.
	 */
	public synchronized static TargetManager getInstance() {
		if (instance == null)
			instance = new TargetManager();
		return instance;
	}
	
	/**
	 * Initialises the ball and the finish target. //TODO these targets should be stored in the mapper
	 */
	private void initTargets() {
		ball = new Waypoint(0, 81);
		target = new Waypoint(40, -30);
	}
	
	/**
	 * Adds another robot to the maps. Robots are identified by there bluetooth friendly names.
	 * 
	 * @param robotName the robot's friendly name
	 */
	public void addRobot(String robotName) {
		robotPaths.put(robotName, new Path());
		currentRobotWaypointNumber.put(robotName, 0);
	}
	
	/**
	 * Says whether a given waypoint is the ball or not.
	 * 
	 * @param waypoint the given waypoint
	 * @return true if the waypoint is the ball, false otherwise
	 */
	public boolean isBallWaypoint(Waypoint waypoint) {
		if ((int) waypoint.x == (int) ball.x && (int) waypoint.y == (int) ball.y)
			return true;
		return false;
	}
	
	/**
	 * Says whether a given waypoint is the final target or not.
	 * 
	 * @param waypoint the given waypoint
	 * @return true if the waypoint is the target, false otherwise
	 */
	public boolean isFinalTarget(Waypoint waypoint) {
		if ((int) waypoint.x == (int) target.x && (int) waypoint.y == (int) target.y)
			return true;
		return false;
	}
	
	/**
	 * Returns the waypoint where the ball can be found.
	 * 
	 * @return the ball's waypoint
	 */
	public Waypoint getBallWaypoint() {
		return ball;
	}
	
	/**
	 * Returns the waypoint where the final target can be found.
	 * 
	 * @return the final target's waypoint
	 */
	public Waypoint getFinalTarget() {
		return target;
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
	public Waypoint getCurrentWaypoint(String robotName) {
		return robotPaths.get(robotName).get(currentRobotWaypointNumber.get(robotName));
	}
	
	/**
	 * Returns the robot's path.
	 * 
	 * @param robotName the robots friendly name
	 * @return the targets as a path
	 */
	public Path getPath(String robotName) {
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
		controller.targetChanged(robotName); // notify view that new targets arrived
	}
		
	public void setController(Server controller) {
		this.controller = controller;
	}

	/**
	 * Removes a robot.
	 * 
	 * @param robotName the robot's friendly name
	 */
	public void removeRobot(String robotName) {
		Path currentPath = robotPaths.get(robotName);
		//remove remaining waypoints
		for (int i = currentRobotWaypointNumber.get(robotName); i < currentPath.size(); ++i)
			currentPath.remove(i);
		//remove robot from watched robots - it's path will remain in the map
		currentRobotWaypointNumber.remove(robotName);
		controller.targetChanged(robotName);
	}
	
}
