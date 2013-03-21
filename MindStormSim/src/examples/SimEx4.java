package examples;
// SimEx4.java
// One touch sensor, polling

import ch.aplu.nxtsim.*;

public class SimEx4
{
  public SimEx4()
  {
    NxtRobot robot = new NxtRobot();
    Gear gear = new Gear();
    TouchSensor ts = new TouchSensor(SensorPort.S3);
    robot.addPart(gear);
    robot.addPart(ts);
    gear.setSpeed(30);
    gear.forward();
    while (true)
    {
      if (ts.isPressed())
      {
        gear.backward(1200);
        gear.left(750);
        gear.forward();
      }
    }
  }

  public static void main(String[] args)
  {
    new SimEx4();
  }

  // ------------------ Environment --------------------------
  static
  {
    NxtContext.showNavigationBar();
    NxtContext.useObstacle("sprites/bar0.gif", 250, 200);
    NxtContext.useObstacle("sprites/bar1.gif", 400, 250);
    NxtContext.useObstacle("sprites/bar2.gif", 250, 400);
    NxtContext.useObstacle("sprites/bar3.gif", 100, 250);
  }

}
