package de.fh.zwickau.mindstorms.brick.navigation;

import de.fh.zwickau.mindstorms.brick.Robot;
import de.fh.zwickau.mindstorms.brick.util.Manager;

/**
 * The MovementManager implements the movement of the robot by cm back an
 * forward
 * 
 * @author Martin Petzold
 * 
 */

public class MovementManager implements Manager {

	/** The robot who be moved */
	Robot robot;
	/** The Factor who take Degrees into centimeter */
	double driveTranslation;
	/** the Angle where the Robot stands at Start */
	private int startdegrees;
	/** the Boolean who shows that the robot is driving */
	private boolean driving = false;
	/** the Tachocounts from the Motors on the Start of the Moving Process */
	private int tachoLeft;
	private int tachoRight;
	/** the angle which is the robot driving in real */
	private double rotToDriveRight;
	private double rotToDriveLeft;
	/** the boolean who shows if the Robot drives for- or backward */
	private boolean forward;
	/** the distance which the Robot had to drive */
	private int distance;
	/** the intern Position Manager for correcting the Driving angle */
	private PositionManager positionManager;

	/**
	 * The Constructor of the MovementManager
	 * 
	 * @param robot
	 *            the robot which has to move
	 * @param pm
	 *            the Position Manager for position corrections
	 */
	public MovementManager(Robot robot, PositionManager pm) {
		this.positionManager = pm;
		this.robot = robot;
		this.driveTranslation = robot.driveTranslation;
	}

	/**
	 * The Method who drives the Robot
	 * 
	 * @param dist
	 *            distance in cm which is to drive positive = drive forward,
	 *            negative = drive backward
	 */
	public void move(int dist) {
		if (dist == 0) {
			return;
		}
		startdegrees = (int) robot.compassSensor.getDegrees();
		this.distance = dist;
		driving = true;
		tachoRight = robot.rightMotor.getTachoCount();
		tachoLeft = robot.leftMotor.getTachoCount();
		rotToDriveRight = (int) (dist * driveTranslation) + tachoRight;
		rotToDriveLeft = (int) (dist * driveTranslation) + tachoLeft;
		if (dist > 0) {
			forward = true;
		} else {
			forward = false;
		}
		drive();
		/**
		 * Corrects the drive Angle
		 */
		while (driving == true) {
			if (Math.abs(angelCorrection(startdegrees,
					(int) robot.compassSensor.getDegrees())) > 5) {
				robot.stop();
				double newrtdl = rotToDriveLeft
						- robot.leftMotor.getTachoCount();
				double newrtdr = rotToDriveRight
						- robot.rightMotor.getTachoCount();
				/**
				 * the doubles saving the distance which was driven before
				 * correcting the angle with rotating
				 */
				positionManager.rotateTo(startdegrees);
				/** reinitialize the distance for motor */
				rotToDriveLeft = robot.leftMotor.getTachoCount() + newrtdl;
				rotToDriveRight = robot.rightMotor.getTachoCount() + newrtdr;
				drive();
			}

			/**
			 * stops the moving when the right Tachocount is reached
			 */
			if (forward
					&& (rotToDriveRight <= robot.rightMotor.getTachoCount() || rotToDriveRight <= robot.rightMotor
							.getTachoCount())) {
				driving = false;
			}
			if (!forward
					&& (rotToDriveRight >= robot.rightMotor.getTachoCount() || rotToDriveLeft >= robot.leftMotor
							.getTachoCount())) {
				driving = false;
			}
		}
		stop();
	}

	/**
	 * the Method for stopping the movement
	 */
	@Override
	public int stop() {
		robot.stop();
		driving = false;
		return 0;
	}

	public boolean isMoving() {
		return driving;
	}

	/**
	 * the correction of the angle for finding the shortest way to rotate to a
	 * specific angle
	 * 
	 * @param currentAngle
	 *            angle where you are
	 * @param newAngle
	 *            angle who wanted
	 * @return degrees where to move
	 */
	int angelCorrection(int currentAngle, int newAngle) {
		int c = newAngle - currentAngle;
		if (c > 180) {
			c = c - 360;
		}
		if (c < -180) {
			c = c + 360;
		}
		return c;

	}

	/**
	 * the method who starts driving and decides if for- or backward
	 */
	void drive() {
		robot.setModeDrive();
		if (forward) {
			robot.leftMotor.forward();
			robot.rightMotor.forward();
		} else {
			robot.leftMotor.backward();
			robot.rightMotor.backward();
		}
	}
}