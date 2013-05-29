package de.fh.zwickau.mindstorms.server.navigation.mapping.camera;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
/**
 * A Klass who makes a scaled Jframe Component out of a buffered Image
 * @author Aismael
 *
 */
class ImageComponent extends JComponent
{
  private static final long serialVersionUID = 8055865896136562197L;

  private BufferedImage image;
  private float scale;

  /**
   * 
   * @param imageIn
   */
  public void setImage( BufferedImage imageIn )
  {
    this.image = new BufferedImage(512, 512,  BufferedImage.TYPE_INT_ARGB);
    this.image.getGraphics().drawImage(imageIn, 0,0, 512,512, null);
    setPreferredSize( new Dimension(image.getWidth(), image.getHeight()) );
    scale=imageIn.getHeight()/512;
    System.out.println(scale);
    repaint();
    invalidate();
  }

  @Override
  protected void paintComponent( Graphics g )
  {
    if ( image != null )
      g.drawImage( image, 0, 0, this );
  }

public float getScale() {
	return scale;
}
}
