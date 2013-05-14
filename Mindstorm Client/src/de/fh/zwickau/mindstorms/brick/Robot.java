package de.fh.zwickau.mindstorms.brick;

import de.fh.zwickau.mindstorms.brick.navigation.PositionManager;
import de.fh.zwickau.mindstorms.brick.task.Pick;
import lejos.nxt.ColorSensor;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.TouchSensor;
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
public class Robot {
	// Motors
	public NXTRegulatedMotor leftMotor;
	public NXTRegulatedMotor rightMotor;
	public NXTRegulatedMotor grabberMotor;
	// Sensors
	public CompassHTSensor compassSensor;
	public UltrasonicSensor ultrasonicSensor;
	public TouchSensor touchSensor;
	public ColorSensor colorSensor;

	public double driveTranslation; // angel in degrees per cm
	public PositionManager positionManager;

	public int rotationSpeed = 100; // standard values, will be changed after
									// the calibration
	public int driveSpeed = 200;
	public final int STANDARD_ROTATE_ACC = 5000;
	public final int STANDARD_DRIVE_ACC = 500;
	
	public Pick picker;

	public Robot() {
		leftMotor = Motor.A;
		rightMotor = Motor.B;
		grabberMotor = Motor.C;
		ultrasonicSensor = new UltrasonicSensor(SensorPort.S1);
		compassSensor = new CompassHTSensor(SensorPort.S2);
		touchSensor = new TouchSensor(SensorPort.S3);
		colorSensor = new ColorSensor(SensorPort.S4);
		picker = new Pick(this);
		picker.pickerUp();
	}

	/**
	 * Set the speed of the motors to a specific value.
	 * 
	 * @param speed
	 */
	public void setMotorSpeed(int speed) {
		leftMotor.setSpeed(speed);
		rightMotor.setSpeed(speed);
	}

	/**
	 * Set the acceleration of the motors to a specific value.
	 * 
	 * @param acc
	 */
	public void setAcc(int acc) {
		leftMotor.setAcceleration(acc);
		rightMotor.setAcceleration(acc);
	}

	/**
	 * This method is used to set the motor speed and acceleration for driving.
	 */
	public void setModeDrive() {
		setMotorSpeed(driveSpeed);
		setAcc(STANDARD_DRIVE_ACC);
	}

	/**
	 * This method is used to set the motor speed and acceleration for rotating.
	 */
	public void setModeRotate() {
		setMotorSpeed(rotationSpeed);
		setAcc(STANDARD_ROTATE_ACC);
	}

	/**
	 * 
	 * @return current degrees directly from the compass sensor
	 */
	public int getDirection() {
		return (int) compassSensor.getDegrees();
	}

	/**
	 * Sets the current driveTranslation used to control the motors
	 * 
	 * @param driveTranslation
	 */
	public void setDriveTranslation(double driveTranslation) {
		this.driveTranslation = driveTranslation;
	}

	/**
	 * This method stops the two motors of the robot.
	 */
	public void stop() {
		rightMotor.stop(true);
		leftMotor.stop(false);
	}

	/**
	 * This method moves the robot in front of an item and picks the item with
	 * the grabber.
	 */
	public void pickItem() {
		// do centralisation first
		if( ! picker.pickItem() )
			pickItem();		// attention, this could be an infinite loop, should be changed
	}
	
	public void dropItem(){
		picker.dropItem();
	}

}