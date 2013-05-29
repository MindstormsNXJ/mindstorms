package de.fh.zwickau.mindstorms.brick.communication;

import lejos.nxt.NXT;
import lejos.robotics.navigation.Pose;
import de.fh.zwickau.mindstorms.brick.Robot;

/**
 * A simple Parser that is able to parse received commands (and starts the execution
 * afterwards) as well as robot's current Pose.
 * 
 * @author Tobias Schießl
 * @version 1.0
 */
public class Parser {

	private Robot robot;
	
	/**
	 * Initialises a Parser.
	 * 
	 * @param robot the robot that will execute the commands and that's pose has to be parsed
	 */
	public Parser(Robot robot) {
		this.robot = robot;
	}
	
	/**
	 * Parses a received command and starts it's execution.
	 * The command has to finish with a number in order to parse it correctly.
	 * If the command was drop, the return value will be false.
	 * 
	 * @param command the command to parse and execute
	 * @return true, if the ConnectionManager should send the current pose
	 * @throws IllegalArgumentException if the command is unknown
	 */
	public boolean parseCommand(String command) throws IllegalArgumentException {
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
			return true;
		}
		//process command
		switch (operation) {
		case "fw":
			robot.positionManager.move(valueAsInt);
			return true;
		case "bw":
			robot.positionManager.move(-valueAsInt);
			return true;
		case "turn":
			robot.positionManager.rotateTo(valueAsInt);
			return true;
		case "pick":
			if (valueAsInt != 0)
				robot.positionManager.move(valueAsInt);
			robot.pickItem();
			return true;
		case "drop":
			if (valueAsInt != 0)
				robot.positionManager.move(valueAsInt);
			robot.dropItem();
			return false;
		case "exit":
			robot.positionManager.move(-150);
			robot.picker.pickerDown();
			NXT.shutDown();
			return false;
		case "query":
			return true; //nothing to do, the pose will be send in the next step
		default:
			throw new IllegalArgumentException("Unknown command");
		}
	}
	
	/**
	 * Gets the robot's current Pose and parses it to a String.
	 * The String will have the form "x...y...dir...end", where the ... will be 
	 * replaced by the Pose's coordinates and direction.
	 * 
	 * @return the parsed Pose
	 */
	public String getParsedPose() {
		Pose poseToParse = robot.positionManager.getPose();
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
