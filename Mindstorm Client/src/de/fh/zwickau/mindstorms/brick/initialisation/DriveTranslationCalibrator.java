package de.fh.zwickau.mindstorms.brick.initialisation;

import java.util.ArrayList;

import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.UltrasonicSensor;
import lejos.robotics.objectdetection.Feature;
import lejos.robotics.objectdetection.FeatureDetector;
import lejos.robotics.objectdetection.FeatureListener;
import lejos.robotics.objectdetection.RangeFeatureDetector;
import de.fh.zwickau.mindstorms.brick.Robot;

public class DriveTranslationCalibrator implements FeatureListener {
	private final int motAcc, rotations, minDistance, maxDistance, interval;
	private int lastTacho;
	private double driveTranslation, lastRange;
	private ArrayList<Double> translations = new ArrayList<Double>();
	private UltrasonicSensor ultrasonicSensor;
	private NXTRegulatedMotor rightMotor;
	private NXTRegulatedMotor leftMotor;

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

	private void reset() {
		leftMotor.setAcceleration(motAcc);
		rightMotor.setAcceleration(motAcc);
		leftMotor.resetTachoCount();
		rightMotor.resetTachoCount();
	}

	public void calibrate() {
		FeatureDetector scanner = new RangeFeatureDetector(ultrasonicSensor,
				maxDistance, interval);
		scanner.addListener(this);
		for (int i = 0; i < 3; i++) {
			reset();
			setSpeed(100 + i * 50);
			rotate(-360 * rotations);
			rotate(360 * rotations);
		}
		double summe = 0;
		for (double trans : translations) {
			summe += trans;
		}
		driveTranslation = summe / translations.size();
		System.out.println("Translation: " + driveTranslation);
	}

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

	private void rotate(int angel) {
		leftMotor.rotate(angel, true);
		rightMotor.rotate(angel, false);
	}

	private int getTacho() {
		return (leftMotor.getTachoCount() + rightMotor.getTachoCount()) / 2;
	}

	private void setSpeed(int speed) {
		leftMotor.setSpeed(speed);
		rightMotor.setSpeed(speed);
	}

	public double getDriveTranslation() {
		return driveTranslation;
	}
}
