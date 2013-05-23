package de.fh.zwickau.mindstorms.server.navigation;

import javax.naming.OperationNotSupportedException;

import de.fh.zwickau.mindstorms.server.communication.ConnectionManager;
import lejos.robotics.mapping.LineMap;
import lejos.robotics.navigation.DestinationUnreachableException;
import lejos.robotics.navigation.Pose;
import lejos.robotics.navigation.Waypoint;
import lejos.robotics.pathfinding.Path;
import lejos.robotics.pathfinding.ShortestPathFinder;
import lejos.util.Delay;

/**
 * This PathFinder class decides which command has to be send next by
 * finding to path to it's current target using a Dijkstra algorithm.
 * 
 * @author Tobias Schießl
 * @version 1.3
 */
public class PathFinder {
	
	private lejos.robotics.pathfinding.PathFinder finder;
	private TargetManager targetManager;
	private boolean robotHasBall = false;
	private String robotName;
	private boolean mapChanged = true;
	
	//two values which will be used for the line map converting process
	public static final int ROBOT_LENGTH_CM = 6;
	public static final int ROBOT_WIDTH_CM = 4;
	
	/**
	 * Initialises a PathFinder with the LineMap to use from now on.
	 * 
	 * @param map the map to use
	 * @param robotName the robot's friendly name
	 * @throws OperationNotSupportedException if the robot name is unequal to "Picker"
	 */
	public PathFinder(LineMap map, String robotName) {
		finder = new ShortestPathFinder(map);
//		((ShortestPathFinder) finder).lengthenLines((float) 0.01); //not necessary I think
		this.targetManager = TargetManager.getInstance();
		this.robotName = robotName;
	}
	
	/**
	 * Finds the next action to perform and tells the ConnectionManager
	 * to send the right command.
	 * 
	 * @param currentPose the robot's current pose
	 * @param manager the ConnectionManager which will send the command
	 * @param robotName the robot's friendly name
	 */
	public void nextAction(Pose currentPose, ConnectionManager manager) {
		if (mapChanged || !targetManager.hasMoreWaypoints(robotName)) { //calculate (new) path
			Path path = calculatePath(currentPose);
			if (path == null)
					return;
			System.out.println("Path calculated:\n" + path + "\n");
			mapChanged = false;
			targetManager.setNewPath(path, robotName);
			targetManager.waypointReached(robotName); //necessary because the first waypoint of the found path will be the current pose
		}
		
		System.out.println("current pose: " + currentPose);
		Waypoint currentWaypoint = targetManager.getCurrentWaypoint(robotName);
		System.out.println("current target: " + currentWaypoint);
		
		int xDiv = (int) (currentWaypoint.x - currentPose.getX());
		int yDiv = (int) (currentWaypoint.y - currentPose.getY());
		int targetDir;
		if (yDiv != 0 && xDiv != 0) {
			targetDir = (int) Math.toDegrees(Math.atan((double) xDiv/ (double) yDiv));
			if (yDiv > 0 && targetDir < 0) {
				targetDir = 360 + targetDir;
			} else if (yDiv < 0) {
				targetDir = 180 + targetDir;
			}
		}
		else {
			if (yDiv == 0) {
				if (currentWaypoint.x > currentPose.getX())
					targetDir = 90;
				else
					targetDir = 270;
			} else {
				if (currentWaypoint.y > currentPose.getY())
					targetDir = 0;
				else
					targetDir = 180;
			}
		}
		int distanceToMove = (int) (Math.sqrt(xDiv * xDiv + yDiv * yDiv) * 10);
		if (distanceToMove == 0) {
			//just a bug that happens due to calculation with float in the path finder - use next waypoint
			System.out.println("Skipping target...");
			targetManager.waypointReached(robotName);
			nextAction(currentPose, manager);
		} else {
			int deltaDir = (int) Math.abs(targetDir - currentPose.getHeading());
			if (deltaDir > 2) {//turn robot
				manager.sendTurnCommand(targetDir);
				System.out.println("Sending turn command - degrees: " + targetDir);
			}
			else { //move robot forward
				if (targetManager.isBallWaypoint(currentWaypoint) || (robotHasBall && targetManager.isFinalTarget(currentWaypoint))) {
					distanceToMove -= 300; //to leave enough distance for the pick and drop procedures
//					if (distanceToMove < 0) {
//						//TODO maybe we should move backwards, but that could cause trouble if a wall gets in the ultrasonic sensors range
////						manager.sendBackwardCommand(Math.abs(distanceToMove));
////						System.out.println("Sending backward command - distance: " + Math.abs(distanceToMove));
//					} else {
//						manager.sendForwardCommand(distanceToMove);
//						System.out.println("Sending forward command - distance: " + distanceToMove);
//					}
//					Delay.msDelay(2000);
					if (distanceToMove < 0)
						distanceToMove = 0;
					if (targetManager.isBallWaypoint(currentWaypoint)) {
						manager.sendPickCommand(distanceToMove);
						robotHasBall = true;
						System.out.println("Sending pick command - distance: " + distanceToMove);
						targetManager.waypointReached(robotName);
					} else {
						manager.sendDropCommand(distanceToMove);
						System.out.println("Sending drop command - distance: " + distanceToMove);
						Delay.msDelay(2000);
						manager.terminate();
						System.out.println("Sending terminate command");
					}
				} else {
					manager.sendForwardCommand(distanceToMove);
					System.out.println("Sending forward command - distance: " + distanceToMove);
					targetManager.waypointReached(robotName);
				}
			}
			System.out.println();
		}
	}
	
	/**
	 * Calculates the path to ball or target from the current pose on.
	 * 
	 * @param currentPose the robot's current pose
	 * @return the calculated path or null if destination is unreachable
	 */
	private Path calculatePath(Pose currentPose) {
		Path path = null;
		if (!robotHasBall) {
			try {
				path = finder.findRoute(currentPose, targetManager.getBallWaypoint());
			} catch (DestinationUnreachableException e) {
				System.err.println("Error in finding path: destination is unreachable");
			}
		} else {
			try {
				path = finder.findRoute(currentPose, targetManager.getFinalTarget());
			} catch (DestinationUnreachableException e) {
				System.err.println("Error in finding path: destination is unreachable");
			}
		}
		return path;
	}
	
	/**
	 * Should be called once the map changes.
	 */
	public void mapChanged(LineMap newMap) {
		mapChanged = true;
		((ShortestPathFinder) finder).setMap(newMap);
	}
	
}
