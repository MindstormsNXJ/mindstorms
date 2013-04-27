package de.fh.zwickau.mindstorms.brick.communication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTConnection;
import lejos.robotics.navigation.Pose;
import lejos.util.Delay;
import de.fh.zwickau.mindstorms.brick.Robot;

/**
 * This connection manager is responsible for establishing a connection to the server.
 * Afterwards, it will send it's initial Pose to it, and wait for further commands.
 * After a command is processed, it sends it's new Pose to the server again.
 * 
 * @author Tobias Schießl
 * @version 1.0
 */
public class ConnectionManager {
	
	private Robot robot;
	private DataOutputStream positionSender;
	private DataInputStream commandReceiver;
	
	/**
	 * Initialises a ConnectionManager, including the connection itself and 
	 * the Thread that listens for commands to execute.
	 * 
	 * @param robot the robot which established the connection 
	 */
	public ConnectionManager(Robot robot) {
		this.robot = robot;
		establishConnection();
		sendStartPosition();
		waitForCommands();
		
		//TODO test commands - remove them when successful
//		sendPose(new Pose(10,20,45));
	}
	
	/**
	 * Establishes the connection.
	 */
	private void establishConnection() {
		System.out.println("Waiting for bluetooth connection...");
		NXTConnection connection = Bluetooth.waitForConnection();
		System.out.println("Connection established via bluetooth");
		
		positionSender = connection.openDataOutputStream();
		commandReceiver = connection.openDataInputStream();
	}
	
	/**
	 * Sends the initial start position to the server, which is needed there
	 * to decide what has to be done first.
	 */
	private void sendStartPosition() {
		//TODO insert these lines as soon as the pose is calculated correctly
//		Pose pose = robot.positionManager.getPose();
//		sendPose(pose);
	}
	
	/**
	 * Starts the Thread that will wait for commands to execute.
	 */
	private void waitForCommands() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				while (true) {
					try {
						System.out.println("Waiting for commands...");
						while (commandReceiver.available() == 0)
							Delay.msDelay(100);
						String command = commandReceiver.readUTF();
						System.out.println("Command received: " + command);
						parseCommand(command);
						//TODO insert these lines as soon as the pose is calculated correctly
//						Pose pose = robot.positionManager.getPose();
//						sendPose(pose);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			
		}).start();
	}
	
	private void parseCommand(String command) {
		String operation = "", value = "";
		int index = 0;
		//get the operation (fw, bw, left or right)
		while (!Character.isDigit(command.charAt(index))) {
			operation += command.charAt(index);
			++index;
		}
		//get the parameter (distance or degrees)
		while (index < command.length() && Character.isDigit(command.charAt(index))) {
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
		//process command
//		switch (operation) {
//		case "fw":
//			robot.positionManager.move(valueAsInt);
//			break;
//		case "bw":
//			robot.positionManager.move(-valueAsInt);
//			break;
//		case "left":
//			robot.positionManager.rotate(valueAsInt, Direction.LEFT);
//			break;
//		case "right":
//			robot.positionManager.rotate(valueAsInt, Direction.RIGHT);
//			break;
//		default:
//			System.err.println("The received command is unknown");
//		}
		//TODO replace this code with the one above as soon as PositionManager is ready
		switch (operation) {
		case "fw":
			robot.leftMotor.forward();
			robot.rightMotor.forward();
			break;
		case "bw":
			robot.leftMotor.backward();
			robot.rightMotor.backward();
			break;
		case "left":
			robot.leftMotor.backward();
			robot.rightMotor.forward();
			break;
		case "right":
			robot.leftMotor.forward();
			robot.rightMotor.backward();
			break;
		default:
			System.err.println("The received command is unknown");
		}
	}
	
	private boolean sendPose(Pose pose) {
		boolean success = false;
		String parsedPose = parsePose(pose);
		System.out.println("Sending pose: " + parsedPose);
		try {
			positionSender.writeUTF(parsedPose);
			positionSender.flush();
			success = true;
			System.out.println("Pose sent");
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