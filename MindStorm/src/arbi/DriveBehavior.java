package arbi;

import lejos.robotics.subsumption.Behavior;

public class DriveBehavior implements Behavior {

	private Arbi arbi;
	private boolean suppress;
	
	public DriveBehavior(Arbi arbi) {
		this.arbi = arbi;
	}
	
	@Override
	public boolean takeControl() {
		if (arbi.isDrive())
			return true;
		else
			return false;
	}

	@Override
	public void action() {
		while (!suppress) {
			arbi.getLeftMotor().forward();
			arbi.getRightMotor().forward();
		}
		suppress = false;
	}

	@Override
	public void suppress() {
		suppress = true;
	}

}
