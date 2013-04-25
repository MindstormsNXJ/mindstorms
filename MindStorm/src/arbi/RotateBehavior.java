package arbi;

import lejos.robotics.subsumption.Behavior;

public class RotateBehavior implements Behavior {

	private Arbi arbi;
	private boolean suppress;
	
	public RotateBehavior(Arbi arbi) {
		this.arbi = arbi;
	}
	
	@Override
	public boolean takeControl() {
		if (arbi.isDrive())
			return false;
		else
			return true;
	}

	@Override
	public void action() {
		while (!suppress) {
			arbi.getLeftMotor().forward();
			arbi.getRightMotor().backward();
		}
		suppress = false;
	}

	@Override
	public void suppress() {
		suppress = true;
	}

}
