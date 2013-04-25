package sensor.color_and_pressure;
import lejos.nxt.SensorPort;
import lejos.nxt.addon.ColorHTSensor;
import lejos.util.Delay;

/**
 * A simple test class for the Hi Technic Color Sensor.
 * @author simon
 *
 */
public class HTColorTest {
	private ColorHTSensor htSensor;

	public HTColorTest(){
		htSensor = new ColorHTSensor(SensorPort.S1);
		Delay.msDelay(1000);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		HTColorTest htTest = new HTColorTest();
		htTest.testHTSensor();
	}

	/**
	 * Print the color-id and color on lcd.
	 */
	public void testHTSensor(){
		while(true){
			System.out.println("index#: " + htSensor.getColorIndexNumber());
			System.out.println("cID: " + htSensor.getColorID());
			System.out.println(getColorName(htSensor.getColorID()));
			Delay.msDelay(1000);
		}
	}
	
	/**
	 * Turns the int from the Sensor into a String with the color
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
