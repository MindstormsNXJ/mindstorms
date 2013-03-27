package sensor.compass;

import lejos.nxt.*;
import lejos.nxt.addon.CompassHTSensor;

public class CalibrationStore {

	private CompassHTSensor compassSensor;
	private NXTRegulatedMotor motorA;
	private NXTRegulatedMotor motorB;
	
	public CalibrationStore() {
		Button.ESCAPE.addButtonListener(new EscapeButtonListener());
		Button.LEFT.addButtonListener(new StopCalibrationButtonListener());
		compassSensor = new CompassHTSensor(SensorPort.S1);
		motorA = Motor.A; 
		motorB = Motor.B;
		int motorSpeed = 216;
		motorA.setSpeed(motorSpeed);
		motorB.setSpeed(motorSpeed);		
		calibrate();
		try {
			Thread.sleep(50000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		showDegreesForTwentySeconds();
		Button.ESCAPE.waitForPress();
	}
	
	private void showDegreesForTwentySeconds() {
		motorA.forward();
		motorB.backward();
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

	private void calibrate() {
		compassSensor.startCalibration();
		motorA.forward();
		motorB.backward();
	}
	
	public static void main(String args[]) {
		new CalibrationStore();
	}

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
