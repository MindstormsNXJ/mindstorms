package sensor.color_and_pressure;

import lejos.nxt.Button;
import lejos.nxt.ButtonListener;
import lejos.nxt.LightSensor;
import lejos.nxt.SensorPort;
import lejos.util.Delay;

/**
 * Class for quick-testing the Lego light sensor (no color) ON PORT 1.
 * Prints the 
 * Implemented buttons:
 * 	* escape button -> System.exit
 * @author simon
 *
 */
public class LightSensorTest {
	private LightSensor ls;		// simple light sensor
	
	public LightSensorTest(){
		Button.ESCAPE.addButtonListener(new EscapeButton());
		Button.RIGHT.addButtonListener(new RightButton(this));
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		LightSensorTest lt = new LightSensorTest();
		lt.runSensorTest();
	}
	
	/**
	 * print the light values to LCD
	 */
	public void runSensorTest(){
		ls = new LightSensor(SensorPort.S1);
		ls.setFloodlight(false);
		System.out.println(" light sensor on Port1\n");
		System.out.println("right Button > flootlight on/off");
		Delay.msDelay(4000);
		while(true){
			System.out.println("lightValue: " + ls.getLightValue());
			System.out.println("normalV.: " + ls.getNormalizedLightValue());
			Delay.msDelay(1000);
		}
	}

	/**
	 * turn flood light on and off
	 */
	public void floodLightOnOff(){
		if(ls.isFloodlightOn())
			ls.setFloodlight(false);
		else
			ls.setFloodlight(true);
	}
}

/**
 * Implementation of the Button Listeners
 * @author simon
 */
class EscapeButton implements ButtonListener{

	@Override
	public void buttonPressed(Button b) {
		System.exit(0);
	}

	@Override
	public void buttonReleased(Button b) {
		// do nothing
	}
}

class RightButton implements ButtonListener{
	private LightSensorTest lt;
	public RightButton(LightSensorTest lt){
		this.lt = lt;
	}
	@Override
	public void buttonPressed(Button b) {
		lt.floodLightOnOff();
	}
	
	@Override
	public void buttonReleased(Button b) {
		// do nothing
	}
}