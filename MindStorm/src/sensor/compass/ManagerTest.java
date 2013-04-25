package sensor.compass;

import lejos.nxt.*;
import lejos.nxt.addon.CompassHTSensor;
import lejos.util.Delay;

/**
 * A simple test class to demonstrate the DirectionManager.
 * 
 * @author Tobias Schießl
 * @version 1.0
 */
public class ManagerTest {

	public ManagerTest() {
		NXTRegulatedMotor leftMotor = Motor.B;
		NXTRegulatedMotor rightMotor = Motor.A;
		CompassHTSensor compassSensor = new CompassHTSensor(SensorPort.S1);
		int diretion = 90;
		int motorSpeed = 250;
		DirectionManager manager = new DirectionManager(motorSpeed, diretion, leftMotor, rightMotor, compassSensor);
		System.out.println("Direction: 90");
		Button.ENTER.waitForPress();
		leftMotor.forward();
		rightMotor.forward();
		manager.start();
		Delay.msDelay(15000);
		leftMotor.stop();
		rightMotor.stop();
		System.out.println("Direction - 45: 45");
		Button.ENTER.waitForPress();
		leftMotor.forward();
		rightMotor.forward();
		manager.turnLeft(45);
		Delay.msDelay(15000);
		leftMotor.stop();
		rightMotor.stop();
		System.out.println("Direction - 90: 315");
		Button.ENTER.waitForPress();
		leftMotor.forward();
		rightMotor.forward();
		manager.turnLeft(90);
		Delay.msDelay(15000);
		leftMotor.stop();
		rightMotor.stop();
		System.out.println("Direction + 135: 90");
		Button.ENTER.waitForPress();
		leftMotor.forward();
		rightMotor.forward();
		manager.turnRight(135);
		Delay.msDelay(15000);
		leftMotor.stop();
		rightMotor.stop();
		System.exit(0);
	}
	
	public static void main(String[] args) {
		new ManagerTest();
	}

}
