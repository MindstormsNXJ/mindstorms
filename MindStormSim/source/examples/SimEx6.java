package examples;
// SimEx6.java
// Two touch sensors, complex track, QuitPane

import ch.aplu.nxtsim.*;
import ch.aplu.util.*;

public class SimEx6
{
  public SimEx6()
  {
    NxtRobot robot = new NxtRobot();
    Gear gear = new Gear();
    robot.addPart(gear);
    TouchSensor ts1 = new TouchSensor(SensorPort.S1); // right sensor
    TouchSensor ts2 = new TouchSensor(SensorPort.S2); // left sensor
    robot.addPart(ts1);
    robot.addPart(ts2);
    gear.forward();
    while (!QuitPane.quit())
    {
      boolean t1 = ts1.isPressed();
      boolean t2 = ts2.isPressed();

      if (t1 && t2)
      {
        gear.backward(500);
        gear.left(400);
        gear.forward();
      }
      else
      {
        if (t1)
        {
          gear.backward(500);
          gear.left(400);
          gear.forward();
        }
        else
        {
          if (t2)
          {
            gear.backward(500);
            gear.right(100);
            gear.forward();
          }
        }
      }
      Tools.delay(20);
    }
    robot.exit();
  }

  static public void main(String[] args)
  {
    new SimEx6();
  }

  // ------------------ Environment --------------------------
  static
  {
    NxtContext.setLocation(10, 10);
    NxtContext.setStartDirection(5);
    NxtContext.setStartPosition(100, 240);
    NxtContext.useObstacle(NxtContext.channel);
  }
}
