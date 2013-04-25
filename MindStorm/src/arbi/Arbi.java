package arbi;

import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.robotics.subsumption.Arbitrator;
import lejos.robotics.subsumption.Behavior;
import lejos.util.Delay;

public class Arbi {

	private Arbitrator arbi;
	private boolean drive;
	private NXTRegulatedMotor leftMotor;
	private NXTRegulatedMotor rightMotor;
	
	public Arbi() {
		leftMotor = Motor.A;
		rightMotor = Motor.B;
		
		drive = true;
		Behavior[] behaviors = new Behavior[2];
		behaviors[1] = new RotateBehavior(this);
		behaviors[0] = new DriveBehavior(this);
		arbi = new Arbitrator(behaviors);
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				arbi.start();
			}
			
		}).start();
		
		Delay.msDelay(5000);
		
		drive = false;
		
		Delay.msDelay(5000);
		
		drive = true;
		
		Delay.msDelay(5000);
		
		System.exit(0);
	}
	
	public boolean isDrive() {
		return drive;
	}
	
	public void setDrive(boolean drive) {
		this.drive = drive;
	}
	
	public NXTRegulatedMotor getLeftMotor() {
		return leftMotor;
	}
	
	public NXTRegulatedMotor getRightMotor() {
		return rightMotor;
	}
	
	public static void main(String[] args) {
		new Arbi();
	}
	
}
