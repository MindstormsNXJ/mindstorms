package examples;
// SimEx3.java
// TurtleRobot

import ch.aplu.nxtsim.*;

public class SimEx3
{
  public SimEx3()
  {
    TurtleRobot robot = new TurtleRobot();

    for (int i = 0; i < 4; i++)
    {
      robot.forward(100);
      robot.left(90);
    }
    robot.exit();
  }

  public static void main(String[] args)
  {
    new SimEx3();
  }
}