package examples;
// SimEx11.java
// One sound sensor, one touch sensor, event driven

import ch.aplu.nxtsim.*;

public class SimEx11 implements SoundListener, TouchListener
{
  private Gear gear = new Gear();

  public SimEx11()
  {
    NxtRobot robot = new NxtRobot();
    SoundSensor ss = new SoundSensor(SensorPort.S1);
    TouchSensor ts = new TouchSensor(SensorPort.S3);
    robot.addPart(gear);
    robot.addPart(ss);
    robot.addPart(ts);
    ss.addSoundListener(this, 50);
    ts.addTouchListener(this);
  }

  public void loud(SensorPort port, int level)
  {
    if (gear.isMoving())
      gear.stop();
    else
      gear.forward();
  }

  public void quiet(SensorPort port, int level)
  {
  }

  public void pressed(SensorPort port)
  {
    gear.backward();
  }

  public void released(SensorPort port)
  {
  }

  public static void main(String[] args)
  {
    new SimEx11();
  }

  // ------------------ Environment --------------------------
  static
  {
    NxtContext.useObstacle("sprites/bar0.gif", 250, 50);
  }
}
