package sensor.compass;

import lejos.nxt.addon.CompassHTSensor;
import lejos.nxt.Button;
import lejos.nxt.ButtonListener;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.robotics.navigation.CompassPilot;
import lejos.util.Delay;


@SuppressWarnings("deprecation")
public class WayStoneOne{
	
	private CompassHTSensor compassSensor;
	private NXTRegulatedMotor motB;
	private NXTRegulatedMotor motC;
	private long runningTime;
	private int motSpeed;
	
	public WayStoneOne() {
		Button.ESCAPE.addButtonListener(new EscapeButtonListener());
		motB = Motor.B;
		motC = Motor.C;
		compassSensor = new CompassHTSensor(SensorPort.S1);
		setMotSpeed(50);
		motC.setSpeed(motSpeed);
		motB.setSpeed(motSpeed);
	}

	public static void main(String[] args) {
		WayStoneOne wso = new WayStoneOne();
		wso.preCalibrate();
		wso.Calibrate();
		Button.ESCAPE.waitForPress();
	}

	public void preCalibrate() {
		long before = System.currentTimeMillis();		
		final float startDirection = compassSensor.getDegrees();
		final int BUFFER = 40;
		motB.forward();
		motC.backward();
		Thread check = new Thread(new Runnable() {
			
			@Override
			public void run() {
				Delay.msDelay(1000);
				while (motB.isMoving()) {
					float direction = compassSensor.getDegrees();
					if (Math.abs(((startDirection + BUFFER) % 360) - direction) < 2) {
						motB.stop();
						motC.stop();
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
	 * Need pre Calibrate ² know how much speed it need for a whole Round
	 */
	public void Calibrate() {
		motC.setSpeed(motSpeed);
		motB.setSpeed(motSpeed);
		compassSensor.startCalibration();
		motC.forward();
		motB.backward();
			try {
				Thread.sleep(40000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		compassSensor.stopCalibration();
		motB.stop();
		motC.stop();
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
