package examples;
// SimEx7.java
// One light sensor, event driven

import ch.aplu.nxtsim.*;
import ch.aplu.jgamegrid.*;
import java.awt.*;

public class SimEx7 implements LightListener
{
  private Gear gear = new Gear();

  public SimEx7()
  {
    NxtRobot robot = new NxtRobot();
    LightSensor ls = new LightSensor(SensorPort.S3);
    robot.addPart(gear);
    robot.addPart(ls);
    ls.addLightListener(this, 500);
    gear.forward();
  }

  public void bright(SensorPort port, int level)
  {
    gear.rightArc(0.15);
  }

  public void dark(SensorPort port, int level)
  {
    gear.leftArc(0.15);
  }

  public static void main(String[] args)
  {
    new SimEx7();
  }

  // ------------------ Environment --------------------------
  public static void _init(GameGrid gg)
  {
    GGBackground bg = gg.getBg();
    bg.setPaintColor(Color.black);
    bg.fillArc(new Point(250, 250), 50, 0, 360);
    bg.fillArc(new Point(250, 350), 100, 0, 360);
  }
}
