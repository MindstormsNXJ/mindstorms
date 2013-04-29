package de.fh.zwickau.mindstorms.server.navigation;

import de.fh.zwickau.mindstorms.server.communication.ConnectionManager;
import lejos.geom.Point;
import lejos.robotics.mapping.LineMap;
import lejos.robotics.navigation.DestinationUnreachableException;
import lejos.robotics.navigation.Pose;
import lejos.robotics.navigation.Waypoint;
import lejos.robotics.pathfinding.DijkstraPathFinder;
import lejos.robotics.pathfinding.Path;

/**
 * This PathFinder class decides which command has to be send next by
 * finding to path to it's current target using a Dijkstra algorithm.
 * 
 * @author Tobias Schießl
 * @version 1.0
 */
public class PathFinder {
	
	private DijkstraPathFinder finder;
	private Waypoint currentTarget;
	private boolean robotHasBall = false;
	
	/**
	 * Initialises a PathFinder with the LineMap to use from now on.
	 * 
	 * @param map the map to use
	 */
	public PathFinder(LineMap map) {
		finder = new DijkstraPathFinder(map);
	}
	
	/**
	 * Set's the current target which has to be reached.
	 * 
	 * @param x the target's x coordinate
	 * @param y the target's y coordinate
	 */
	public void setCurrentTarget(int x, int y) {
		currentTarget = new Waypoint(new Point(x, y));
	}
	
	/**
	 * Finds the next action to perform and tells the ConnectionManager
	 * to send the right command.
	 * 
	 * @param currentPose the robot's current pose
	 * @param manager the ConnectionManager which will send the command
	 * @return true if the target has been reached
	 * @throws DestinationUnreachableException if the destination is unreachable
	 */
	public boolean nextAction(Pose currentPose, ConnectionManager manager) throws DestinationUnreachableException {
		Path path = null;
		try {
			path = finder.findRoute(currentPose, currentTarget);
		} catch (DestinationUnreachableException e) {
			System.err.println("Error in finding path: destination is unreachable");
			throw e;
		}
		if (path.size() == 1) {
			System.out.println("Current target has been reached");
			if (!robotHasBall) {
				robotHasBall = true;
				manager.sendPickCommand();
			} else {
				manager.sendDropCommand();
				manager.finish();
			}
			return true;
		}
		Waypoint nextWaypoint = path.get(1); //0 is the current position
		int xDiv = (int) (nextWaypoint.x - currentPose.getX());
		int yDiv = (int) (nextWaypoint.y - currentPose.getY());
		int dir = (int) Math.toDegrees(Math.atan(xDiv/yDiv));
		int deltaDir = (int) (dir - currentPose.getHeading());
		if (deltaDir > 2) {//turn robot
			if (dir > currentPose.getHeading()) {//turn right
				manager.sendTurnRightCommand(deltaDir);
				System.out.println("Sending turn right command - degrees: " + deltaDir);
			} else { //turn left
				manager.sendTurnLeftCommand(deltaDir);
				System.out.println("Sending turn l commaeftnd - degrees: " + deltaDir);
			}
		}
		else { //move robot fw
			int distanceToMove = (int) Math.sqrt(xDiv * xDiv + yDiv * yDiv);
			manager.sendForwardCommand(distanceToMove);
			System.out.println("Sending forward command - distance: " + distanceToMove);
		}
		return false;
	}
	
}
