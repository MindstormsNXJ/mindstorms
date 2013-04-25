package de.fh.zwickau.mindstorms.brick.communication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTConnection;
import lejos.nxt.comm.USB;
import lejos.robotics.navigation.Pose;
import lejos.util.Delay;
import de.fh.zwickau.mindstorms.brick.Robot;

public class ConnectionManager {
	
	private Robot robot;
	private DataOutputStream positionWriter;
	private DataInputStream commandReceiver;
	
	public ConnectionManager(Robot robot) {
		this.robot = robot;
		establishConnection();
		waitForCommands();
	}
	
	private void establishConnection() {
		NXTConnection connection = USB.waitForConnection();
//		NXTConnection connection = Bluetooth.waitForConnection();
		
		positionWriter = connection.openDataOutputStream();
		commandReceiver = connection.openDataInputStream();
	}
	
	private void waitForCommands() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				while (true) {
					try {
						while (commandReceiver.available() == 0)
							Delay.msDelay(100);
						String command = commandReceiver.readUTF();
						parseCommand(command);
					} catch (IOException e) {
						
					}
				}
			}
			
		}).start();
	}
	
	private void parseCommand(String command) {
		
	}
	
	public boolean sendPose(Pose pose) {
		boolean success = false;
		String parsedPose = parsePose(pose);
		try {
			positionWriter.writeUTF(parsedPose);
			success = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return success;
	}
	
	private String parsePose(Pose poseToParse) {
		StringBuilder builder = new StringBuilder();
		builder.append("x");
		builder.append(poseToParse.getX());
		builder.append("y");
		builder.append(poseToParse.getY());
		builder.append("dir");
		builder.append(poseToParse.getHeading());
		builder.append("end");
		return builder.toString();
	}
	
}