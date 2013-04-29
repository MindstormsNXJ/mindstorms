package de.fh.zwickau.mindstorms.server.navigation;

import de.fh.zwickau.mindstorms.server.communication.ConnectionManager;
import lejos.geom.Point;
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
	
	public void setCurrentTarget(int x, int y) {
		currentTarget = new Waypoint(new Point(x, y));
	}
	
	public void nextAction(Pose currentPose, ConnectionManager manager) {
		Path path = null;
		try {
			path = finder.findRoute(currentPose, currentTarget);
		} catch (DestinationUnreachableException e) {
			System.err.println("Destination is unreachable");
			return;
		}
		Waypoint nextWaypoint = path.get(1);
		int xDiv = (int) (nextWaypoint.x - currentPose.getX());
		int yDiv = (int) (nextWaypoint.y - currentPose.getY());
		int dir = (int) Math.toDegrees(Math.atan(xDiv/yDiv));
		int deltaDir = (int) (dir - currentPose.getHeading());
		System.out.println(dir + " " + deltaDir);
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
	}
	
}
