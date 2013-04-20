package de.fh.zwickau.mindstorms.server;

import de.fh.zwickau.mindstorms.server.navigation.mapping.Mapper;
import de.fh.zwickau.mindstorms.server.view.View;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		View view = new View();
		Mapper mapper = new Mapper();
		
		mapper.setObserverView(view);
		view.registerMapper(mapper);
		
		view.start();	// new Thread for view

	}

}
