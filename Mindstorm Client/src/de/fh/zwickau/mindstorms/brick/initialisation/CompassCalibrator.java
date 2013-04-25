package de.fh.zwickau.mindstorms.brick.initialisation;

import de.fh.zwickau.mindstorms.brick.Robot;
import lejos.util.Delay;

public class CompassCalibrator {

	private Robot robot;
	private int rotationSpeed;
	private boolean preCalibrationSuccessful;
	
	public CompassCalibrator(Robot robot) {
		System.out.println("starting compass calibration");
		this.robot = robot;
		robot.setModeRotate();
		preCalibrationSuccessful = false;
	}

	public void preCalibrate() {
		long before = System.currentTimeMillis();		
		final float startDirection = robot.compassSensor.getDegrees();
		if (startDirection == 506) {
			System.out.println("pre calibration failed, please restart NXT");
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
		rotationSpeed = (int) ((rotationSpeed * runningTime) / 20000);
		preCalibrationSuccessful = true;
	}

	public void calibrate() {
		if (!preCalibrationSuccessful) {
			System.out.println("pre calibration is necessary before calibration compass sensor");
			return;
		}
		robot.rotateSpeed = rotationSpeed;
		robot.setModeRotate();
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
		robot.setModeDrive();
		System.out.println("compass calibration finished");
	}
	
}
