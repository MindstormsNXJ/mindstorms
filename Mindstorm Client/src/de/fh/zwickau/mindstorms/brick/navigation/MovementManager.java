package de.fh.zwickau.mindstorms.brick.navigation;

import lejos.util.Delay;
import de.fh.zwickau.mindstorms.brick.Robot;
import de.fh.zwickau.mindstorms.brick.util.Manager;

public class MovementManager implements Manager {

	double driveTranslation;
	Robot robot;
	private int startdegrees;
	private int robotspeed=200;
	private boolean driving;
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
		driving = true;
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
				while(driving==true){
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
						driving=false;
					}}
			if(!forward){
				if(tachoRight+(rotToDriveRight)>=robot.rightMotor.getTachoCount()||
						tachoLeft+(rotToDriveLeft)>=robot.rightMotor.getTachoCount()
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

	@Override
	public void stop() {
		robot.rightMotor.stop(true);//also in direction + doku +arbeitspaketbericht
		robot.leftMotor.stop(true);
		driving = false;
		int x=(int) ((( (robot.rightMotor.getTachoCount()-tachoRight)+ (robot.leftMotor.getTachoCount()-tachoLeft))/2)*((translationLeft+translationRight))/2);

	}

	public boolean isMoving() {
		return driving;
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