package de.fh.zwickau.mindstorms.brick.task;

import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.Sound;
import lejos.nxt.TouchSensor;
import lejos.nxt.UltrasonicSensor;
import lejos.util.Delay;
import de.fh.zwickau.mindstorms.brick.Robot;

/**
 * Class which enables a robot to use it's grabber. This class provides methods as pickerUp, 
 * pickerDown, take and drop item.
 * @author simon
 *
 */
public class Pick {
	private Robot robot;
	private TouchSensor touchSensor;
	private NXTRegulatedMotor grabberMotor;
	private int wayDown = 480;
	private int itemDistance = 25;
	private UltrasonicSensor sensor;
	

	public Pick(Robot r){
		robot = r;
		grabberMotor = robot.grabberMotor;
		touchSensor = robot.touchSensor;
		sensor = robot.ultrasonicSensor;
//		pickerUp();
//		takeItem();
//		pickerDown();
//		pickerUp();
//		pickerDown();
//		robot.positionManager.move(10);
	}
	
	/**
	 * Lift the grabber up, regardless if there is an item to pick.
	 * To pick an item use pickItem().
	 */
	public void pickerUp(){
		grabberMotor.resetTachoCount();
		grabberMotor.setSpeed(300);
		grabberMotor.forward();
		boolean up = false;
		while(!up){
			if(touchSensor.isPressed() /*|| (grabberMotor.getTachoCount() > 330)*/ ){
				Sound.beep();
				grabberMotor.stop();
				up = true;
			}
		}
	}
	
	/**
	 * Move the picker down. When this method is called the picker should be UP!
	 */
	public void pickerDown(){
		if(!touchSensor.isPressed())
			pickerUp();
		grabberMotor.resetTachoCount();
		grabberMotor.setSpeed(100);
		grabberMotor.backward();
		boolean down = true;
		while(down){
			if(grabberMotor.getTachoCount() < - wayDown){
				down = false;
				grabberMotor.stop();
			}
		}
	}
	
	/**
	 * Method to pick an item which is straight ahead of the robot
	 */
	public void pickItem(){
		pickerUp();
		int distance = sensor.getDistance();
		if(distance < 250){
			int driveDist = distance - itemDistance;
			System.out.println(driveDist);
			robot.positionManager.move(driveDist);
			pickerDown();
			robot.positionManager.move(9);
			pickerUp();
		}
		else{
			System.out.println("nothing to pick");
			Sound.beepSequence();
		}
		Delay.msDelay(2000);
		
	}

}
