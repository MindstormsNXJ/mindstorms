package de.fh.zwickau.mindstorms.server;

import de.fh.zwickau.mindstorms.server.communication.ConnectionManager;
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
		Mapper mapper = new Mapper(20);
		
		//register for observer model
		mapper.setObserverView(view);
		view.registerMapper(mapper);
		
		view.start();	// new Thread for view

		TargetManager targetManaer = new TargetManager();
		new ConnectionManager(mapper, targetManaer);
	}

}