package de.fh.zwickau.mindstorms.brick;

import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.addon.CompassHTSensor;

/**
 * This class is the abstract superclass for the two robot types, namely
 * WorkerRobot and PickerRobot. Therefore, it holds the fields that all robots
 * have in common and makes them accessible for other classes.
 * 
 * @author Tobias Schießl
 * @version 1.0
 */
public abstract class Robot {

	private String id;

	public NXTRegulatedMotor leftMotor;
	public NXTRegulatedMotor rightMotor;
	public CompassHTSensor compassSensor;
	public UltrasonicSensor ultrasonicSensor;
	public double driveTranslation;


	public Robot(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}
}