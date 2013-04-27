package de.fh.zwickau.mindstorms.brick.communication;

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
	 * 
	 * @param command the command to parse and execute
	 */
	public void parseCommand(String command) {
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
//		case "pick":
//			//TODO pick up the ball
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
		case "pick":
			System.out.println("Picking up the ball...");
			break;
		default:
			System.err.println("The received command is unknown");
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
