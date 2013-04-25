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
	private double rotToDriveRight;
	private double rotToDriveLeft;
	private int tachoLeft;
	private boolean forward;
	private double translationLeft;
	private double translationRight;
	private int dist2;
	private PositionManager pm;
	

	public MovementManager(Robot robot, PositionManager pm) {
		this.pm=pm;
		this.robot = robot;
		this.driveTranslation = robot.driveTranslation;
	}

	public void move(int dist) {
		startdegrees = (int) robot.compassSensor.getDegrees();
		robot.setModeDrive();
		robotspeed=robot.driveSpeed;
		this.dist2=dist;
		driving = true;
		translationRight=translationLeft=driveTranslation;
		
		tachoRight=robot.rightMotor.getTachoCount();
		tachoLeft=robot.leftMotor.getTachoCount();
		rotToDriveRight=(int) (dist*driveTranslation)+tachoRight;
		rotToDriveLeft=(int) (dist*driveTranslation)+tachoLeft;
		
		driving(dist);
		Thread check = new Thread(new Runnable() {

			

			@Override
			public void run() {
				/**
				 * Angelcorrection
				 */
				while(driving==true){
					if(Math.abs(angelCorrection(startdegrees, (int) robot.compassSensor.getDegrees()))>5){
						double newrtdl = rotToDriveLeft-robot.leftMotor.getTachoCount();
						double newrtdr = rotToDriveRight-robot.rightMotor.getTachoCount();
						System.out.println("start"+startdegrees);
						System.out.println("aktuall"+(int) robot.compassSensor.getDegrees());
						robot.rightMotor.stop(true);
						robot.leftMotor.stop(true);
						pm.rotateTo(startdegrees);
						rotToDriveLeft=robot.leftMotor.getTachoCount()+newrtdl;
						rotToDriveRight=robot.rightMotor.getTachoCount()+newrtdr;
						driving(dist2);
						System.out.println("start"+startdegrees);
						System.out.println("aktuall"+(int) robot.compassSensor.getDegrees());
						
					}
					
					
			/**
			 * endcausal
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

	@Override
	public void stop() {
		robot.rightMotor.stop(true);//also in direction + doku +arbeitspaketbericht
		robot.leftMotor.stop(true);
		driving = false;
		

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