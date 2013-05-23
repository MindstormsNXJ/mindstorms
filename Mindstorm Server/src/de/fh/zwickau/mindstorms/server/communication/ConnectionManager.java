package de.fh.zwickau.mindstorms.server.communication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;

import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTConnector;
import lejos.robotics.mapping.LineMap;
import lejos.robotics.navigation.Pose;
import lejos.util.Delay;

import de.fh.zwickau.mindstorms.server.Server;
import de.fh.zwickau.mindstorms.server.navigation.PathFinder;
import de.fh.zwickau.mindstorms.server.navigation.TargetManager;
import de.fh.zwickau.mindstorms.server.navigation.mapping.Mapper;

/**
 * The ConnectionManager is responsible for establishing the connection to the
 * NXT, receiving it's position as a Pose and sending commands to it.
 * 
 * @author Tobias Schießl
 * @version 2.0
 */
public class ConnectionManager {

	private NXTConnector connector;
	private Mapper mapper;
	private TargetManager targetManager;
	private PathFinder pathFinder;
	private DataOutputStream commandSender;
	private DataInputStream stringReceiver;
	private String robotName;
	private Server server;
	private boolean terminate;
	
	//TODO remove for final version
	private final boolean NO_NXT = false; //true, if there is no NXT available - PathFinding only
	
	/**
	 * Initialises a ConnectionManager, including the connection itself as well as
	 * the Thread that will process the received Poses.
	 * 
	 * @param mapper the Mapper that gets notified about the robot's current Pose
	 * @param robotName the robot's friendly name
	 * @param server the server to tell that this connections is terminated at the end
	 */
	public ConnectionManager(final Mapper mapper, final String robotName, final Server server) {
		this.mapper = mapper;
		this.robotName = robotName;
		this.server = server;
		new Thread(new Runnable(){
			
			@Override
			public void run() {
				terminate = false;
				targetManager = TargetManager.getInstance();
				targetManager.addRobot(robotName);
				
				if (NO_NXT) 
					localTest();
				else {
					pathFinder = new PathFinder(mapper.getLineMap(), robotName);
					
					while (!establishConnection()) {
						System.err.println("Connection failed, will retry in 10 seconds...");
						Delay.msDelay(10000);
					}
					receiveAndProcessPoses();
				}
			}
			
		}).start();
	}
	
	//TODO remove for final version
	private void localTest() {
		LineMap lineMap = mapper.getLineMap();
		pathFinder = new PathFinder(lineMap, robotName);
		//long way
		pathFinder.nextAction(new Pose(0,0,0), this);
		pathFinder.nextAction(new Pose(0,0,200), this);
		pathFinder.nextAction(new Pose(-3,-8,200), this);
		pathFinder.nextAction(new Pose(-3,-8,180), this);
		pathFinder.nextAction(new Pose(-3,-14,180), this);
		pathFinder.nextAction(new Pose(-3,-22,180), this);
		pathFinder.nextAction(new Pose(-3,-22,90), this);
		pathFinder.nextAction(new Pose(3,-22,90), this);
		pathFinder.nextAction(new Pose(3,-22,48), this);
		pathFinder.nextAction(new Pose(12,-14,48), this);
		pathFinder.nextAction(new Pose(12,-14,0), this);
		pathFinder.nextAction(new Pose(12,-8,0), this);
		pathFinder.nextAction(new Pose(12,-6,354), this);
		pathFinder.nextAction(new Pose(10,10,354), this); //pick command was sent
		//short way
//		pathFinder.nextAction(new Pose(0,0,0), this);
//		pathFinder.nextAction(new Pose(0,0,200), this);
//		pathFinder.nextAction(new Pose(-3,-8,200), this);
//		pathFinder.nextAction(new Pose(-3,-8,90), this);
//		pathFinder.nextAction(new Pose(1,-8,90), this);
//		pathFinder.nextAction(new Pose(3,-8,90), this);
//		pathFinder.nextAction(new Pose(7,-8,90), this);
//		pathFinder.nextAction(new Pose(12,-8,90), this);
//		pathFinder.nextAction(new Pose(12,-8,354), this);
//		pathFinder.nextAction(new Pose(10,10,354), this); //pick command was sent
	}
	
	/**
	 * Establishes the connection to the NXT.
	 * 
	 * @return true if connection was successful established
	 */
	private boolean establishConnection() {
		connector = new NXTConnector();
		boolean success = connector.connectTo(robotName, null, NXTCommFactory.BLUETOOTH);
		if (success) {
			System.out.println("Connection established via bluetooth");
			commandSender = new DataOutputStream(connector.getOutputStream());
			stringReceiver = new DataInputStream(connector.getInputStream());
		} else {
			System.err.println("Could not establish connection to NXT");
		}
		return success;
	}
	
	/**
	 * Starts the Thread that will listen for received Poses and process them afterwards.
	 */
	private void receiveAndProcessPoses() {
		while (!terminate) {
			try {
				System.out.println("Waiting to receive Pose...");
				String receivedString = stringReceiver.readUTF();
				System.out.println("String received: " + receivedString);
				decodeString(receivedString);
			} catch (EOFException ex) {
				System.err.println("Connection terminated by NXT");
				if (!terminate && targetManager.hasMoreWaypoints(robotName)) {
					System.out.println("Resetting connection");
					while (!establishConnection())
						Delay.msDelay(2000);
				} else {
					terminate();
					break;
				}
			} catch (IOException ex) {
				if (ex.getMessage().contains("Failed to read"))
					System.err.println("Timeout while receiving pose");
				else
					ex.printStackTrace();
			}
		}
	}
	
	/**
	 * Decodes the received String and forwards it if necessary. 
	 * Note, that a received pose will be with millimeter coordinates - we will convert them to centimeter for the path finder.
	 * 
	 * @param receivedString the received string
	 */
	private void decodeString(String receivedString) {
		if (receivedString.charAt(0) == 'o') { //something should be printed on the console
			System.out.println("Output from NXT " + robotName + ": " + receivedString.substring(1));
		} else if (receivedString.charAt(0) == 'x') { //a pose was received
			int index = 1; //skip 'x'
			String xPos = "", yPos = "", dir = "";
			while (receivedString.charAt(index) != 'y') {
				xPos += receivedString.charAt(index);
				++index;
			}
			++index; //skip 'y'
			while (receivedString.charAt(index) != 'd') {
				yPos += receivedString.charAt(index);
				++index;
			}
			index += 3; // skip 'dir'
			while (receivedString.charAt(index) != 'e') {
				dir += receivedString.charAt(index);
				++index;
			}
			Pose pose = null;
			try {
				pose = new Pose(Integer.parseInt(xPos) / 10, Integer.parseInt(yPos) / 10, Integer.parseInt(dir));
			} catch (NumberFormatException ex) {
				System.err.println("Could not parse received pose");
				System.err.println("Received String: " + receivedString);
				return;
			}
			mapper.addPose(pose, connector.getNXTInfo().name);
			pathFinder.nextAction(pose, this);
		} else {
			System.err.println("The received String could not be decoded.");
		}
	}
	
	/**
	 * Sends a command to the NXT.
	 * 
	 * @param command the command to send
	 */
	private void sendCommand(String command) {
		try {
			if (!NO_NXT) {
				commandSender.writeUTF(command);
				commandSender.flush();
			}
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
	 * Sends a turn-command.
	 * 
	 * @param degrees the degrees to turn to
	 */
	public void sendTurnCommand(int degrees) {
		sendCommand("turn" + degrees);
	}
	
	/**
	 * Sends a pick-command. We assume that the robot has the ball after
	 * sending this command, at least he should.
	 */
	public void sendPickCommand() {
		sendCommand("pick0");
	}
	
	/**
	 * Sends a drop-ball-command.
	 */
	public void sendDropCommand() {
		sendCommand("drop0");
	}
	
	/**
	 * Terminates the NXT and closes this connection.
	 */
	public void terminate() {
		sendCommand("exit0");
		System.out.println("NXT " + robotName + " is shutting down");
		try {
			commandSender.close();
			stringReceiver.close();
			connector.close();
		} catch (IOException e) {
			System.err.println("Could not close sockets to NXT");
		}
		targetManager.removeRobot(robotName);
		server.removeConnection(this);
		terminate = true;
	}
	
	/**
	 * Sends a command to the NXT to query it's current pose.
	 */
	public void sendQueryPoseCommand() {
		sendCommand("query0");
	}
	
	public void mapChaged(){
		pathFinder.mapChanged(mapper.getLineMap());
	}
	
}
