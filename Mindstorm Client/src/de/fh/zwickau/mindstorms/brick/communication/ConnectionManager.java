package de.fh.zwickau.mindstorms.brick.communication;

import lejos.robotics.navigation.Pose;
import de.fh.zwickau.mindstorms.brick.Robot;

public class ConnectionManager {
	
	private Robot robot;
	
	public ConnectionManager(Robot robot) {
		this.robot = robot;
		establishConnection();
	}
	
	public void sendPose(Pose pose) {
		//TODO
	}
	
	private void establishConnection() {
		
	}
	
}