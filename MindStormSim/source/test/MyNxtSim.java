package test;

import ch.aplu.nxtsim.*;

class MyNxtSim
{
  MyNxtSim()
  {	
    TurtleRobot robot = new TurtleRobot();
    robot.forward(100);
    robot.exit();    
  }

  public static void main(String[] args)
  {
    new MyNxtSim();    
  }
}