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
		compassSensor = new CompassHTSensor(SensorPort.S1);
		setMotSpeed(50);
		motA.setSpeed(motSpeed);
		motB.setSpeed(motSpeed);
	}

	public static void main(String[] args) {
		AutoCalibrator wso = new AutoCalibrator();
		wso.preCalibrate();
		wso.Calibrate();
		Button.ESCAPE.waitForPress();
	}

	/**
	 * Starts the pre calibration, which is the 360 degrees rotation.
	 * Also calculates the new speed after this rotation.
	 */
	public void preCalibrate() {
		long before = System.currentTimeMillis();		
		final float startDirection = compassSensor.getDegrees();
		final int BUFFER = 40;
		motB.forward();
		motA.backward();
		Thread check = new Thread(new Runnable() {
			
			@Override
			public void run() {
				Delay.msDelay(1000);
				while (motB.isMoving()) {
					float direction = compassSensor.getDegrees();
					if (Math.abs(((startDirection + BUFFER) % 360) - direction) < 2) {
						motB.stop();
						motA.stop();
					}
					Delay.msDelay(10);
				}
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
		System.out.println(runningTime);
		setMotSpeed((int) (getMotSpeed()*2000 / (runningTime)));
		System.out.println("Speed :" +getMotSpeed());
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
		System.out.println("Kalibration fertig");
	}	
	
	private class EscapeButtonListener implements ButtonListener {
		
		@Override
		public void buttonPressed(Button b) {
			System.exit(0);
		}

		@Override
		public void buttonReleased(Button b) {	}	
		
	}
	
	public int getMotSpeed() {
		return motSpeed;
	}

	public void setMotSpeed(int motSpeed) {
		this.motSpeed = motSpeed;
	}	
	
}
