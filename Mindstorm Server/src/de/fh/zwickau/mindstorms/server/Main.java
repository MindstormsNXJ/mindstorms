package de.fh.zwickau.mindstorms.server;

import lejos.robotics.navigation.Pose;
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
		Mapper mapper = new Mapper(20);
		
		//register for observer model
		mapper.setObserverView(view);
		view.registerMapper(mapper);
		
		view.start();	// new Thread for view
		
		
		//TODO: (just Debug) make some test calls to Mapper.
		for(int i = 0; i < 360*8; i++){
			Pose pose = new Pose(10.0f,10.0f, i);
			mapper.addObstacle(pose, (int)(400 * (Math.random() * 0.25 * Math.sin(i) +1)));
		}

	}

}