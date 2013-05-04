package de.fh.zwickau.mindstorms.brick.task;

import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.Sound;
import lejos.nxt.TouchSensor;
import lejos.robotics.navigation.Pose;
import lejos.util.Delay;
import de.fh.zwickau.mindstorms.brick.Robot;
import de.fh.zwickau.mindstorms.brick.initialisation.DriveTranslationCalibrator;
import de.fh.zwickau.mindstorms.brick.navigation.MovementManager;
import de.fh.zwickau.mindstorms.brick.navigation.PositionManager;

public class Pick {
	private Robot robot;
	private TouchSensor touchSensor;
	private NXTRegulatedMotor picker;
	

	public Pick(Robot r){
		robot = r;
		picker = robot.picker;
		touchSensor = robot.touchSensor;
		calibratePicker();
		Sound.beep();
	}
	
	public void calibratePicker(){
		picker.setSpeed(80);
		System.out.println(picker.getTachoCount());
		robot.picker.forward();
		boolean up = false;
		while(!up){
			if(touchSensor.isPressed()){
				Sound.beep();
				robot.picker.stop();
				up = true;
			}
			
		}
		System.out.println(picker.getTachoCount());
		
		Delay.msDelay(4000);
		
	}
	
	

}
