package de.fh.zwickau.mindstorms.brick.task;

import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.Sound;
import lejos.nxt.TouchSensor;
import lejos.nxt.UltrasonicSensor;
import lejos.util.Delay;
import de.fh.zwickau.mindstorms.brick.Robot;
import de.fh.zwickau.mindstorms.brick.navigation.Direction;

/**
 * 
 * @author simon
 *
 */
public class Pick {
	private Robot robot;
	private TouchSensor touchSensor;
	private NXTRegulatedMotor grabberMotor;
	private int wayDown = 360;
	private int itemDistance = 25;
	private UltrasonicSensor sensor;
	

	public Pick(Robot r){
		robot = r;
		grabberMotor = robot.grabberMotor;
		touchSensor = robot.touchSensor;
		sensor = robot.ultrasonicSensor;
//		pickerUp();
		takeItem();
//		pickerDown();
//		pickerUp();
//		pickerDown();
//		robot.positionManager.move(10);
	}
	
	public void pickerUp(){
		grabberMotor.resetTachoCount();
		grabberMotor.setSpeed(250);
		System.out.println(grabberMotor.getTachoCount());
		grabberMotor.forward();
		boolean up = false;
//		while(up || !up){
//			System.out.println(robot.ultrasonicSensor.getDistance());
//		}
		while(!up){
			if(touchSensor.isPressed() || (grabberMotor.getTachoCount() > 360)){
				Sound.beep();
				grabberMotor.stop();
				up = true;
			}
			
		}
		System.out.println(grabberMotor.getTachoCount());
		
		Delay.msDelay(1000);
		
	}
	
	public void pickerDown(){
		grabberMotor.resetTachoCount();
//		System.out.println("down");
		int currentTacho = grabberMotor.getTachoCount();
		int targetTacho = currentTacho - wayDown;
//		System.out.println(picker.getTachoCount());
//		boolean move = true;
		grabberMotor.setSpeed(100);
		grabberMotor.backward();
//		Delay.msDelay(500);
		boolean down = true;
		while(down){
			if(grabberMotor.getTachoCount() < targetTacho){
				down = false;
				grabberMotor.stop();
			}
		}
		
//		System.out.println(picker.getTachoCount());
//		Delay.msDelay(4000);
	}
	
	public void takeItem(){
		pickerUp();
		int distance = sensor.getDistance();
		if(distance < 250){
			int driveDist = distance - itemDistance;
			System.out.println(driveDist);
			robot.positionManager.move(driveDist);
			pickerDown();
			robot.positionManager.move(9);
			Delay.msDelay(1000);
			pickerUp();
			robot.positionManager.rotate(45, Direction.RIGHT);
			pickerDown();
			
		}
		else{
			System.out.println("nothing to pick");
		}
		
		Delay.msDelay(2000);
		
	}

}
