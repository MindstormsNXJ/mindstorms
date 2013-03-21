package examples;
// SimEx16.java
// Search target using the ultrasonic sensor and body-check it

import ch.aplu.nxtsim.*;
import java.awt.*;
import ch.aplu.jgamegrid.*;

public class SimEx16
{
  private static NxtRobot robot;  // Static, because used in _init()
  private Gear gear;
  private UltrasonicSensor us;
  private TouchSensor ts;

  public SimEx16()
  {
    robot = new TurtleRobot();
    gear = new Gear();
    robot.addPart(gear);
    gear.setSpeed(10);
    us = new UltrasonicSensor(SensorPort.S2);
    robot.addPart(us);
    us.setBeamAreaColor(Color.green);  // May be commented out
    us.setProximityCircleColor(Color.lightGray); // May be commented out
    ts = new TouchSensor(SensorPort.S3);
    robot.addPart(ts);
    runRobotProgram();
  }

  private void runRobotProgram()
  {
    while (true)
    {
      if (ts.isPressed())
      {
        gear.setSpeed(70);
        gear.right(3000);
        gear.stop();
        return;
      }
      int distance = us.getDistance();
      if (distance == -1)
        searchTarget();
    }
  }

  private void searchTarget()
  {
    while (true)
    {
      gear.right(50);
      int distance = us.getDistance();
      if (distance != -1)
      {
        gear.right(1500);
        gear.forward();
        return;
      }
    }
  }

  public static void main(String[] args)
  {
    new SimEx16();
  }

  // ------------------ Environment --------------------------
  private static void _init(final GameGrid gg)
  {
    gg.setTitle("Click to place target");
    Point[] mesh =
    {
      new Point(50, 0), new Point(25, 42), new Point(-25, 42),
      new Point(-50, 0), new Point(-25, -42), new Point(25, -42)
    };
    final Target target = new Target("sprites/target_red.gif", mesh);
    final Obstacle obstacle = new Obstacle("sprites/target_red.gif");
    gg.addMouseListener(
      new GGMouseListener()
      {
        public boolean mouseEvent(GGMouse mouse)
        {
          Location loc =
            gg.toLocationInGrid(mouse.getX(), mouse.getY());
          robot.addTarget(target, loc.x, loc.y);
          robot.addObstacle(obstacle, loc.x, loc.y);
          return true;
        }

      }, GGMouse.lPress);
  }

}
