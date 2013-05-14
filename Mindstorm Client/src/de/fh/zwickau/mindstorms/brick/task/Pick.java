package de.fh.zwickau.mindstorms.brick.task;

import lejos.nxt.ColorSensor.Color;
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
	private int wayDown = 460;
	private int itemDistance = 25;
	private UltrasonicSensor sensor;
	

	public Pick(Robot r){
		robot = r;
		grabberMotor = robot.grabberMotor;
		touchSensor = robot.touchSensor;
		sensor = robot.ultrasonicSensor;
	}
	
	/**
	 * Lift the grabber up, regardless if there is an item to pick.
	 * To pick an item use pickItem().
	 */
	public void pickerUp(){
		grabberMotor.resetTachoCount();
		grabberMotor.setSpeed(150);
		grabberMotor.forward();
		boolean up = false;
		while(!up){
			if(touchSensor.isPressed() || (grabberMotor.getTachoCount() > 700)){
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
		grabberMotor.setSpeed(150);
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
	 * @return true if the item was picked correctly
	 */
	public boolean pickItem(){
		pickerUp();
		int distance = sensor.getDistance();
		if(distance < 250){
			int driveDist = distance - itemDistance;
			System.out.println(driveDist);
			robot.positionManager.move(driveDist);
			pickerDown();
			robot.positionManager.move(12);
			pickerUp();
			
			// delay and some movement to ensure the ball is still in the picker
			Delay.msDelay(3000);
			robot.positionManager.move(-5);
			robot.positionManager.move(5);
			
			// check the color sensor, if the blue ball was picked
			if(robot.colorSensor.getColorID() == 2){
				Sound.beepSequenceUp();
				System.out.println("blue");
				return true;
			}
			else{
				System.out.println("no color");
				return false;
			}
		}
		else{	// no item was found
			System.out.println("nothing to pick");
//			Sound.beepSequence();
			return false;
		}
		
	}
	
	public void dropItem(){
		grabberMotor.resetTachoCount();
		boolean down = true;
		grabberMotor.setSpeed(50);
		grabberMotor.backward();
		while(down){
			if(grabberMotor.getTachoCount() < - wayDown || robot.colorSensor.getColorID() != 2)
				down = false;
		}
		pickerUp();
	}

}
