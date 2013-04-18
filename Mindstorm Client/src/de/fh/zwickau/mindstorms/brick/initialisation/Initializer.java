package de.fh.zwickau.mindstorms.brick.initialisation;

import lejos.nxt.Button;
import lejos.nxt.ButtonListener;
import lejos.nxt.Motor;
import lejos.nxt.NXT;
import lejos.nxt.SensorPort;
import lejos.nxt.addon.CompassHTSensor;
import de.fh.zwickau.mindstorms.brick.Robot;

/**
 * This class is responsible for managing all tasks that have to be performed
 * when the NXT starts, e.g. sensor calibration and connection establishment.
 * This includes also the initialisation of the sensors and motors.
 * 
 * @author Tobias Schießl
 * @version 1.0
 */
public class Initializer {

	/**
	 * Initialises the NXT and adds a button listener to the escape button, that will shut
	 * down the robot whenever it its pressed.
	 */
	public void initialize() {
		//create NXT and init sensors
		String id = ""; //TODO get id from running NXT and change type depending on result
		final Robot thisRobot = new Robot(id){};
		thisRobot.leftMotor = Motor.A;
		thisRobot.rightMotor = Motor.B;
		thisRobot.compassSensor = new CompassHTSensor(SensorPort.S2);
		
		//add the universal listener to stop the robot
		Button.ESCAPE.addButtonListener(new ButtonListener() {
			
			@Override
			public void buttonReleased(Button b) {
				//nothing
			}
			
			@Override
			public void buttonPressed(Button b) {
				thisRobot.compassSensor.stopCalibration();
				NXT.shutDown();
			}
			
		});
		
		//TODO calibrate ultrasonic sensor
		
		//calibrate compass sensor
		CompassCalibrator compassCalibrator = new CompassCalibrator(thisRobot);
		compassCalibrator.preCalibrate();
		compassCalibrator.calibrate();
		
		//TODO establish connection to the server
		
	}
}