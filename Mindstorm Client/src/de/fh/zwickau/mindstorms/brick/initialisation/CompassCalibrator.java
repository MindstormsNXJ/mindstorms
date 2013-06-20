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

	public CompassCalibrator(Robot robot) {
		this.robot = robot;
		leftMotor = robot.leftMotor;
		rightMotor = robot.rightMotor;
		compassSensor = robot.compassSensor;
		System.out.println("starting compass calibration");
		robot.setModeRotate();
		rotationSpeed = robot.rotationSpeed;
		preCalibrationSuccessful = false;
		preCalibrate();
		calibrate();
	}

	public void preCalibrate() {
		long before = System.currentTimeMillis();
		final float startDirection = robot.getDirection();
		if (startDirection == 506) {
			System.out.println("pre calibration failed, please restart NXT");
			return;
		}
		leftMotor.forward();
		rightMotor.backward();
		Delay.msDelay(3000);
		while (leftMotor.isMoving()) {
			float direction = robot.getDirection();
			if (Math.abs(startDirection - direction) < 2) {
				leftMotor.stop(true);
				rightMotor.stop(false);
			}
			Delay.msDelay(10);
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
		System.out.println(rotationSpeed);
		robot.setMotorSpeed(rotationSpeed);
		compassSensor.startCalibration();
		rightMotor.forward();
		leftMotor.backward();
		try {
			Thread.sleep(40000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		compassSensor.stopCalibration();
		leftMotor.stop(true);
		rightMotor.stop(false);
		System.out.println("compass calibration finished");
	}

}
