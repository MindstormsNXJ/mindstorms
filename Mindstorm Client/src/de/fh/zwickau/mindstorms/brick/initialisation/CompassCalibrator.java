package de.fh.zwickau.mindstorms.brick.initialisation;

import de.fh.zwickau.mindstorms.brick.Robot;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.addon.CompassHTSensor;
import lejos.util.Delay;

public class CompassCalibrator {

	private Robot robot;
	private int rotationSpeed;
	private boolean preCalibrationSuccessful;
	private CompassHTSensor compassSensor;
	private NXTRegulatedMotor rightMotor;
	private NXTRegulatedMotor leftMotor;
	
	public CompassCalibrator(Robot robot, NXTRegulatedMotor leftMotor, NXTRegulatedMotor rightMotor, CompassHTSensor compassSensor) {
		this.robot=robot;
		this.leftMotor=leftMotor;
		this.rightMotor=rightMotor;
		this.compassSensor=compassSensor;
		System.out.println("starting compass calibration");
		robot.setModeRotate();
		preCalibrationSuccessful = false;
		preCalibrate();
		calibrate();
	}

	public void preCalibrate() {
		long before = System.currentTimeMillis();		
		final float startDirection = compassSensor.getDegrees();
		if (startDirection == 506) {
			System.out.println("pre calibration failed, please restart NXT");
			return;
		}
		leftMotor.forward();
		rightMotor.backward();
		Thread check = new Thread(new Runnable() {
			
			@Override
			public void run() {
				Delay.msDelay(1000);
				while (leftMotor.isMoving()) {
					float direction = compassSensor.getDegrees();
					if (Math.abs(startDirection - direction) < 2) {
						leftMotor.stop();
						rightMotor.stop();
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
		robot.setRotateSpeed(rotationSpeed);
		robot.setModeRotate();
		compassSensor.startCalibration();
		rightMotor.forward();
		leftMotor.backward();
			try {
				Thread.sleep(40000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		compassSensor.stopCalibration();
		leftMotor.stop();
		rightMotor.stop();
		robot.setModeDrive();
		System.out.println("compass calibration finished");
	}
	
}
