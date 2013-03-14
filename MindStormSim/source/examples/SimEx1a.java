package examples;
// SimEx1.java
// Motors

import ch.aplu.nxtsim.*;
import ch.aplu.nxtsim.Motor;
import ch.aplu.nxtsim.MotorPort;


public class SimEx1a
{
  public SimEx1a()
  {
    NxtRobot robot = new NxtRobot();
    Motor motA = new Motor(MotorPort.A);
    Motor motB = new Motor(MotorPort.B);
    robot.addPart(motA);
    robot.addPart(motB);

    System.out.println("motA forward, motB forward");
    motA.forward();
    motB.forward();
    Tools.delay(2000);

    System.out.println("motA stop");
    motA.stop();
    Tools.delay(2000);

    System.out.println("motB stop");
    motB.stop();
    Tools.delay(2000);

    System.out.println("motA backward, motA forward");
    motA.backward();
    motB.forward();
    Tools.delay(2000);

    System.out.println("motA backward");
    motB.backward();
    Tools.delay(2000);

    System.out.println("exiting");
    robot.exit();
  }

  public static void main(String[] args)
  {
    new SimEx1a();
  }
  
  // ------------------ Environment --------------------------
  static
  {
    NxtContext.showStatusBar(30);
  }
}