package de.fh.zwickau.mindstorms.brick.initialisation;

import lejos.nxt.Button;
import lejos.nxt.ButtonListener;
import lejos.nxt.NXT;
import lejos.nxt.Sound;
import lejos.robotics.navigation.Pose;
import de.fh.zwickau.mindstorms.brick.Robot;
import de.fh.zwickau.mindstorms.brick.communication.ConnectionManager;
import de.fh.zwickau.mindstorms.brick.navigation.Direction;
import de.fh.zwickau.mindstorms.brick.navigation.PositionManager;
import de.fh.zwickau.mindstorms.brick.sensors.ObjectCentralisation;

/**
 * This class is responsible for managing all tasks that have to be performed
 * when the NXT starts, e.g. sensor calibration and connection establishment.
 * This includes also the initialisation of the sensors and motors.
 * 
 * @author Tobias Schießl
 * @version 1.0
 */
public class Initializer implements ButtonListener {

	private enum Mode {
		CENTRALISATION, SERVERMODE, PICKERTEST, TEST;
	}

	private Robot robot;
	private final double STD_DRIVE_TRANSLATION = 38.0 / 10;

	// config Flags and Enums
	private boolean hasToCalibrate = false;
	private Mode mode = Mode.SERVERMODE;

	/**
	 * Initialises the NXT and adds a button listener to the escape button, that
	 * will shut down the robot whenever it its pressed.
	 */
	public Initializer() {

		robot = new Robot();
		initialize();
		// establish connection to the server
		if (mode == Mode.SERVERMODE) {
			robot.positionManager.rotateTo(0);
			new ConnectionManager(robot);
		}

		// try to centralize the Object in front of
		if (mode == Mode.CENTRALISATION) {
			new ObjectCentralisation(robot);
		}

		// place testing here
		if (mode == Mode.TEST) {

		}

		if (mode == Mode.PICKERTEST) {
			for (int i = 0 ; i < 1 ; i++ ){
				robot.pickItem();
//				robot.picker.pickerDown();
				robot.dropItem();
				robot.positionManager.move(-20);
			}
//			robot.picker.pickerUp();
//			robot.picker.pickerDown();
//			robot.picker.pickerUp();
//			robot.picker.pickerDown();
//			robot.picker.pickerUp();
//			robot.picker.pickerDown();
//			robot.positionManager.rotate(90, Direction.RIGHT);
//			robot.putDown();
//			robot.positionManager.move(-10);
		}
	}

	@Override
	public void buttonPressed(Button b) {
		robot.compassSensor.stopCalibration();
		NXT.shutDown();
	}

	public void initialize() {
		Button.ESCAPE.addButtonListener(this);
		calibrate();
		robot.positionManager = new PositionManager(new Pose(0, 0, robot.getDirection()), robot);
	}

	private void calibrate() {
		{
			if (hasToCalibrate) {
				System.out.println("calibrate");
				{
					// calculate driveTranslation
					DriveTranslationCalibrator driveTranslationCalibrator = new DriveTranslationCalibrator(
							robot.leftMotor, robot.rightMotor, robot.ultrasonicSensor);
					robot.driveTranslation = driveTranslationCalibrator.getDriveTranslation() / 10;

					// calibrate compass sensor
					new CompassCalibrator(
							robot.leftMotor, robot.rightMotor, robot.compassSensor);

				}
				System.out.println("calibrated");
			} else {
				System.out.println("not calibrated");
				robot.driveTranslation = STD_DRIVE_TRANSLATION;
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