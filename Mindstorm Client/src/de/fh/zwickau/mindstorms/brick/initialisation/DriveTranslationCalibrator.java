package de.fh.zwickau.mindstorms.brick.initialisation;

import java.util.ArrayList;

import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.UltrasonicSensor;
import lejos.robotics.objectdetection.Feature;
import lejos.robotics.objectdetection.FeatureDetector;
import lejos.robotics.objectdetection.FeatureListener;
import lejos.robotics.objectdetection.RangeFeatureDetector;

/**
 * This class calibrate the ratio addicted from the underground nature by using
 * the ultrasonic sensor and a vertacally wall in front of the robot
 * 
 * @author Markus Krummnacker
 * @version 1.2
 */
public class DriveTranslationCalibrator implements FeatureListener {
	private final int motAcc, rotations, minDistance, maxDistance, interval;
	private int lastTacho;
	private double driveTranslation, lastRange;
	private ArrayList<Double> translations = new ArrayList<Double>();
	private UltrasonicSensor ultrasonicSensor;
	private NXTRegulatedMotor rightMotor;
	private NXTRegulatedMotor leftMotor;

	/**
	 * start the calibration
	 * 
	 * @param leftMotor
	 * @param rightMotor
	 * @param ultrasonicSensor
	 */
	public DriveTranslationCalibrator(NXTRegulatedMotor leftMotor,
			NXTRegulatedMotor rightMotor, UltrasonicSensor ultrasonicSensor) {
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.ultrasonicSensor = ultrasonicSensor;
		rotations = 3;
		interval = 10;
		minDistance = 20;
		maxDistance = 45;
		motAcc = 500;
		calibrate();
	}

	/**
	 * calibrate the ratio by scan rages during moving three times and evaluate
	 * the correlate array
	 */
	private void calibrate() {
		FeatureDetector scanner = new RangeFeatureDetector(ultrasonicSensor,
				maxDistance, interval);
		scanner.addListener(this);
		for (int i = 0; i < 3; i++) {
			reset();
			setSpeed(100 + i * 50);
			motorRotate(-360 * rotations);
			motorRotate(360 * rotations);
		}
		double summe = 0;
		for (double trans : translations) {
			summe += trans;
		}
		driveTranslation = summe / translations.size();
		System.out.println("Translation: " + driveTranslation);
	}

	/*
	 * (non-Javadoc) scan the distance of the wall to calibrate and if there is
	 * an difference store this and the motor tacho count in an array for
	 * evaluation
	 * 
	 * @see
	 * lejos.robotics.objectdetection.FeatureListener#featureDetected(lejos.
	 * robotics.objectdetection.Feature,
	 * lejos.robotics.objectdetection.FeatureDetector)
	 */
	@Override
	public void featureDetected(Feature feature, FeatureDetector detector) {
		double range = feature.getRangeReading().getRange();
		if (range > minDistance) {
			if (lastRange < range) {
				int tacho = getTacho();
				if (tacho < lastTacho) {
					translations.add((lastTacho - tacho) / (range - lastRange));
					lastRange = range;
					lastTacho = tacho;
				}
			} else if (lastRange > range) {
				int tacho = getTacho();
				if (tacho > lastTacho) {
					translations.add((tacho - lastTacho) / (lastRange - range));
					lastRange = range;
					lastTacho = tacho;
				}
			}
		} else if (range == minDistance) {
			lastRange = feature.getRangeReading().getRange();
			lastTacho = getTacho();
		}
	}

	/**
	 * move the robot by degrees of motor rotation
	 * 
	 * @param angle
	 *            to rotate
	 */
	private void motorRotate(int angle) {
		leftMotor.rotate(angle, true);
		rightMotor.rotate(angle, false);
	}

	/**
	 * set the motor tacho count back and set the acceleration speed to define
	 * value (500)
	 */
	private void reset() {
		leftMotor.setAcceleration(motAcc);
		rightMotor.setAcceleration(motAcc);
		leftMotor.resetTachoCount();
		rightMotor.resetTachoCount();
	}

	private void setSpeed(int speed) {
		leftMotor.setSpeed(speed);
		rightMotor.setSpeed(speed);
	}

	private int getTacho() {
		return (leftMotor.getTachoCount() + rightMotor.getTachoCount()) / 2;
	}

	public double getDriveTranslation() {
		return driveTranslation;
	}
}
