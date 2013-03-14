package examples;
// SimEx14.java
// Poll ultrasonic sensor

import ch.aplu.nxtsim.*;
import java.awt.Color;
import java.awt.Point;

public class SimEx14
{
  public SimEx14()
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
    new SimEx14();
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
  }

}
