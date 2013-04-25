package de.fh.zwickau.mindstorms.brick;

import de.fh.zwickau.mindstorms.brick.navigation.PositionManager;
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
	public NXTRegulatedMotor leftMotor;
	public NXTRegulatedMotor rightMotor;
	public CompassHTSensor compassSensor;
	public UltrasonicSensor ultrasonicSensor;
	public double driveTranslation; //angel in degrees per cm
	public PositionManager positionManager;
	
	public int rotateSpeed = 100; //standard values, will be changed after the calibration
	public int driveSpeed = 200; 
	public final int STANDARD_ROTATE_ACC = 5000;
	public final int STANDARD_DRIVE_ACC = 500;
	
	public void setMotorSpeed(int speed) {
		leftMotor.setSpeed(speed);
		rightMotor.setSpeed(speed);
	}
	
	public void setAcc(int acc) {
		leftMotor.setAcceleration(acc);
		rightMotor.setAcceleration(acc);
	}
	
	public void setModeDrive() {
		setMotorSpeed(driveSpeed);
		setAcc(STANDARD_DRIVE_ACC);
	}
	
	public void setModeRotate() {
		setMotorSpeed(rotateSpeed);
		setAcc(STANDARD_ROTATE_ACC);
	}
	
	public int getDirection() {
		return (int) compassSensor.getDegrees();
	}
	
}