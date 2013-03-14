package test;

import lejos.nxt.*;

public class SingleMotorTest {
	public SingleMotorTest() {
		NXTRegulatedMotor motA = Motor.A;
//		Button.ENTER.waitForPressAndRelease();
		System.out.println("Start");
		motA.forward();
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		motA.stop();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		motA.backward();
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		motA.setSpeed(5000);
		motA.forward();
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		motA.stop();
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		motA.setSpeed(1000);
		motA.rotate(180);
		motA.rotate(-180);
		System.out.println("Ende");
	}

	public static void main(String[] args) {
		new SingleMotorTest();
	}
}