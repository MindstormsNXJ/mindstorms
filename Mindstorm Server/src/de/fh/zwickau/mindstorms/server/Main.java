package de.fh.zwickau.mindstorms.server;

/**
 * The server start point of the application.
 * 
 * @author Andre Furchner, Tobias Schießl
 * @version 1.0
 */
public class Main {

	public static void main(String[] args) {
		
		new Server();
		new TestingGui(System.out);
	}

}
