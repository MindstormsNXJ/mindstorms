package examples;
// SimEx13.java
// Creating obstacles programmatically

import ch.aplu.nxtsim.*;
import ch.aplu.jgamegrid.*;
import java.awt.*;

public class SimEx13
{
  public SimEx13()
  {
    NxtRobot robot = new NxtRobot();
    Gear gear = new Gear();
    TouchSensor ts = new TouchSensor(SensorPort.S3);
    robot.addPart(gear);
    robot.addPart(ts);
    gear.setSpeed(30);
    gear.forward();
    while (true)
    {
      if (ts.isPressed())
      {
        gear.backward(1200);
        gear.left(750);
        gear.forward();
      }
    }
  }

  public static void main(String[] args)
  {
    new SimEx13();
  }

  // ------------------ Environment --------------------------
  private static GGBitmap bar(int width, int length, Color color)
  {
    GGBitmap bm = new GGBitmap(width, length);
    bm.setPaintColor(color);
    bm.fillRectangle(new Point(0, 0), new Point(width - 1, length - 1));
    return bm;
  }

  private static GGBitmap circle(int radius,  Color color)
  {
    GGBitmap bm = new GGBitmap(2 * radius, 2 * radius);
    bm.setPaintColor(color);
    bm.setLineWidth(3);
    bm.drawCircle(new Point(radius, radius), radius - 1);
    return bm;
  }
  
  static
  {
    NxtContext.setStartPosition(300, 200);
    NxtContext.setStartDirection(30);
    NxtContext.showNavigationBar();
    NxtContext.useObstacle(bar(300, 20, Color.red), 250, 150);
    NxtContext.useObstacle(bar(300, 20, Color.green), 250, 350);
    NxtContext.useObstacle(bar(20, 300, Color.blue), 150, 250);
    NxtContext.useObstacle(bar(20, 300, Color.yellow), 350, 250);
    NxtContext.useObstacle(circle(20, Color.black), 250, 250);
  }

}
