package examples;
// SimEx15.java
// Move through channel using ultrasonic sensor

import ch.aplu.nxtsim.*;
import java.awt.Color;
import java.awt.Point;

public class SimEx15
{
  public SimEx15()
  {
    NxtRobot robot = new NxtRobot();
    Gear gear = new Gear();
    robot.addPart(gear);
    UltrasonicSensor us = new UltrasonicSensor(SensorPort.S1);
    robot.addPart(us);
    us.setBeamAreaColor(Color.green);
    us.setProximityCircleColor(Color.lightGray);
    
    double arc = 0.5;
    gear.setSpeed(50);
    gear.rightArc(arc);
    boolean isRightArc = true;
    
    int oldDistance = 0;
    while (true)
    {
      Tools.delay(100);
      int distance = us.getDistance();
      if (distance == -1)
        continue;
      if (distance < oldDistance)
      {
        if (isRightArc)
        {
          gear.leftArc(arc);
          isRightArc = false;
        }
        else
        {
          gear.rightArc(arc);
          isRightArc = true;
        }
      }
      oldDistance = distance;
    }
  }

  public static void main(String[] args)
  {
    new SimEx15();
  }
  
  // ------------------ Environment --------------------------
  static
  {
    Point[] mesh_bar =
    {
      new Point(10, 200), new Point(-10, 200),
      new Point(-10, -200), new Point(10, -200)
    };
    NxtContext.useTarget("sprites/bar1.gif", mesh_bar, 200, 250);
    NxtContext.useTarget("sprites/bar1.gif", mesh_bar, 300, 250);
    
    NxtContext.setStartPosition(250, 460);
  }


}
