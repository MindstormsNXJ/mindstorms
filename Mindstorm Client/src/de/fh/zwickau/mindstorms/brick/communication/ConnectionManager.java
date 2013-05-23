package de.fh.zwickau.mindstorms.brick.communication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTConnection;
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
	
	private Parser parser;
	private DataOutputStream positionSender;
	private DataInputStream commandReceiver;
	
	/**
	 * Initialises a ConnectionManager, including the connection itself and 
	 * the Thread that listens for commands to execute.
	 * 
	 * @param robot the robot which established the connection 
	 */
	public ConnectionManager(Robot robot) {
		parser = new Parser(robot);
		establishConnection();
		sendPose();
		waitForCommands();
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
						try {
							boolean shouldSendPose = parser.parseCommand(command);
							if (shouldSendPose)
								sendPose();
						} catch (IllegalArgumentException ex) {
							sendOutputMessage("The received command was unknown: " + command);
						} //can be extended by further catch blocks if more errors may occur
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			
		}).start();
	}
	
	private void sendOutputMessage(String outputMessage) {
		System.out.println("Sending message: " + outputMessage);
		try {
			positionSender.writeUTF("o" + outputMessage);
			positionSender.flush();
		} catch (IOException e) {
			System.err.println("Error sending message, retrying...");
			Delay.msDelay(1000);
			sendOutputMessage(outputMessage);
		}
	}
	
	/**
	 * Gets the robot's current Pose and sends it to the server.
	 */
	private void sendPose() {
		String parsedPose = parser.getParsedPose();
		System.out.println("Sending pose: " + parsedPose);
		try {
			positionSender.writeUTF(parsedPose);
			positionSender.flush();
			System.out.println("Pose sent");
		} catch (IOException e) {
			System.err.println("Error sending pose, retrying...");
			Delay.msDelay(1000);
			sendPose();
		}
	}
	
}