package sensor.compass;

import lejos.nxt.Button;
import lejos.nxt.MotorPort;
import lejos.nxt.NXTRegulatedMotor;

/**
 * Result: the calibration under usage of the motors tacho count does not work.
 * In the case below:
 *    plain underground: a bit over 2 rotations
 *    textile: a bit under 2 rotations
 *    carpet: a bit under 1.5 rotations
 *    
 * Another option might be to let the robot turn 2 times with uncalibrated
 * sensors, get the time, adapt the speed and let it turn for 40 seconds in
 * calibration mode afterwards.
 *    
 * @author Tobias Schießl
 */
public class TachoCountCalibrator {

	private NXTRegulatedMotor leftMotor;
	private NXTRegulatedMotor rightMotor;
	
	public TachoCountCalibrator() {
		leftMotor = new NXTRegulatedMotor(MotorPort.A);
		rightMotor = new NXTRegulatedMotor(MotorPort.B);
		
		leftMotor.setSpeed(50);
		rightMotor.setSpeed(50);
		
		leftMotor.resetTachoCount();
		rightMotor.resetTachoCount();
		
		leftMotor.rotate(2000, true);
		rightMotor.rotate(-2000);
		
		System.out.println("left: " + leftMotor.getTachoCount());
		System.out.println("right: " + rightMotor.getTachoCount());
		
		Button.ESCAPE.waitForPress();
	}
	
	public static void main(String[] args) {
		new TachoCountCalibrator();
	}

}
