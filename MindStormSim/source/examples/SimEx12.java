package examples;
// SimEx12.java
// One sound sensor, polling, QuitPane to terminate

import ch.aplu.nxtsim.*;
import ch.aplu.util.*;

public class SimEx12
{
  private Gear gear = new Gear();

  public SimEx12()
  {
    NxtRobot robot = new NxtRobot();
    SoundSensor ss = new SoundSensor(SensorPort.S3);
    robot.addPart(gear);
    robot.addPart(ss);
    while (!QuitPane.quit())
    {
      int v = ss.getValue();
      System.out.println(v);
      if (v < 30)
        gear.forward();
      if (v >= 30 && v < 140)
        gear.leftArc(0.1);
      if (v >= 140)
        gear.rightArc(0.1);
      Tools.delay(200);
    }
    robot.exit();
  }

  public static void main(String[] args)
  {
    new SimEx12();
  }
}
