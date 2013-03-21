package examples;
// SimEx8.java
// Two light sensors, polling

import ch.aplu.nxtsim.*;
import ch.aplu.jgamegrid.*;
import java.awt.*;

public class SimEx8
{
  public SimEx8()
  {
    NxtRobot robot = new NxtRobot();
    Gear gear = new Gear();
    LightSensor ls1 = new LightSensor(SensorPort.S1);
    LightSensor ls2 = new LightSensor(SensorPort.S2);
    robot.addPart(gear);
    robot.addPart(ls1);
    robot.addPart(ls2);
    gear.forward();

    int v = 500;
    double r = 0.3;
    while (true)
    {
      int v1 = ls1.getValue();
      int v2 = ls2.getValue();
      if (v1 < v && v2 < v)
        gear.forward();
      if (v1 < v && v2 > v)
        gear.rightArc(r);
      if (v1 > v && v2 < v)
        gear.leftArc(r);
      if (v1 > v && v2 > v)
        gear.backward();
    }
  }

  public static void main(String[] args)
  {
    new SimEx8();
  }

  // ------------------ Environment --------------------------
  private static void _init(GameGrid gg)
  {
    GGBackground bg = gg.getBg();
    bg.setPaintColor(Color.black);
    bg.fillArc(new Point(250, 150), 120, 0, 360);
    bg.setPaintColor(Color.white);
    bg.fillArc(new Point(250, 150), 60, 0, 360);
    bg.setPaintColor(Color.black);
    bg.fillArc(new Point(250, 350), 120, 0, 360);
    bg.setPaintColor(Color.white);
    bg.fillArc(new Point(250, 350), 60, 0, 360);
  }

  static
  {
    NxtContext.setStartDirection(10);
  }

}
