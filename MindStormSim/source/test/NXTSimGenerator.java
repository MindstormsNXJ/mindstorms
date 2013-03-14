package test;

import ch.aplu.nxtsim.*;

public class NXTSimGenerator {
	public NXTSimGenerator() {
		// Robot Generation
		NxtRobot robot = new NxtRobot();
		Motor motA = new Motor(MotorPort.A);
		NxtRobot robot2 = new NxtRobot();
		robot.addPart(motA);
		// Class to test
//		new SingleMotorTest();
	}

	public static void main(String[] args) {
		new NXTSimGenerator();
	}
}