package examples;
// SimEx9.java
// One light sensor, polling, interactive trail

import java.awt.Color;

import ch.aplu.jgamegrid.*;
import ch.aplu.nxtsim.*;
import ch.aplu.util.*;

public class SimEx9
{
  private static int xOld, yOld;

  public SimEx9()
  {
    NxtRobot robot = new NxtRobot();
    Gear gear = new Gear();
    LightSensor ls = new LightSensor(SensorPort.S3);
    robot.addPart(gear);
    robot.addPart(ls);
    Monitor.putSleep();  // Wait until track is drawn
    while (true)
    {
      if (ls.getValue() > 800)  //  Bright
        gear.leftArc(0.1);
      else
        gear.rightArc(0.1);
    }
  }

  public static void main(String[] args)
  {
    new SimEx9();
  }

  // ------------------ Environment --------------------------
  private static void _init(final GameGrid gg)
  {
    gg.setTitle("Drag with left mouse button to draw the track");
    gg.addMouseListener(
      new GGMouseListener()
      {
        public boolean mouseEvent(GGMouse mouse)
        {
          Location loc =
            gg.toLocationInGrid(mouse.getX(), mouse.getY());
          switch (mouse.getEvent())
          {
            case GGMouse.lPress:
              xOld = loc.x;
              yOld = loc.y;
              break;
            case GGMouse.lDrag:
              gg.getBg().drawLine(xOld, yOld, loc.x, loc.y);
              xOld = loc.x;
              yOld = loc.y;
              break;
            case GGMouse.lRelease:
              Monitor.wakeUp();  // Start simulation
              break;
          }
          return true;
        }
      }, GGMouse.lPress | GGMouse.lDrag | GGMouse.lRelease);
    gg.getBg().setPaintColor(Color.darkGray);
    gg.getBg().setLineWidth(32);
  }
}
