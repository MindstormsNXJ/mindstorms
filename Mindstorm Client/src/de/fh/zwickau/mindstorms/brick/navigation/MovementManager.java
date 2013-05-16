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

	/** the robot who be moved */
	Robot robot;
	/** the boolean who shows that the robot is driving */
	private boolean driving;
	/** the intern Position Manager for correcting the Driving angle */
	private PositionManager positionManager;
	private double rotToDriveLeft;
	private double rotToDriveRight;
	private int dist;

	/**
	 * The Constructor of the MovementManager
	 * 
	 * @param robot
	 *            the robot which has to move
	 * @param pm
	 *            the Position Manager for position corrections
	 */
	public MovementManager(Robot robot, PositionManager pm) {
		this.robot = robot;
		this.positionManager = pm;
		driving = false;
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
		this.dist=dist;
		// the angle where the robot stands at start
		int startdegrees = robot.getDirection();
		driving = true;
		// the tachocounts from the motors on the start of the moving process
		int tachoRight = robot.rightMotor.getTachoCount();
		int tachoLeft = robot.leftMotor.getTachoCount();
		// the angle which is the robot driving in real
		rotToDriveRight = dist * robot.driveTranslation + tachoRight;
		rotToDriveLeft = dist * robot.driveTranslation + tachoLeft;

		// the boolean who shows if the robot drives for- or backward
		boolean forward = false;
		if (dist > 0) {
			forward = true;
		}
		drive(forward);

		/**
		 * Corrects the drive Angle
		 */
		while (driving == true) {
			if (Math.abs(angelCorrection(startdegrees, robot.getDirection())) > 5) {
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
				drive(forward);
			}

			/**
			 * stops the moving when the right Tachocount is reached
			 */
			if (forward
					&& (rotToDriveRight <= robot.rightMotor.getTachoCount() || rotToDriveRight <= robot.rightMotor
							.getTachoCount())
					|| !forward
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
		driving = false;
		double wayNotDrived = 
				((rotToDriveLeft- robot.leftMotor.getTachoCount())+
				(rotToDriveRight- robot.rightMotor.getTachoCount()))/
				(2*robot.driveTranslation);
		robot.rightMotor.stop(true);
		robot.leftMotor.stop(false);
		return (int) (dist-wayNotDrived);
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
	void drive(boolean forward) {
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