package de.fh.zwickau.mindstorms.brick.sensors;

import de.fh.zwickau.mindstorms.brick.Robot;

public class ObjectCentralisation {
	
	private Robot robot;

	public ObjectCentralisation(Robot robot) {
		this.robot=robot;
		int targetAngel = calcAngel(scanLeft(),scanRight());
		System.out.println(targetAngel);
		robot.positionManager.rotateTo(targetAngel);
	}
	
	private int calcAngel(int leftAngel, int rightAngel) {
		int centralAngel = 0;
		centralAngel= (leftAngel+rightAngel)/2;
		if (leftAngel > rightAngel) {
			centralAngel+=180;
		}
		return centralAngel;
	}

	private int scanRight() {
		// TODO Auto-generated method stub	
	return (int) robot.compassSensor.getDegrees();
		}

	private int scanLeft() {
		// TODO Auto-generated method stub	
		return (int) robot.compassSensor.getDegrees();
	}
}
