package test;

import lejos.nxt.*;

public class Testing {

	NXTRegulatedMotor left = Motor.B;
	NXTRegulatedMotor right = Motor.C;
	UltrasonicSensor us = new UltrasonicSensor(SensorPort.S1);
	int speed = 300;
	int rotationspeed = 100;
	boolean run = true;
	int turncounter = 0;
	
	public Testing() {
		Button.ENTER.waitForPressAndRelease();
		int distance = 0;
		while(run) {
			distance = getDistance();
			forward();
			if(distance <= 40) {
				System.out.println(distance);
				stop();
				sleep(200);
				turnRight();
				turncounter++;
				sleep(3000);
				stop();
				forward();
			}
			if(turncounter == 5) {
				run = false;
				stop();
			}
		}
	}
	
	public static void main(String args[]) {
		new Testing();
	}
	
	public void setSpeed(int speed) {
		left.setSpeed(speed);
		right.setSpeed(speed);
	}
	
	public void forward() {
		setSpeed(speed);
		left.forward();
		right.forward();
	}
	
	public void backward(){
		setSpeed(speed / 2);
		left.backward();
		right.backward();
	}
	
	public void turnLeft() {
		setSpeed(rotationspeed);
		left.backward();
		right.forward();
	}
	
	public void turnRight() {
		setSpeed(rotationspeed);
		left.forward();
		right.backward();
	}
	
	public void sleep(int millis) {
		try {
			Thread.sleep(millis);
		}
		catch(InterruptedException ie) {
			ie.printStackTrace();
		}
	}
	
	public int getDistance() {
		return us.getDistance();
	}
	
	public void stop() {
		left.stop();
		right.stop();
	}
}
