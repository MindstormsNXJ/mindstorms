package examples;

// SimEx10.java
// One light sensor, one touchsensor, road follower

import ch.aplu.nxtsim.*;

public class SimEx10
{
  public SimEx10()
  {
    NxtRobot robot = new NxtRobot();
    Gear gear = new Gear();
    LightSensor ls = new LightSensor(SensorPort.S3);
    TouchSensor ts = new TouchSensor(SensorPort.S1);
    robot.addPart(gear);
    robot.addPart(ls);
    robot.addPart(ts);
    ls.activate(true);

    while (true)
    {
      int v = ls.getValue();
      if (v < 100)  // black
        gear.forward();
      if (v > 600 && v < 700)  // green
        gear.leftArc(0.1);
      if (v > 800)  // yellow
        gear.rightArc(0.1);
      if (ts.isPressed())
        break;
    }
  }

  public static void main(String[] args)
  {
    new SimEx10();
  }

  // ------------------ Environment --------------------------
  static
  {
    NxtContext.setStartPosition(40, 460);
    NxtContext.setStartDirection(-90);
    NxtContext.useBackground("sprites/road.gif");
//    NxtContext.useObstacle("sprites/chocolate.gif", 400, 50);
  }
}
