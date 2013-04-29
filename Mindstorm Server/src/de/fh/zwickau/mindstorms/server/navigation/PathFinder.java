package de.fh.zwickau.mindstorms.server.navigation;

import de.fh.zwickau.mindstorms.server.communication.ConnectionManager;
import lejos.robotics.mapping.LineMap;
import lejos.robotics.navigation.DestinationUnreachableException;
import lejos.robotics.navigation.Pose;
import lejos.robotics.navigation.Waypoint;
import lejos.robotics.pathfinding.DijkstraPathFinder;
import lejos.robotics.pathfinding.Path;

public class PathFinder {
	
	private DijkstraPathFinder finder;
	private Waypoint currentTarget;
	
	public PathFinder(LineMap map) {
		finder = new DijkstraPathFinder(map);
	}
	
	public void setCurrentTarget(Pose targetPose) {
		currentTarget = new Waypoint(targetPose);
	}
	
	public void nextAction(Pose currentPose, ConnectionManager manager) {
		Path path = null;
		try {
			path = finder.findRoute(currentPose, currentTarget);
		} catch (DestinationUnreachableException e) {
			System.err.println("Destination is unreachable");
			return;
		}
		Waypoint nextWaypoint = path.get(0);
		int xDiv = (int) (nextWaypoint.x - currentPose.getX());
		int yDiv = (int) (nextWaypoint.y - currentPose.getY());
		int dir = (int) Math.atan(xDiv/yDiv);
		int deltaDiv = (int) (dir - currentPose.getHeading());
		if (deltaDiv > 2) //turn robot
			;
		else { //move robot fw
			
		}
		//TODO call methods on ConnectionManager to move robot
	}
	
}
