package sensor.compass;

import lejos.nxt.*;
import lejos.nxt.addon.CompassHTSensor;

public class CalibrationStore {

	private CompassHTSensor compassSensor;
	
	public CalibrationStore() {
		Button.ESCAPE.addButtonListener(new EscapeButtonListener());
		compassSensor = new CompassHTSensor(SensorPort.S1);
		calibrate();
		showDegreesOnDisplay(20);
//		final NXTRegulatedMotor motor = Motor.A;
//		motor.setSpeed(5);
//		motor.forward();
//		try {
//			Thread.sleep(2000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		motor.stop();
//		while (true) {
//			System.out.println(compassSensor.getDegrees());
//		}
	}
	
	private void calibrate() {
		//16 seconds for one turn --> 4 seconds for 90 degrees
		compassSensor.startCalibration();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				int second = 0;
				while (second <= 16) {
					System.out.println(second);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					second++;
				}
			}
			
		}).start();
		try {
			Thread.sleep(16000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		compassSensor.stopCalibration();
		System.out.println("Calibration finished");
	}
	
	private void showDegreesOnDisplay(final int turns) {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				int counter = 0;
				while (counter < turns) {
					System.out.println(compassSensor.getDegrees());
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					counter++;
				}
			}
			
		}).start();
	}
	
	public static void main(String args[]) {
		new CalibrationStore();
	}

	public class EscapeButtonListener implements ButtonListener {

		@Override
		public void buttonPressed(Button b) {
			System.exit(0);
		}

		@Override
		public void buttonReleased(Button b) {
			//nothing
		}
		
	}
	
}
