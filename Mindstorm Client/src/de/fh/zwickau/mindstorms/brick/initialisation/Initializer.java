package de.fh.zwickau.mindstorms.brick.initialisation;

import lejos.nxt.Button;
import lejos.nxt.ButtonListener;
import lejos.nxt.Motor;
import lejos.nxt.NXT;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.nxt.TouchSensor;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.addon.CompassHTSensor;
import lejos.robotics.navigation.Pose;
import de.fh.zwickau.mindstorms.brick.Robot;
import de.fh.zwickau.mindstorms.brick.communication.ConnectionManager;
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
public class Initializer implements ButtonListener {

	private Robot robot;
	private NXTRegulatedMotor leftMotor;
	private NXTRegulatedMotor rightMotor;
	private NXTRegulatedMotor grabberMotor;
	private TouchSensor touchSensor;
	private CompassHTSensor compassSensor;
	private UltrasonicSensor ultrasonicSensor;
	private boolean hasToCalibrate = false;
	private final double STD_DRIVE_TRANSLATION = 38.0;

	/**
	 * Initialises the NXT and adds a button listener to the escape button, that
	 * will shut down the robot whenever it its pressed.
	 */
	public Initializer() {
		robot=new Robot();
		leftMotor = Motor.A;
		robot.leftMotor = leftMotor;  
		rightMotor = Motor.B;
		robot.rightMotor = rightMotor;
//		grabberMotor = Motor.C;
//		compassSensor = new CompassHTSensor(SensorPort.S2);
//		robot.compassSensor = compassSensor;
		robot.ultrasonicSensor = ultrasonicSensor = new UltrasonicSensor(SensorPort.S1);
		touchSensor = new TouchSensor(SensorPort.S3);
		initialize();
	}

	@Override
	public void buttonPressed(Button b) {
		compassSensor.stopCalibration();
		NXT.shutDown();
	}

	public void initialize() {
		Button.ESCAPE.addButtonListener(this);
		calibrate();

		// establish connection to the server
		ConnectionManager connectionManager = new ConnectionManager(robot);

		// obstacle detection test
//		Button.ENTER.waitForPress();
//		new PositionManager(new Pose(0, 0, 0), robot);
//		new SensorManager(robot);
//		Button.ESCAPE.waitForPress();
	}

	private void calibrate() {
		{
			if (hasToCalibrate) {
				System.out.println("calibrate");
				{
					// calculate driveTranslation
					DriveTranslationCalibrator driveTranslationCalibrator = new DriveTranslationCalibrator(
							leftMotor, rightMotor, ultrasonicSensor);
					robot.driveTranslation = driveTranslationCalibrator
							.getDriveTranslation();

					// calibrate compass sensor
					CompassCalibrator compassCalibrator = new CompassCalibrator(
							leftMotor, rightMotor, compassSensor);

				}
				System.out.println("calibrated");
			} else {
				System.out.println("not Calibrated");
				robot.driveTranslation= STD_DRIVE_TRANSLATION;
			}
			Sound.beep();
		}
	}

	public Robot getRobot() {
		return robot;
	}

	@Override
	public void buttonReleased(Button b) {
		// nothing
	}
}