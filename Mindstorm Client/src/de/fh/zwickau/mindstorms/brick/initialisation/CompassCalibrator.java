package de.fh.zwickau.mindstorms.brick.initialisation;

import de.fh.zwickau.mindstorms.brick.Robot;
import lejos.util.Delay;

public class CompassCalibrator {

	private Robot robot;
	private int motSpeed;
	
	public CompassCalibrator(Robot robot) {
		this.robot = robot;
		motSpeed = 200;
		robot.leftMotor.setSpeed(motSpeed);
		robot.leftMotor.setSpeed(motSpeed);
	}

	public void preCalibrate() {
		long before = System.currentTimeMillis();		
		final float startDirection = robot.compassSensor.getDegrees();
		if (startDirection == 506) {
			System.out.println("pre calibration failed, please restart program");
			return;
		}
		robot.leftMotor.forward();
		robot.rightMotor.backward();
		Thread check = new Thread(new Runnable() {
			
			@Override
			public void run() {
				Delay.msDelay(1000);
				while (robot.leftMotor.isMoving()) {
					float direction = robot.compassSensor.getDegrees();
					if (Math.abs(startDirection - direction) < 2) {
						robot.leftMotor.stop();
						robot.rightMotor.stop();
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
		long runningTime = after - before;
		motSpeed = (int) ((motSpeed * runningTime) / 20000);
	}

	public void calibrate() {
		robot.rightMotor.setSpeed(motSpeed);
		robot.leftMotor.setSpeed(motSpeed);
		robot.compassSensor.startCalibration();
		robot.rightMotor.forward();
		robot.leftMotor.backward();
			try {
				Thread.sleep(40000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		robot.compassSensor.stopCalibration();
		robot.leftMotor.stop();
		robot.rightMotor.stop();
		System.out.println("compass calibration finished");
	}
	
}
