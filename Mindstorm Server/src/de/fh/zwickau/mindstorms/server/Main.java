package de.fh.zwickau.mindstorms.server;

import lejos.robotics.navigation.Pose;
import de.fh.zwickau.mindstorms.server.communication.ConnectionManager;
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

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		View view = new View();
		Mapper mapper = new Mapper(1);
		
		//register for observer model
		mapper.setObserverView(view);
		view.registerMapper(mapper);
		
		view.start();	// new Thread for view
		
		TargetManager targetManager = new TargetManager();
		new ConnectionManager(mapper, targetManager);
		
		//TODO TEST - remove if commands are calculated correctly now
//		PathFinder finder = new PathFinder(mapper.getLineMap(), targetManager);
//		finder.nextAction(new Pose(0,0,0), null);
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
