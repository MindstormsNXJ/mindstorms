package de.fh.zwickau.mindstorms.server.communication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

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
		establishConnection();
		receivePoses();
		
		//TODO test commands - remove them when successful
		sendForwardCommand(10);
		sendTurnLeftCommand(90);
		sendBackwardCommand(10);
	}
	
	private void establishConnection() {
		NXTConnector connector = new NXTConnector();
		boolean success = connector.connectTo();
		if (success) {
			commandSender = new DataOutputStream(connector.getOutputStream());
			poseReceiver = new DataInputStream(connector.getInputStream());
		} else {
			System.err.println("Could not establish connection to NXT");
		}
	}
	
	private void receivePoses() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				while (true) {
					try {
						while (poseReceiver.available() == 0)
							Delay.msDelay(100);
						String pose = poseReceiver.readUTF();
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
	
	private void sendTurnRIghtCommand(int degrees) {
		sendCommand("right" + degrees);
	}
	
}
