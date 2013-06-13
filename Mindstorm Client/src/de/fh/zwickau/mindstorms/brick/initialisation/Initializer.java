package de.fh.zwickau.mindstorms.brick.initialisation;

import lejos.nxt.Button;
import lejos.nxt.ButtonListener;
import lejos.nxt.NXT;
import lejos.nxt.Sound;
import lejos.robotics.navigation.Pose;
import de.fh.zwickau.mindstorms.brick.Robot;
import de.fh.zwickau.mindstorms.brick.communication.ConnectionManager;
import de.fh.zwickau.mindstorms.brick.navigation.PositionManager;

/**
 * This class is responsible for managing all tasks that have to be performed
 * when the NXT starts, e.g. sensor calibration and connection establishment.
 * This includes also the initialisation of the sensors and motors.
 * 
 * @author Tobias Schießl, Markus Krummnacker
 * @version 1.0
 */
public class Initializer implements ButtonListener {

	private enum Mode {
		CENTRALISATION, SERVERMODE, PICKERTEST, TEST;
	}

	private Robot robot;
	private final double STD_DRIVE_TRANSLATION = 35.7 / 10; //38.7 for test environment

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
		
		switch (mode) {
		case SERVERMODE:
			// establish connection to the server
			new ConnectionManager(robot);
			break;
		case CENTRALISATION:
			// try to centralize the Object in front of
			System.out.println("centralize");
			while(true) {
				Button.ENTER.waitForPressAndRelease();
				robot.centralizer.centralize();
			}
//			robot.pickItem();
		case TEST:
			//try new rotation with own Cartesian zero
//			robot.positionManager.rotateTo(180);
//			robot.positionManager.rotateTo(90);
//			robot.positionManager.rotateTo(270);
			Button.ENTER.waitForPress();
			robot.positionManager.move(300);
			robot.positionManager.rotateTo(90);
			robot.positionManager.move(200);
			robot.positionManager.rotateTo(180);
			robot.positionManager.move(300);
			robot.positionManager.rotateTo(270);
			robot.positionManager.move(200);
			robot.positionManager.rotateTo(0);
			
			break;
		case PICKERTEST:
			// testing for the pick and drop mechanism
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
			break;
		}
	}

	public void initialize() {
		Button.ESCAPE.addButtonListener(this);
		calibrate();
		robot.positionManager = new PositionManager(new Pose(0, 0, robot.getDirection()), robot);
	}

	private void calibrate() {
		{
			if (hasToCalibrate) {
				System.out.println("calibrating");
				{
					// calculate driveTranslation
					DriveTranslationCalibrator driveTranslationCalibrator = new DriveTranslationCalibrator(robot.leftMotor, robot.rightMotor, robot.ultrasonicSensor);
					robot.driveTranslation = driveTranslationCalibrator.getDriveTranslation() / 10;
					
					// calibrate compass sensor
					new CompassCalibrator(robot);
				}
				System.out.println("calibrated");
			} else {
				System.out.println("not calibrated");
				robot.driveTranslation = STD_DRIVE_TRANSLATION;
			}
			Sound.beep();
		}
	}

	@Override
	public void buttonPressed(Button b) {
		robot.compassSensor.stopCalibration();
		NXT.shutDown();
	}

	@Override
	public void buttonReleased(Button b) {
		// nothing
	}

}
