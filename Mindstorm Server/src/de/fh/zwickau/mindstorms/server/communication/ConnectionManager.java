package de.fh.zwickau.mindstorms.server.communication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;

import javax.naming.OperationNotSupportedException;

import lejos.geom.Line;
import lejos.geom.Rectangle;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTConnector;
import lejos.robotics.mapping.LineMap;
import lejos.robotics.navigation.Pose;
import lejos.util.Delay;

import de.fh.zwickau.mindstorms.server.navigation.PathFinder;
import de.fh.zwickau.mindstorms.server.navigation.TargetManager;
import de.fh.zwickau.mindstorms.server.navigation.mapping.ConverterV2;
import de.fh.zwickau.mindstorms.server.navigation.mapping.Mapper;

/**
 * The ConnectionManager is responsible for establishing the connection to the
 * NXT, receiving it's position as a Pose and sending commands to it.
 * 
 * @author Tobias Schießl
 * @version 1.2
 */
public class ConnectionManager {

	private NXTConnector connector;
	private Mapper mapper;
	private TargetManager targetManager;
	private PathFinder pathFinder;
	private DataOutputStream commandSender;
	private DataInputStream poseReceiver;
	private String robotName;
	
	private final boolean NO_NXT = true; //true, if there is no NXT available - PathFinding only
	
	/**
	 * Initialises a ConnectionManager, including the connection itself as well as
	 * the Thread that will process the received Poses.
	 * 
	 * @param mapper the Mapper that gets notified about the robot's current Pose
	 * @param robotName the robot's friendly name
	 * @throws OperationNotSupportedException if the robot's name is not "Picker"
	 */
	public ConnectionManager(final Mapper mapper, final String robotName) {
		this.mapper = mapper;
		this.robotName = robotName;
		new Thread(new Runnable(){
			
			@Override
			public void run() {
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
	
	private void localTest() {
//		Line[] lines = new Line[6];
//		lines[0] = new Line(-5,5,5,5);
//		lines[1] = new Line(-5,5,-5,31);
//		lines[2] = new Line(5,5,5,-5);
//		lines[3] = new Line(0,-10,10,-10);
//		lines[4] = new Line(0,-10,0,-20);
//		lines[5] = new Line(-11,10,-11,-15);
//		LineMap lineMap = new LineMap(lines, new Rectangle(-31, 31, 62, 62));
//		pathFinder = new PathFinder(lineMap, robotName);
//		pathFinder.nextAction(new Pose(0,0,0), this);
//		pathFinder.nextAction(new Pose(0,0,225), this);
//		pathFinder.nextAction(new Pose(5,-5,225), this);
//		pathFinder.nextAction(new Pose(5,-5,251), this);
//		pathFinder.nextAction(new Pose(-10,-10,251), this);
//		pathFinder.nextAction(new Pose(-10,-10,154), this);
//		pathFinder.nextAction(new Pose(0,-30,154), this);
//		pathFinder.nextAction(new Pose(0,-30,45), this);
//		pathFinder.nextAction(new Pose(20,-10,45), this);
//		pathFinder.nextAction(new Pose(20,-10,342), this);
//		pathFinder.nextAction(new Pose(15,5,342), this);
//		pathFinder.nextAction(new Pose(15,5,315), this);
//		pathFinder.nextAction(new Pose(10,10,315), this);
		
//		LineMap lineMap = ConverterV2.convertGridToLineMap(mapper.getGrid(), 3);
		LineMap lineMap = mapper.getLineMap();
		pathFinder = new PathFinder(lineMap, robotName);
		pathFinder.nextAction(new Pose(0,0,0), this);
	}
	
	/**
	 * Establishes the connection to the NXT.
	 * 
	 * @return true if connection was successful established
	 */
	private boolean establishConnection() {
		connector = new NXTConnector();
		boolean success = connector.connectTo(robotName, null, NXTCommFactory.BLUETOOTH);
//		boolean success = connector.connectTo(null, null, NXTCommFactory.BLUETOOTH);
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
		while (true) {
			try {
				System.out.println("Waiting to receive Pose...");
				String pose = poseReceiver.readUTF();
				System.out.println("Pose received: " + pose);
				decodePose(pose);
			} catch (EOFException ex) {
				System.err.println("Connection terminated by NXT");
				if (targetManager.hasMoreWaypoints(robotName)) {
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
	 * Terminates the whole server.
	 */
	public void terminate() {
		sendCommand("exit0");
		System.out.println("Server and NXT are shutting down");
		System.exit(0); //TODO there should be a smarter way to do this
	}
	
	public void mapChaged(){
		//TODO: Map has changed. Update PathFinder with new LineMap.
		//update new LineMap with mapper.getLineMap();
	}
	
}
