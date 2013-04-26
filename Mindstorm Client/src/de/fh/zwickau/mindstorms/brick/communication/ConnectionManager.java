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
import de.fh.zwickau.mindstorms.brick.navigation.Direction;

public class ConnectionManager {
	
	private Robot robot;
	private DataOutputStream positionSender;
	private DataInputStream commandReceiver;
	
	public ConnectionManager(Robot robot) {
		this.robot = robot;
		establishConnection();
		waitForCommands();
	}
	
	private void establishConnection() {
		NXTConnection connection = USB.waitForConnection();
//		NXTConnection connection = Bluetooth.waitForConnection();
		
		positionSender = connection.openDataOutputStream();
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
						Pose pose = robot.positionManager.getPose();
						sendPose(pose);
					} catch (IOException e) {
						
					}
				}
			}
			
		}).start();
	}
	
	private void parseCommand(String command) {
		String operation = "", value = "";
		int index = 0;
		while (!Character.isDigit(command.charAt(index))) {
			operation += command.charAt(index);
			++index;
		}
		while (index < command.length()) {
			value += command.charAt(index);
			++index;
		}
		int valueAsInt;
		try {
			valueAsInt = Integer.parseInt(value);
		} catch (NumberFormatException ex) {
			System.err.println("No parameter was send with command");
			return;
		}
		switch (operation) {
		case "fw":
			robot.positionManager.move(valueAsInt);
			break;
		case "bw":
			robot.positionManager.move(-valueAsInt);
			break;
		case "left":
			robot.positionManager.rotate(valueAsInt, Direction.LEFT);
			break;
		case "right":
			robot.positionManager.rotate(valueAsInt, Direction.RIGHT);
			break;
		default:
			System.err.println("The received command is unknown");
		}
	}
	
	private boolean sendPose(Pose pose) {
		boolean success = false;
		String parsedPose = parsePose(pose);
		try {
			positionSender.writeUTF(parsedPose);
			success = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return success;
	}
	
	private String parsePose(Pose poseToParse) {
		StringBuilder builder = new StringBuilder();
		builder.append("x");
		builder.append((int) poseToParse.getX());
		builder.append("y");
		builder.append((int) poseToParse.getY());
		builder.append("dir");
		builder.append((int) poseToParse.getHeading());
		builder.append("end");
		return builder.toString();
	}
	
}