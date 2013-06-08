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
	private UltrasonicSensor sensor;
	private int wayDown = 400;			// tacho count of grabber motor to move grabber down
	private int itemDistance = 250;		// at this distance the robot stops and put the grabber down
	private int targetDistance = 130;   // the rest distance to the final target where the robot will stop
	

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
		long startTime = System.currentTimeMillis();
		boolean up = false;
		while(!up){
			if(touchSensor.isPressed() || (System.currentTimeMillis() - startTime) > 8000){
				Sound.beep();
				grabberMotor.stop();
				up = true;
			}
		}
		grabberMotor.rotateTo(grabberMotor.getTachoCount() - 60);
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
		int distance = sensor.getDistance() * 10;	// for mm
		if(distance < 2500){
			int driveDist = distance - itemDistance;
			System.out.println(driveDist);
			robot.positionManager.move(driveDist);
			pickerDown();
			robot.positionManager.move(120);
			pickerUp();
			
			// delay and some movement to ensure the ball is still in the picker
			Delay.msDelay(2500);
			robot.positionManager.move(-10);
			robot.positionManager.move(10);
			
			// check the color sensor, if the blue ball was picked
			if(robot.colorSensor.getColorID() == 2){
				Sound.beepSequenceUp();
				System.out.println("blue");
				grabberMotor.rotateTo(grabberMotor.getTachoCount() - 60);
				return true;
			}
			else{
				System.out.println("no color");
				pickerDown();
				robot.positionManager.move(-50);
				pickerUp();
				return false;
			}
		}
		else{	// no item was found
			System.out.println("nothing to pick");
			return false;
		}
		
	}
	
	public void dropItem(){
		int distance = sensor.getDistance() * 10; // for mm
		int driveDist = distance - targetDistance;
		// System.out.println(driveDist);
		robot.positionManager.move(driveDist);
		grabberMotor.resetTachoCount();
		boolean down = true;
		grabberMotor.setSpeed(50);
		grabberMotor.backward();
		while(down){
			if(grabberMotor.getTachoCount() < - wayDown || robot.colorSensor.getColorID() != 2)
				down = false;
		}
		grabberMotor.stop();
		robot.positionManager.move(-100);
		pickerUp();
	}

}
