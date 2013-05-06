package de.fh.zwickau.mindstorms.brick.sensors;

import de.fh.zwickau.mindstorms.brick.Robot;
import de.fh.zwickau.mindstorms.brick.navigation.Direction;

public class ObjectCentralisation {
	
	private Robot robot;
	private int distance;
	private boolean isdetected;

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
		distance= robot.ultrasonicSensor.getDistance();
		while (isdetected) {
		robot.positionManager.rotate(3, Direction.LEFT);
		}
		return (int) robot.compassSensor.getDegrees();
		}

	private int scanLeft() {
		// TODO Auto-generated method stub	
		return (int) robot.compassSensor.getDegrees();
	}
}
