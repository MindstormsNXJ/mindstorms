package test;

import lejos.nxt.*;
public class UltrasonicTest {

	UltrasonicSensor us = new UltrasonicSensor(SensorPort.S4);
	
	public UltrasonicTest() {
		Button.ENTER.waitForPressAndRelease();
		while(true) {
			System.out.println(us.getDistance());
			try {
				Thread.sleep(800);
			}
			catch(InterruptedException ie) {
				ie.printStackTrace();
			}
		}
	}
	
	public static void main(String args[]) {
		new UltrasonicTest();
	}
}
