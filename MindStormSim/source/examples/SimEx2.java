package examples;
// SimEx2.java
// Gear

import ch.aplu.nxtsim.*;

public class SimEx2
{
  public SimEx2()
  {
    NxtRobot robot = new NxtRobot();
    Gear gear = new Gear();
    robot.addPart(gear);

    gear.forward();
    Tools.delay(2000);
    gear.left(2000);
    gear.forward(2000);
    gear.leftArc(0.2, 2000);
    gear.forward(2000);
    gear.leftArc(-0.2, 2000);
    robot.exit();
  }

  public static void main(String[] args)
  {
    new SimEx2();
  }
}