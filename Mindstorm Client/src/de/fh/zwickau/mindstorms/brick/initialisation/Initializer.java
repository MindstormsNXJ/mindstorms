package de.fh.zwickau.mindstorms.brick.initialisation;

import lejos.nxt.Button;
import lejos.nxt.ButtonListener;
import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.NXT;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.addon.CompassHTSensor;
import lejos.nxt.comm.Bluetooth;
import lejos.robotics.navigation.Pose;
import de.fh.zwickau.mindstorms.brick.PickerRobot;
import de.fh.zwickau.mindstorms.brick.Robot;
import de.fh.zwickau.mindstorms.brick.WorkerRobot;
import de.fh.zwickau.mindstorms.brick.navigation.PositionManager;
import de.fh.zwickau.mindstorms.brick.sensors.SensorManager;

/**
 * This class is responsible for managing all tasks that have to be performed
 * when the NXT starts, e.g. sensor calibration and connection establishment.
 * This includes also the initialisation of the sensors and motors.
 * 
 * @author Tobias Schießl
 * @version 1.0
 */
public class Initializer {

	private Robot robot;

	/**
	 * Initialises the NXT and adds a button listener to the escape button, that
	 * will shut down the robot whenever it its pressed.
	 */
	public void initialize() {
		// create NXT and init sensors
		String id = Bluetooth.getFriendlyName();
		if (id.equals("Worker1") || id.equals("Worker2")) {
			robot = new WorkerRobot(id);
		} else if (id.equals("Picker")) {
			robot = new PickerRobot(id);
		} else {
			System.out.println("please check the robots id");
			Button.ENTER.waitForPress();
			NXT.shutDown();
		}
		robot.leftMotor = Motor.A;
		robot.rightMotor = Motor.B;
		robot.compassSensor = new CompassHTSensor(SensorPort.S2);
		robot.ultrasonicSensor = new UltrasonicSensor(SensorPort.S1);

		// add the universal listener to stop the robot
		Button.ESCAPE.addButtonListener(new ButtonListener() {

			@Override
			public void buttonReleased(Button b) {
				// nothing
			}

			@Override
			public void buttonPressed(Button b) {
				robot.compassSensor.stopCalibration();
				NXT.shutDown();
			}

		});
		// Calibtration
		{
			System.out.println("Calibrate");
			// calibrate translation

			DriveTranslationCalibrator driveTranslationCalibrator = new DriveTranslationCalibrator(
					robot);
			driveTranslationCalibrator.calibrate();

			// calibrate compass sensor
			CompassCalibrator compassCalibrator = new CompassCalibrator(robot);
			compassCalibrator.preCalibrate();
			compassCalibrator.calibrate();
			System.out.println("Calibrated");

		}
		// TODO establish connection to the server
		
		Button.ENTER.waitForPress();
		new PositionManager(new Pose(0,0,0), robot);
		new SensorManager(robot);
		
		Button.ESCAPE.waitForPress();
	}
}