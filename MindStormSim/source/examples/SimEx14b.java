package examples;
// SimEx14b.java
// Show status bar

import ch.aplu.nxtsim.*;
import java.awt.*;

public class SimEx14b
{
  public SimEx14b()
  {
    NxtRobot robot = new NxtRobot();
    Gear gear = new Gear();
    robot.addPart(gear);
    UltrasonicSensor us = new UltrasonicSensor(SensorPort.S1);
    robot.addPart(us);
    us.setBeamAreaColor(Color.green);
    us.setProximityCircleColor(Color.lightGray);
  
    gear.setSpeed(15);
    gear.forward();
    
    while (true)
    {
      int distance = us.getDistance();
      System.out.println("distance = " + distance);
      if (distance > 0 && distance < 50)
      {
        gear.backward(2000);
        gear.left(1000);
        gear.forward();
      }
    }
  }

  public static void main(String[] args)
  {
    new SimEx14b();
  }

   // ------------------ Environment --------------------------
  static
  {
    Point[] mesh_hbar =
    {
      new Point(200, 10), new Point(-200, 10),
      new Point(-200, -10), new Point(200, -10)
    };
    Point[] mesh_vbar =
    {
      new Point(10, 200), new Point(-10, 200),
      new Point(-10, -200), new Point(10, -200)
    };
    NxtContext.useTarget("sprites/bar0.gif", mesh_hbar, 250, 100);
    NxtContext.useTarget("sprites/bar0.gif", mesh_hbar, 250, 400);
    NxtContext.useTarget("sprites/bar1.gif", mesh_vbar, 100, 250);
    NxtContext.useTarget("sprites/bar1.gif", mesh_vbar, 400, 250);
    
    NxtContext.showStatusBar(30);
  }
}
