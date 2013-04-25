package de.fh.zwickau.mindstorms.brick.navigation;

import lejos.util.Delay;
import de.fh.zwickau.mindstorms.brick.Robot;
import de.fh.zwickau.mindstorms.brick.util.Manager;

public class MovementManager implements Manager {

	/** value for translating motorcycles to a distance */
 	double driveTranslation;
	/** the robot */
 	Robot robot;
	/** direction to the beginning */
 	private int startdegrees;
	/** default speed for stopping*/
	//TODO Why set here? Overwritten at Line 37
	private int robotspeed = 200;
	/** whether the robot is currently moving */
	private boolean isMoving;
	private int tachoRight;
	private int rotToDriveRight;
	private double rotToDriveLeft;
	private int tachoLeft;
	private boolean forward;
	private double translationLeft;
	private double translationRight;
	private int dist2;
	

	public MovementManager(Robot robot) {
		this.robot = robot;
		this.driveTranslation = robot.driveTranslation;
	}

	public void move(int dist) {
		robot.setModeDrive();
		robotspeed=robot.driveSpeed;
		this.dist2=dist;
		isMoving = true;
		startdegrees = (int) robot.compassSensor.getDegrees();
		translationRight=translationLeft=driveTranslation;
		
		rotToDriveRight=(int) (dist*driveTranslation);
		rotToDriveLeft=(int) (dist*driveTranslation);
		tachoRight=robot.rightMotor.getTachoCount();
		tachoLeft=robot.leftMotor.getTachoCount();
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
		System.out.println(rotToDriveLeft);
		Thread check = new Thread(new Runnable() {

			

			@Override
			public void run() {
				/**
				 * Angelcorrection
				 */
				while(isMoving==true){
//					System.out.println("start"+startdegrees);
//					System.out.println("aktuall"+(int) robot.compassSensor.getDegreesCartesian());
//					if(angelCorrection(startdegrees, (int) robot.compassSensor.getDegreesCartesian())<-10){
//						robot.leftMotor.setSpeed((int) (robotspeed+robotspeed*0.1));
//						translationRight=translationRight+1;
//						rotToDriveLeft=(dist2*(translationRight));
//					}
//					if(angelCorrection(startdegrees, (int) robot.compassSensor.getDegreesCartesian())>10){
//						robot.rightMotor.setSpeed((int) (robotspeed+robotspeed*0.1));
//						translationLeft=translationLeft+1;
//						rotToDriveLeft=(dist2*(translationLeft));
//					}
//					if(startdegrees==robot.compassSensor.getDegrees()){
//						robot.rightMotor.setSpeed(robotspeed);
//						robot.leftMotor.setSpeed(robotspeed);
//					}
			/**
			 * endcausal
			 */
			if(forward){
					if(tachoRight+(rotToDriveRight)<=robot.rightMotor.getTachoCount()||
							tachoLeft+(rotToDriveLeft)<=robot.rightMotor.getTachoCount()
							) {
						isMoving=false;
					}}
			if(!forward){
				if(tachoRight+(rotToDriveRight)>=robot.rightMotor.getTachoCount()||
						tachoLeft+(rotToDriveLeft)>=robot.rightMotor.getTachoCount()
						) {
					isMoving=false;
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

	@Override
	public void stop() {
		robot.rightMotor.stop(true);//also in direction + doku +arbeitspaketbericht
		robot.leftMotor.stop(true);
		isMoving = false;
		int x=(int) ((( (robot.rightMotor.getTachoCount()-tachoRight)+ (robot.leftMotor.getTachoCount()-tachoLeft))/2)*((translationLeft+translationRight))/2);

	}

	public boolean isMoving() {
		return isMoving;
	}

	 int angelCorrection(int a,int n){
			int c =n-a;
			if(c>180){
				c=c-360;
			}
			if(c<-180){
				c=c+360;
			}
			return c;
			
		}
}