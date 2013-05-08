package de.fh.zwickau.mindstorms.server.navigation;

import javax.naming.OperationNotSupportedException;

import de.fh.zwickau.mindstorms.server.communication.ConnectionManager;
import lejos.robotics.mapping.LineMap;
import lejos.robotics.navigation.DestinationUnreachableException;
import lejos.robotics.navigation.Pose;
import lejos.robotics.navigation.Waypoint;
import lejos.robotics.pathfinding.DijkstraPathFinder;
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
	
	/**
	 * Initialises a PathFinder with the LineMap to use from now on.
	 * 
	 * @param map the map to use
	 * @param targetManager the target manager to update the calculated paths
	 * @param robotName the robot's friendly name
	 * @throws OperationNotSupportedException if the robot name is unequal to "Picker"
	 */
	public PathFinder(LineMap map, TargetManager targetManager, String robotName) throws OperationNotSupportedException {
		if (!robotName.equals("Picker"))
			throw new OperationNotSupportedException("The nextAction() method is designed for the \"Picker\" robot only by now");
		
		finder = new DijkstraPathFinder(map);
		if (finder instanceof DijkstraPathFinder)
			((DijkstraPathFinder) finder).lengthenLines(10);
		else if (finder instanceof ShortestPathFinder)
			((ShortestPathFinder) finder).lengthenLines(10);
		this.targetManager = targetManager;
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
		if (!targetManager.hasMoreWaypoints(robotName)) {
			//find new path - will happen if method is called for the first time and after the ball has been picked up
			Path path = null;
			if (!robotHasBall) {
				try {
					path = finder.findRoute(currentPose, targetManager.getBallWaypoint());
				} catch (DestinationUnreachableException e) {
					System.err.println("Error in finding path: destination is unreachable");
					return;
				}
			} else {
				try {
					path = finder.findRoute(currentPose, targetManager.getFinalTarget());
				} catch (DestinationUnreachableException e) {
					System.err.println("Error in finding path: destination is unreachable");
					return;
				}
			}
			System.out.println("Path calculated:\n" + path + "\n");
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
		int deltaDir = (int) Math.abs(targetDir - currentPose.getHeading());
		if (deltaDir > 2) {//turn robot
			manager.sendTurnCommand(targetDir);
			System.out.println("Sending turn command - degrees: " + targetDir);
		}
		else { //move robot forward
			int distanceToMove = (int) Math.sqrt(xDiv * xDiv + yDiv * yDiv);
			if (targetManager.isBallWaypoint(currentWaypoint) || targetManager.isFinalTarget(currentWaypoint)) {
				distanceToMove -= 20; //to leave enough distance for the pick and drop procedures
				if (distanceToMove < 0) {
					//TODO maybe we should move backwards, but that could cause trouble if a wall gets in the ultrasonic sensors range
//					manager.sendBackwardCommand(Math.abs(distanceToMove));
//					System.out.println("Sending backward command - distance: " + Math.abs(distanceToMove));
				} else {
					manager.sendForwardCommand(distanceToMove);
					System.out.println("Sending forward command - distance: " + distanceToMove);
				}
				Delay.msDelay(2000);
				if (targetManager.isBallWaypoint(currentWaypoint)) {
					manager.sendPickCommand();
					robotHasBall = true;
					System.out.println("Sending pick command");
				} else {
					manager.sendDropCommand();
					System.out.println("Sending drop command");
					Delay.msDelay(2000);
					manager.terminate();
					System.out.println("Sending terminate command");
				}
					
			} else {
				manager.sendForwardCommand(distanceToMove);
				System.out.println("Sending forward command - distance: " + distanceToMove);
			}
			targetManager.waypointReached(robotName);
		}
		System.out.println();
	}
	
}
