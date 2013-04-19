package de.fh.zwickau.mindstorms.server;

import de.fh.zwickau.mindstorms.server.navigation.mapping.Mapper;
import de.fh.zwickau.mindstorms.server.view.View;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Mapper mapper = new Mapper();
		View view = new View();
		view.start();

	}

}
