package de.fh.zwickau.mindstorms.server.communication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lejos.pc.comm.NXTConnector;
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
		//TODO
	}
	
}
