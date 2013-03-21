package examples;

import ch.aplu.nxtsim.*;

public class SimEx1
{
  public SimEx1()
  {
    NxtRobot robot = new NxtRobot();
    Motor motA = new Motor(MotorPort.A);
    Motor motB = new Motor(MotorPort.B);
    robot.addPart(motA);
    robot.addPart(motB);
  //lejos ähnlicher code---------------------
    motA.forward();
    motB.forward();
    try {
		Thread.sleep(2000);
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

    motA.stop();
    try {
		Thread.sleep(2000);
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

    motB.stop();
    try {
		Thread.sleep(2000);
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

    motA.backward();
    motB.forward();
    try {
		Thread.sleep(2000);
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

    motB.backward();
    try {
		Thread.sleep(2000);
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

    robot.exit();
  }

  public static void main(String[] args)
  {
	  NxtContext.showNavigationBar();
    new SimEx1();
  }
}