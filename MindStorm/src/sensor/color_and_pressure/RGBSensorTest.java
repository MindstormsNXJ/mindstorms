package sensor.color_and_pressure;

import lejos.nxt.ColorSensor;
import lejos.nxt.SensorPort;
import lejos.util.Delay;

/**
 * Simple test class for the original Lego RGB color sensor.
 * This class also tests the LEDs of the sensor
 * @author simon
 *
 */
public class RGBSensorTest {
	private ColorSensor cs;
	
	public RGBSensorTest(){
		cs = new ColorSensor(SensorPort.S1);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		RGBSensorTest rgbTest = new RGBSensorTest();
		System.out.println("test RGB sensor on Port1");
		Delay.msDelay(2000);
		rgbTest.testLights();
		rgbTest.testSensor();
		
	}
	
	/**
	 * Method to test the different lights of the sensor
	 */
	public void testLights(){
		int i = 0;
		while(i < 2){
			cs.setFloodlight(0);	// red flood light
			Delay.msDelay(1000);
			cs.setFloodlight(1);	// green flood light
			Delay.msDelay(1000);
			cs.setFloodlight(2);	// blue flood light
			Delay.msDelay(1000);
			i++;
		}
		cs.setFloodlight(false);
	}
	
	/**
	 * Method to test the sensor, prints the measured color.
	 */
	public void testSensor(){
		while(true){
			System.out.println(getColorName(cs.getColorID()));
			Delay.msDelay(1000);
		}
	}
	
	/**
	 * Turns the int from the Sensor into a String with the color
	 * The following color table is from the HI TECH sensor, the 
	 * original sensor recognizes only six colors.
	 * 
	 * 0 - red			4 - magenta			8 - pink
	 * 1 - green		5 - orange			9 - gray
	 * 2 - blue			6 - white			10 - light gray
	 * 3 - yellow		7 - black			11 - dark gray		12 - cyan
	 * 
	 * @param cID the int measured by sensor
	 * @return String with the colors name
	 */
	public String getColorName(int cID){
		if(cID == 0) return "red";
		if(cID == 1) return "green";
		if(cID == 2) return "blue";
		if(cID == 3) return "yellow";
		if(cID == 4) return "magenta";
		if(cID == 5) return "orange";
		if(cID == 6) return "white";
		if(cID == 7) return "black";
		if(cID == 8) return "pink";
		if(cID == 9) return "gray";
		if(cID == 10) return "light gray";
		if(cID == 11) return "dark gray";
		if(cID == 12) return "cyan";
		else return "unknown color";
	}
}
