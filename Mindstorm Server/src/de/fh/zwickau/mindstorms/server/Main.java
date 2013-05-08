package de.fh.zwickau.mindstorms.server;

import lejos.geom.Line;
import lejos.geom.Rectangle;
import lejos.robotics.mapping.LineMap;
import lejos.robotics.navigation.DestinationUnreachableException;
import lejos.robotics.navigation.Pose;
import lejos.robotics.navigation.Waypoint;
import lejos.robotics.pathfinding.DijkstraPathFinder;
import lejos.robotics.pathfinding.Path;
import de.fh.zwickau.mindstorms.server.communication.ConnectionManager;
import de.fh.zwickau.mindstorms.server.navigation.MindstormPathFinder;
import de.fh.zwickau.mindstorms.server.navigation.PathFinder;
import de.fh.zwickau.mindstorms.server.navigation.TargetManager;
import de.fh.zwickau.mindstorms.server.navigation.mapping.Mapper;
import de.fh.zwickau.mindstorms.server.view.View;

/**
 * The Server start point
 * 
 * @author Andre Furchner
 *
 */
public class Main {

	public static void main(String[] args) {
		
		View view = new View();
		Mapper mapper = new Mapper(1);
		TargetManager targetManager = new TargetManager();
		
		//register for observer model
		mapper.setObserverView(view);
		targetManager.setObserverView(view);
		
		view.registerMapper(mapper);
		view.registerTargetManager(targetManager);

		view.start();	// new Thread for view
		
		new ConnectionManager(mapper, targetManager);
//		Line[] lines = new Line[2];
//		lines[0] = new Line(0,5,(float) 5.01,5);
//		lines[1] = new Line(5,5,5,0);
//		LineMap lineMap = new LineMap(lines, new Rectangle(-20, 20, 40, 40));
//		lejos.robotics.pathfinding.PathFinder finder = new DijkstraPathFinder(lineMap);
//		Path path = null;
//		try {
//			path = finder.findRoute(new Pose(0,0,0), new Waypoint(10, 10));
//		} catch (DestinationUnreachableException ex) {
//			//nothing
//		}
//		System.out.println(path);
		//TODO test of path finding
//		try {
//			Path path = new MindstormPathFinder(mapper).findRoute(new Pose(0, 0, 0), new Waypoint(10, 10));
//		} catch (DestinationUnreachableException e) {
//			//nothing
//		}
		
		//TODO TEST - remove if commands are calculated correctly now
//		PathFinder finder = new PathFinder(mapper.getLineMap(), targetManager);
//		finder.nextAction(new Pose(0,0,0), null); //TODO algorithm does not work
//		finder.nextAction(new Pose(0,0,45), null);
//		finder.nextAction(new Pose(29,30,45), null);
//		finder.nextAction(new Pose(29,30,90), null);
//		finder.nextAction(new Pose(30,30,90), null);
//		finder.nextAction(new Pose(30,30,90), null);
//		finder.nextAction(new Pose(30,30,225), null);
//		finder.nextAction(new Pose(0,-1,225), null);
//		finder.nextAction(new Pose(0,-1,0), null);
//		finder.nextAction(new Pose(0,0,0), null);
//		System.exit(0);
	}

}
