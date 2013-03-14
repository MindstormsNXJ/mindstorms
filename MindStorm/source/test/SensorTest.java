package test;

import lejos.nxt.*;
import lejos.nxt.addon.ColorHTSensor;
import lejos.nxt.addon.CompassHTSensor;

public class SensorTest {
	public SensorTest() {
		UltrasonicSensor us = new UltrasonicSensor(SensorPort.S1);
		CompassHTSensor cs = new CompassHTSensor(SensorPort.S2);
		ColorHTSensor cos= new ColorHTSensor(SensorPort.S3);
		System.out.println("Start SensorTest");
		Button.ENTER.waitForPressAndRelease();
		while (true) {
			System.out.println(us.getDistance());
			System.out.println(cs.getDegrees());
			System.out.println(cos.getColorID());
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		new SensorTest();
	}
}