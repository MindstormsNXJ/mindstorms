package sensor.color_and_pressure;

import lejos.nxt.Button;
import lejos.nxt.ButtonListener;
import lejos.nxt.ColorSensor;
import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.MotorPort;
import lejos.nxt.NXT;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.SystemSettings;
import lejos.nxt.TouchSensor;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.addon.ColorHTSensor;
import lejos.nxt.addon.CompassHTSensor;
import lejos.robotics.Color;
import lejos.util.Delay;

public class colorAndPressure {
	// Motor declaration
	TouchSensor touch = new TouchSensor(SensorPort.S3);
	ColorHTSensor colorHt = new ColorHTSensor(SensorPort.S4);
	ColorSensor cNormal = new ColorSensor(SensorPort.S2);
	

	public colorAndPressure() {
		colorHt.initWhiteBalance();
		LCD.drawInt(lejos.nxt.ColorSensor.Color.RED, 0, 0);
		Delay.msDelay(1000);
		Button.LEFT.addButtonListener(new ButtonListener() {

			@Override
			public void buttonReleased(Button b) {
				NXT.shutDown();

			}

			@Override
			public void buttonPressed(Button b) {
				NXT.shutDown();

			}
		});
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		colorAndPressure cAP = new colorAndPressure();
		cAP.run();
	}

	public void run() {
		while (true) {
			Delay.msDelay(100);
			LCD.drawInt(colorHt.getColorID(), 0, 0);
			if(lejos.nxt.ColorSensor.Color.RED==colorHt.getColorID()){
				LCD.drawString("Rot", 0, 0);
				
			}
		}
	}
}
