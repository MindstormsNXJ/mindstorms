package sensor.colorAndPressure;

import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.TouchSensor;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.addon.ColorHTSensor;
import lejos.nxt.addon.CompassHTSensor;

public class colorAndPressure {
	// Motor declaration
	NXTRegulatedMotor left = Motor.B; // right
	NXTRegulatedMotor right = Motor.C; // left
	NXTRegulatedMotor grappler = Motor.A; // Grappler arm
	// Sensor declaration
	UltrasonicSensor son = new UltrasonicSensor(SensorPort.S1);
	CompassHTSensor com = new CompassHTSensor(SensorPort.S2);
	TouchSensor touch = new TouchSensor(SensorPort.S3);
	ColorHTSensor color = new ColorHTSensor(SensorPort.S4);
	public colorAndPressure() {

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
