package de.fh.zwickau.mindstorms.server.communication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;

import lejos.geom.Point;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTConnector;
import lejos.robotics.navigation.DestinationUnreachableException;
import lejos.robotics.navigation.Pose;
import lejos.util.Delay;

import de.fh.zwickau.mindstorms.server.navigation.PathFinder;
import de.fh.zwickau.mindstorms.server.navigation.mapping.Mapper;

/**
 * The ConnectionManager is responsible for establishing the connection to the
 * NXT, receiving it's position as a Pose and sending commands to it.
 * 
 * @author Tobias Schießl
 * @version 1.1
 */
public class ConnectionManager {

	private NXTConnector connector;
	private Mapper mapper;
	private PathFinder pathFinder;
	private DataOutputStream commandSender;
	private DataInputStream poseReceiver;
	
	//TODO replace this with points in the mapper if possible
	private Point ballPos = new Point(35,25);
	private Point targetPos = new Point(75,10);
	
	/**
	 * Initialises a ConnectionManager, including the connection itself as well as
	 * the Thread that will process the received Poses.
	 * 
	 * @param mapper the mapper that gets notified about the robot's current Pose
	 */
	public ConnectionManager(Mapper mapper) {
		this.mapper = mapper;
		pathFinder = new PathFinder(mapper.getLineMap());
		pathFinder.setCurrentTarget((int) ballPos.x, (int) ballPos.y);
		if (establishConnection())
			receiveAndProcessPoses();
		else {
			System.err.println("Connection failed, please check your configuration");
			return;
		}
		
		//TODO test commands - remove them for final version
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
//		Delay.msDelay(5000);
//		System.out.println("+pick");
//		sendPickCommand();
		
//		pathFinder.setCurrentTarget(10, 10);
//		try {
//			pathFinder.nextAction(new Pose(0,0,0), this);
//			pathFinder.nextAction(new Pose(0,0,45), this);
//			pathFinder.nextAction(new Pose(10,10,45), this);
//		} catch (DestinationUnreachableException e) {
//			e.printStackTrace();
//		}
	}
	
	/**
	 * Establishes the connection to the NXT.
	 * 
	 * @return true if connection was successful established
	 */
	private boolean establishConnection() {
		connector = new NXTConnector();
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
	
	/**
	 * Starts the Thread that will listen for received Poses and process them afterwards.
	 */
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
					} catch (EOFException ex) {
						System.err.println("Connection terminated by NXT");
						break;
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
		Pose pose = null;
		try {
			pose = new Pose(Integer.parseInt(xPos), Integer.parseInt(yPos), Integer.parseInt(dir));
		} catch (NumberFormatException ex) {
			System.err.println("Could not parse received pose");
			System.err.println("Received String: " + poseString);
			return;
		}
		mapper.addPose(pose, connector.getNXTInfo().name);
		pathFinder.nextAction(pose, this);
	}
	
	/**
	 * Sends a command to the NXT.
	 * 
	 * @param command the command to send
	 */
	private void sendCommand(String command) {
		try {
			commandSender.writeUTF(command);
			commandSender.flush();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * Sends a drive-forward-command.
	 * 
	 * @param dist the distance to drive
	 */
	public void sendForwardCommand(int dist) {
		sendCommand("fw" + dist);
	}
	
	/**
	 * Sends a drive-backward-command.
	 * 
	 * @param dist the distance to drive
	 */
	public void sendBackwardCommand(int dist) {
		sendCommand("bw" + dist);
	}
	
	/**
	 * Sends a turn-left-command.
	 * 
	 * @param degrees the degrees to turn
	 */
	public void sendTurnLeftCommand(int degrees) {
		sendCommand("left" + degrees);
	}
	
	/**
	 * Sends a turn-right-command.
	 * 
	 * @param degrees the degrees to turn
	 */
	public void sendTurnRightCommand(int degrees) {
		sendCommand("right" + degrees);
	}
	
	/**
	 * Sends a pick-command. We assume that the robot has the ball after
	 * sending this command, at least he should.
	 */
	public void sendPickCommand() {
		sendCommand("pick0");
		pathFinder.setCurrentTarget((int) targetPos.x, (int) targetPos.y);
	}
	
	/**
	 * Sends a drop-ball-command.
	 */
	public void sendDropCommand() {
		sendCommand("drop0");
	}
	
	/**
	 * Terminates the whole server.
	 */
	public void terminate() {
		System.out.println("Ball will be dropped now, server is shutting down");
		System.exit(0); //TODO there should be a smarter way to do this
	}
	
}
