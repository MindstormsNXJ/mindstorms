package sensor.compass;

import lejos.nxt.addon.CompassHTSensor;
import lejos.nxt.Button;
import lejos.nxt.ButtonListener;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.util.Delay;

/**
 * The auto calibrator calibrates the compass sensor automatically as the name
 * says. Therefore, it takes the time the robot needs to turn 360 degrees on
 * the current underground, then adapts the speed and lets the robot turn 2 times
 * with smaller speed.
 * 
 * ***IMPORTANT***
 * When stopping the robot while calibrating, it is important to stop the 
 * calibration mode first!!!
 * 
 * @author Patrick Rosenkranz
 * @version 1.1
 */
public class AutoCalibrator{
	
	private CompassHTSensor compassSensor;
	private NXTRegulatedMotor motB;
	private NXTRegulatedMotor motA;
	private long runningTime;
	private int motSpeed;
	
	/**
	 * Initialises a new auto calibrator and it's motors.
	 */
	public AutoCalibrator() {
		Button.ESCAPE.addButtonListener(new EscapeButtonListener());
		motB = Motor.B;
		motA = Motor.A;
		SensorPort.S2.reset();
		compassSensor = new CompassHTSensor(SensorPort.S2);
		compassSensor.stopCalibration();
		motSpeed = 200;
		motA.setSpeed(motSpeed);
		motB.setSpeed(motSpeed);
	}

	public static void main(String[] args) {
		AutoCalibrator wso = new AutoCalibrator();
		boolean success = wso.preCalibrate();
		if (success)
			wso.Calibrate();
		else {
			System.out.println("pre calibration not successful");
		}
		Button.ENTER.waitForPress();
		wso.turn(0);
		Button.ENTER.waitForPress();
	}

	/**
	 * Starts the pre calibration, which is the 360 degrees rotation.
	 * Also calculates the new speed after this rotation.
	 * 
	 * @return true if pre calibration was successful
	 */
	public boolean preCalibrate() {
		long before = System.currentTimeMillis();		
		final float startDirection = compassSensor.getDegrees();
		if (startDirection == 506) 
			return false;
		System.out.println("Start direction: " + startDirection);
		motB.forward();
		motA.backward();
		Thread check = new Thread(new Runnable() {
			
			@Override
			public void run() {
				Delay.msDelay(1000);
				while (motB.isMoving()) {
					float direction = compassSensor.getDegrees();
					if (Math.abs(startDirection - direction) < 2) {
						motB.stop();
						motA.stop();
					}
					Delay.msDelay(10);
				}
				System.out.println("Final direction: " + compassSensor.getDegrees());
			}
			
		});
		check.start();
		try {
			check.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		long after = System.currentTimeMillis();
		runningTime = after - before;
		System.out.println("Time: " + runningTime + "ms");
		motSpeed = (int) ((motSpeed * runningTime) / 20000);
		System.out.println("Speed: " + motSpeed);
		return true;
	}

	/**
	 * Need preCalibrate() to know how much speed is needed for the calibration.
	 */
	public void Calibrate() {
		motA.setSpeed(motSpeed);
		motB.setSpeed(motSpeed);
		compassSensor.startCalibration();
		motA.forward();
		motB.backward();
			try {
				Thread.sleep(40000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		compassSensor.stopCalibration();
		motB.stop();
		motA.stop();
		System.out.println("finished");
	}	
	
	private void turn(int degrees) {
		motA.forward();
		motB.backward();
		while (motA.isMoving()) {
			int currentDeg = (int) compassSensor.getDegrees();
			if (Math.abs(currentDeg - degrees) < 2) {
				motA.stop();
				motB.stop();
			}
		}
	}

	private class EscapeButtonListener implements ButtonListener {
		
		@Override
		public void buttonPressed(Button b) {
			compassSensor.stopCalibration();
			System.exit(0);
		}
	
		@Override
		public void buttonReleased(Button b) {	}	
		
	}	
	
}
