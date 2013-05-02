package de.fh.zwickau.mindstorms.server.navigation;

import de.fh.zwickau.mindstorms.server.communication.ConnectionManager;
import lejos.robotics.mapping.LineMap;
import lejos.robotics.navigation.DestinationUnreachableException;
import lejos.robotics.navigation.Pose;
import lejos.robotics.navigation.Waypoint;
import lejos.robotics.pathfinding.DijkstraPathFinder;
import lejos.robotics.pathfinding.Path;
import lejos.robotics.pathfinding.ShortestPathFinder;

/**
 * This PathFinder class decides which command has to be send next by
 * finding to path to it's current target using a Dijkstra algorithm.
 * 
 * @author Tobias Schießl
 * @version 1.1
 */
public class PathFinder {
	
	private lejos.robotics.pathfinding.PathFinder finder;
	private TargetManager targetManager;
	private boolean robotHasBall = false;
	
	/**
	 * Initialises a PathFinder with the LineMap to use from now on.
	 * 
	 * @param map the map to use
	 * @param targetManager the targetManager to update the current target
	 */
	public PathFinder(LineMap map, TargetManager targetManager) {
		finder = new DijkstraPathFinder(map);
//		finder = new ShortestPathFinder(map);
		this.targetManager = targetManager;
	}
	
	/**
	 * Finds the next action to perform and tells the ConnectionManager
	 * to send the right command.
	 * 
	 * @param currentPose the robot's current pose
	 * @param manager the ConnectionManager which will send the command
	 * @throws DestinationUnreachableException if the destination is unreachable
	 */
	public void nextAction(Pose currentPose, ConnectionManager manager) {
		Waypoint currentTarget = new Waypoint(targetManager.getCurrentTarget());
		Path path = null;
		try {
			path = finder.findRoute(currentPose, currentTarget);
		} catch (DestinationUnreachableException e) {
			System.err.println("Error in finding path: destination is unreachable");
			return;
		}
		
		for (int i = 0; i < path.size(); i++)
			System.out.println("Waypoint" + i + ": (" + path.get(i).x + ", " + path.get(i).y + ")");
		
		if (path.size() == 1) {
			System.out.println("Current target has been reached");
			targetManager.targetReached();
			if (!robotHasBall) {
				robotHasBall = true;
//				manager.sendPickCommand();
				System.out.println("Sending pick command");
			} else {
//				manager.sendDropCommand();
				System.out.println("Sending drop command");
//				manager.terminate();
				System.out.println("Terminating server");
			}
			System.out.println();
			return;
		}
		Waypoint nextWaypoint = path.get(1); //0 is the current position
		System.out.println("Next Waypoint: (" + nextWaypoint.x + ", " + nextWaypoint.y + ")");
		int xDiv = (int) (nextWaypoint.x - currentPose.getX());
		int yDiv = (int) (nextWaypoint.y - currentPose.getY());
		int targetDir;
		if (yDiv != 0 && xDiv != 0) {
			targetDir = (int) Math.toDegrees(Math.atan(xDiv/yDiv));
			if (yDiv > 0 && targetDir < 0) {
				targetDir = 360 + targetDir;
			} else {
				targetDir = 180 + targetDir;
			}
		}
		else {
			if (yDiv == 0) {
				if (nextWaypoint.x > currentPose.getX())
					targetDir = 90;
				else
					targetDir = 270;
			} else {
				if (nextWaypoint.y > currentPose.getY())
					targetDir = 0;
				else
					targetDir = 180;
			}
		}
		int deltaDir = (int) Math.abs(targetDir - currentPose.getHeading());
		if (deltaDir > 2) {//turn robot
			if (targetDir > currentPose.getHeading()) {//turn right
//				manager.sendTurnRightCommand(deltaDir);
				System.out.println("Sending turn right command - degrees: " + deltaDir);
			} else { //turn left
//				manager.sendTurnLeftCommand(deltaDir);
				System.out.println("Sending turn left command - degrees: " + deltaDir);
			}
		}
		else { //move robot forward
			int distanceToMove = (int) Math.sqrt(xDiv * xDiv + yDiv * yDiv);
//			manager.sendForwardCommand(distanceToMove);
			System.out.println("Sending forward command - distance: " + distanceToMove);
		}
		System.out.println();
	}
	
}
