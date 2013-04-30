package de.fh.zwickau.mindstorms.brick.navigation;

import de.fh.zwickau.mindstorms.brick.Robot;
import de.fh.zwickau.mindstorms.brick.util.Manager;

/**
 * The MovementManager implements the movement of the robot by cm
 * back an forward
 * 
 * @author Martin Petzold
 *
 */


public class MovementManager implements Manager {

	/** The robot who be moved */
	Robot robot;
	/**The Factor who take Degrees into centimeter*/
	double driveTranslation;
	/**the Angle where the Robot  stands at Start*/
	private int startdegrees;
	/**the Boolean who shows that the robot is driving*/
	private boolean driving;
	/**the Tachocounts from the Motors on the Start of the Moving Process*/
	private int tachoLeft;
	private int tachoRight;
	/**the angle which is the robot driving in real*/
	private double rotToDriveRight;
	private double rotToDriveLeft;
	/**the boolean who shows if the Robot drives for- or backward*/
	private boolean forward;
	/**the distance which the Robot had to drive*/
	private int distance;
	/**the intern Position Manager for correcting the Driving angle*/
	private PositionManager positionManager;
	

	/**
	 * The Konstruktor of the MovementManager
	 * @param robot the robot which has to move
	 * @param pm the Position Manager for position corrections
	 */
	public MovementManager(Robot robot, PositionManager pm) {
		this.positionManager=pm;
		this.robot = robot;
		this.driveTranslation = robot.driveTranslation;
	}

	/**
	 * The Method who drives the Robot 
	 * @param dist distance in cm which is to drive positive = drive forward, negative = drive backward
	 */
	public void move(int dist) {
		startdegrees = (int) robot.compassSensor.getDegrees();
		robot.setModeDrive();
		this.distance=dist;
		driving = true;		
		tachoRight=robot.rightMotor.getTachoCount();
		tachoLeft=robot.leftMotor.getTachoCount();
		rotToDriveRight=(int) (dist*driveTranslation)+tachoRight;
		rotToDriveLeft=(int) (dist*driveTranslation)+tachoLeft;
		
		driving(dist);
		/**
		 * the intern Thread for stopping the Moving and correcting the Angle
		 */
		Thread check = new Thread(new Runnable() {

			

			@Override
			public void run() {
				/**
				 * Corects the drive Angle
				 */
				while(driving==true){
					if(Math.abs(angelCorrection(startdegrees, (int) robot.compassSensor.getDegrees()))>5){
						robot.rightMotor.stop(true);
						robot.leftMotor.stop(true);
						double newrtdl = rotToDriveLeft-robot.leftMotor.getTachoCount();
						double newrtdr = rotToDriveRight-robot.rightMotor.getTachoCount();
						/**the doubles Saving the distance who is drived before correcting the angle in rotatedegrees*/
						positionManager.rotateTo(startdegrees);
						/**reinitiale the restdistance in rotateangles*/
						rotToDriveLeft=robot.leftMotor.getTachoCount()+newrtdl;
						rotToDriveRight=robot.rightMotor.getTachoCount()+newrtdr;
						driving(distance);						
					}
					
					
			/**
			 * stops the moving when the right Tachocount  is reached
			 */
			if(forward){
					if(rotToDriveRight<=robot.rightMotor.getTachoCount()||
							rotToDriveRight<=robot.rightMotor.getTachoCount()
							) {
						driving=false;
					}}
			if(!forward){
				if(rotToDriveRight>=robot.rightMotor.getTachoCount()||
						rotToDriveLeft>=robot.leftMotor.getTachoCount()
						) {
					driving=false;
				}}
				}
				
				stop();
			}

		});
		check.start();
		try {
			check.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		
		
	}

	/**
	 * the Method for stopping the movement
	 */
	@Override
	public void stop() {
		robot.rightMotor.stop(true);//also in direction + doku +arbeitspaketbericht
		robot.leftMotor.stop(true);
		driving = false;
		

	}

	public boolean isMoving() {
		return driving;
	}

	/**
	 * the correction of the angle for finding the shortest way to rotate to a specific angle
	 * @param currentAngle angle where you are
	 * @param newAngle angle who wantet
	 * @return degrees where to move
	 */
	 int angelCorrection(int currentAngle,int newAngle){
			int c =newAngle-currentAngle;
			if(c>180){
				c=c-360;
			}
			if(c<-180){
				c=c+360;
			}
			return c;
			
		}
	 /**
	  * the method who starts driving and decides if for ore backward
	  * @param dist distance who to drive, positive forward negativ backward
	  */
	 void driving(int dist){
		 robot.setModeDrive();
		 if(dist>=0){
				robot.leftMotor.forward();
				robot.rightMotor.forward();
				forward=true;
			}
			if(dist<0){
				robot.leftMotor.backward();
				robot.rightMotor.backward();
				forward=false;
			}
	 }
}