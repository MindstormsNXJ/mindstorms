package de.fh.zwickau.mindstorms.server.communication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTConnector;
import lejos.robotics.navigation.Pose;
import lejos.util.Delay;

import de.fh.zwickau.mindstorms.server.navigation.mapping.Mapper;

public class ConnectionManager {

	private Mapper mapper;
	private DataOutputStream commandSender;
	private DataInputStream poseReceiver;
	
	public ConnectionManager(Mapper mapper) {
		this.mapper = mapper;
		if (establishConnection())
			receiveAndProcessPoses();
		else {
			System.err.println("Connection failed, please check your configuration");
			return;
		}
		
		//TODO test commands - remove them when successful
//		System.out.println("Sending test commands...");
//		System.out.println("+forward");
//		sendForwardCommand(10);
//		Delay.msDelay(10000);
//		System.out.println("+left");
//		sendTurnLeftCommand(90);
//		Delay.msDelay(5000);
//		System.out.println("+backward");
//		sendBackwardCommand(10);
//		Delay.msDelay(10000);
//		System.out.println("+right");
//		sendTurnRightCommand(180);
	}
	
	private boolean establishConnection() {
		NXTConnector connector = new NXTConnector();
		boolean success = connector.connectTo(null, null, NXTCommFactory.BLUETOOTH);
		if (success) {
			System.out.println("Connection established via bluetooth");
			commandSender = new DataOutputStream(connector.getOutputStream());
			poseReceiver = new DataInputStream(connector.getInputStream());
		} else {
			System.err.println("Could not establish connection to NXT");
		}
		return success;
	}
	
	private void receiveAndProcessPoses() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				while (true) {
					try {
						System.out.println("Waiting to receive Pose...");
						String pose = poseReceiver.readUTF();
						System.out.println("Pose received: " + pose);
						decodePose(pose);
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
			}
			
		}).start();
	}
	
	private void decodePose(String poseString) {
		int index = 1; //skip 'x'
		String xPos = "", yPos = "", dir = "";
		while (poseString.charAt(index) != 'y') {
			xPos += poseString.charAt(index);
			++index;
		}
		++index; //skip 'y'
		while (poseString.charAt(index) != 'd') {
			yPos += poseString.charAt(index);
			++index;
		}
		index += 3; // skip 'dir'
		while (poseString.charAt(index) != 'e') {
			dir += poseString.charAt(index);
			++index;
		}
		try {
			Pose pose = new Pose(Integer.parseInt(xPos), Integer.parseInt(yPos), Integer.parseInt(dir));
			//TODO process pose to mapper and react to new position
		} catch (NumberFormatException ex) {
			System.err.println("Could not parse received pose");
			ex.printStackTrace();
		}
	}
	
	private void sendCommand(String command) {
		try {
			commandSender.writeUTF(command);
			commandSender.flush();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	private void sendForwardCommand(int dist) {
		sendCommand("fw" + dist);
	}
	
	private void sendBackwardCommand(int dist) {
		sendCommand("bw" + dist);
	}
	
	private void sendTurnLeftCommand(int degrees) {
		sendCommand("left" + degrees);
	}
	
	private void sendTurnRightCommand(int degrees) {
		sendCommand("right" + degrees);
	}
	
}
