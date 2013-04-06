package sensor.compass;

import lejos.nxt.*;
import lejos.nxt.addon.CompassHTSensor;

/**
 * A simple test class in order to evaluate the usage of the compass sensor.
 * 
 * @author Patrick Rosenkranz & Tobias Schießl
 * @version 1.0
 */
public class ManuelCalibrator {

	private CompassHTSensor compassSensor;
	private NXTRegulatedMotor motorA;
	private NXTRegulatedMotor motorB;
	
	/**
	 * Initialises a Calibrator and starts the calibration. After the calibration
	 * is finished, the robot will wait 5 seconds and either starts printing the current
	 * degrees on the screen afterwards or holds it's current direction.
	 */
	public ManuelCalibrator() {
		Button.ESCAPE.addButtonListener(new EscapeButtonListener());
		Button.LEFT.addButtonListener(new StopCalibrationButtonListener());
		compassSensor = new CompassHTSensor(SensorPort.S1);
		motorA = Motor.A; 
		motorB = Motor.B;		
		calibrate();
		Button.LEFT.waitForPress(); //wait till calibration is finished
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		showDegrees();
		holdDirection();
		Button.ESCAPE.waitForPress();
	}
	
	/**
	 * Starts the calibration of the compass sensor. The calibration has to be
	 * stopped by hand.
	 */
	private void calibrate() {
		int motorSpeed = 216;
		motorA.setSpeed(motorSpeed);
		motorB.setSpeed(motorSpeed);
		compassSensor.startCalibration();
		motorA.forward();
		motorB.backward();
	}

	/**
	 * Lets the robot rotate and print the current degrees on the display.
	 * The rotation has to be stopped manually.
	 */
	private void showDegrees() {
		motorA.forward();
		motorB.forward();
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (motorA.isMoving()) {
					System.out.println(compassSensor.getDegrees());
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			
		}).start();
	}
	
	/**
	 * Reads the current direction and tries to hold it.
	 */
	private void holdDirection() {
		final float directionToHold = compassSensor.getDegrees();
		DirectionManager holder = new DirectionManager(250, directionToHold, motorB, motorA, compassSensor);
		holder.start();
	}

	public static void main(String args[]) {
		new ManuelCalibrator();
	}

	/**
	 * A button listener that will stop the entire program.
	 * It will not check which button is pressed, so it can be added to any button
	 * you want.
	 * 
	 * @author Tobias Schießl
	 * @version 1.0
	 */
	private class EscapeButtonListener implements ButtonListener {

		@Override
		public void buttonPressed(Button b) {
			System.exit(0);
		}

		@Override
		public void buttonReleased(Button b) {
			//nothing
		}
		
	}
	
	/**
	 * A button listener that will stop the calibration and motors.
	 * It will not check which button is pressed, so it can be added to any button
	 * you want.
	 * 
	 * @author Tobias Schießl
	 * @version 1.0
	 */
	private class StopCalibrationButtonListener implements ButtonListener {

		@Override
		public void buttonPressed(Button b) {
			compassSensor.stopCalibration();
			motorA.stop();
			motorB.stop();
		}

		@Override
		public void buttonReleased(Button b) {
			//nothing
		}
		
	}
	
}
