package de.fh.zwickau.mindstorms.brick;

import de.fh.zwickau.mindstorms.brick.navigation.PositionManager;
import de.fh.zwickau.mindstorms.brick.task.Pick;
import ch.aplu.nxtsim.*;

/**
 * This class is the abstract superclass for the two robot types, namely
 * WorkerRobot and PickerRobot. Therefore, it holds the fields that all robots
 * have in common and makes them accessible for other classes.
 * 
 * @author Tobias Schießl
 * @version 1.0
 */
public class Robot extends NxtRobot{
	// Motors
	public Motor leftMotor;
	public Motor rightMotor;
	public Motor grabberMotor;
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

	public Robot() {
		leftMotor = new Motor(MotorPort.A);
		rightMotor = new Motor(MotorPort.B);
		grabberMotor = new Motor(MotorPort.C);
		ultrasonicSensor = new UltrasonicSensor(SensorPort.S1);
		compassSensor = new CompassHTSensor(SensorPort.S2);
	//	touchSensor = new TouchSensor(SensorPort.S3);
		colorSensor = new ColorSensor(SensorPort.S3);
		this.addPart(leftMotor);
		this.addPart(rightMotor);
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
//		leftMotor.setAcceleration(acc);
//		rightMotor.setAcceleration(acc);
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
		rightMotor.stop();
		leftMotor.stop();
	}

	/**
	 * This method moves the robot in front of an item and picks the item with
	 * the grabber.
	 */
	public void pickItem() {
		Pick p = new Pick(this);
		p.pickItem();
	}

}