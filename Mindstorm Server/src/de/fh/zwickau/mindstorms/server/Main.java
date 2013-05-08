package de.fh.zwickau.mindstorms.server;

import javax.naming.OperationNotSupportedException;

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
 * The server start point of the application.
 * 
 * @author Andre Furchner, Tobias Schießl
 * @version 1.0
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
		
		try {
			new ConnectionManager(mapper, targetManager, "Picker");
		} catch (OperationNotSupportedException e) {
			System.err.println("Please make sure the connection manager is initialized for the robot with the name \"Picker\" - other robot types are currently not supported");
		}
		
		//TODO test of path finding
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
		
//		try {
//			Path path = new MindstormPathFinder(mapper).findRoute(new Pose(0, 0, 0), new Waypoint(10, 10));
//		} catch (DestinationUnreachableException e) {
//			//nothing
//		}
//		System.out.println(path);
		
		//TODO TEST - remove if commands are calculated correctly now
//		PathFinder finder = null;
//		try {
//			finder = new PathFinder(lineMap, targetManager, "Picker");
//		} catch (OperationNotSupportedException e) {
//			//nothing
//		}
//		targetManager.addRobot("Picker");
//		boolean lineLengthen = true;
//		if (!lineLengthen) {
//			finder.nextAction(new Pose(0,0,0), null);
//			finder.nextAction(new Pose(0,0,90), null);
//			finder.nextAction(new Pose(5,0,90), null);
//			finder.nextAction(new Pose(5,0,26), null);
//			finder.nextAction(new Pose(10,10,26), null);
//			finder.nextAction(new Pose(10,10,206), null);
//			finder.nextAction(new Pose(5,0,206), null);
//			finder.nextAction(new Pose(5,0,236), null);
//		} else {
//			finder.nextAction(new Pose(0,0,0), null);
//			finder.nextAction(new Pose(0,0,297), null);
//			finder.nextAction(new Pose(-10,5,297), null);
//			finder.nextAction(new Pose(-10,5,56), null);
//			finder.nextAction(new Pose(5,15,56), null);
//			finder.nextAction(new Pose(5,15,135), null);
//			finder.nextAction(new Pose(10,10,135), null); //TODO causes exception if DijkstraPathFinder is used
//			finder.nextAction(new Pose(10,10,315), null);
//			finder.nextAction(new Pose(5,15,315), null);
//			finder.nextAction(new Pose(5,15,236), null);
//			finder.nextAction(new Pose(-10,5,236), null);
//			finder.nextAction(new Pose(-10,5,180), null);
//		}
//		System.exit(0);
	}

}
