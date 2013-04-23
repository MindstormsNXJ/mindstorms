package de.fh.zwickau.mindstorms.brick.initialisation;

import java.util.ArrayList;

import lejos.robotics.objectdetection.Feature;
import lejos.robotics.objectdetection.FeatureDetector;
import lejos.robotics.objectdetection.FeatureListener;
import lejos.robotics.objectdetection.RangeFeatureDetector;
import de.fh.zwickau.mindstorms.brick.Robot;

public class DriveTranslationCalibrator implements FeatureListener {
	private Robot robot;
	private final int motAcc, rotations, minDistance, maxDistance, interval;
	private int lastTacho;
	private double driveTranslation, lastRange;
	private ArrayList<Double> translations = new ArrayList<Double>();

	public DriveTranslationCalibrator(Robot robot) {
		this.robot = robot;
		rotations = 3;
		interval = 10;
		minDistance = 20;
		maxDistance = 45;
		motAcc = 500;
	}

	private void reset() {
		robot.leftMotor.setAcceleration(motAcc);
		robot.rightMotor.setAcceleration(motAcc);
		robot.leftMotor.resetTachoCount();
		robot.rightMotor.resetTachoCount();
	}

	public void calibrate() {
		FeatureDetector scanner = new RangeFeatureDetector(
				robot.ultrasonicSensor, maxDistance, interval);
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
		driveTranslation= summe/translations.size();
		System.out.println("Translation: " + driveTranslation);
		robot.driveTranslation = driveTranslation;
	}

	@Override
	public void featureDetected(Feature feature, FeatureDetector detector) {
		double range = feature.getRangeReading().getRange();
		if (range > minDistance) {
			if (lastRange < range) {
				int tacho = robot.leftMotor.getTachoCount();
				if (tacho < lastTacho) {
					translations.add((lastTacho - tacho) / (range - lastRange));
					lastRange = range;
					lastTacho = tacho;
				}
			} else if (lastRange > range) {
				int tacho = robot.leftMotor.getTachoCount();
				if (tacho > lastTacho) {
					translations.add((tacho - lastTacho) / (lastRange - range));
					lastRange = range;
					lastTacho = tacho;
				}
			}
		} else if (range == minDistance) {
			lastRange = feature.getRangeReading().getRange();
			lastTacho = robot.leftMotor.getTachoCount();
		}
	}

	private void rotate(int angel) {
		robot.leftMotor.rotate(angel, true);
		robot.rightMotor.rotate(angel, false);
	}

	private void setSpeed(int speed) {
		robot.leftMotor.setSpeed(speed);
		robot.rightMotor.setSpeed(speed);
	}
}
