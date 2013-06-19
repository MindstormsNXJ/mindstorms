package de.fh.zwickau.mindstorms.server.navigation.mapping.camera;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
/**
 * A Klass who makes a scaled Jframe Component out of a buffered Image , the Jframe Component is to make ,
 * because a Jframe Gui can only draw Components
 * @author Aismael
 *
 */
public class ImageComponent extends JComponent
{
  private static final long serialVersionUID = 8055865896136562197L;

  private BufferedImage image;//the Buffered Image who is to Convert
  private float scale;//the Scaling rate from buffered image to a 512*512 Jframe Component

  /**
 * the Method who can give the Bufferedimage of the Converted Picture to a Gui
 * @return  Bufferedimage which is set
 */
public BufferedImage getImage() {
	return image;
}


/**
 * returns thes scaling from orginal to Jframe Component
 * @return the Scaling rate from buffered image to a 512*512 Jframe Component
 */
public float getScale() {
	return scale;
}

/**
 * The Method who draws the Component on a Jcontainer
 */
@Override
  protected void paintComponent( Graphics g )
  {
    if ( image != null )
      g.drawImage( image, 0, 0, this );
  }

/**
   * 
   * @param imageIn the Buffered Image who is to Convert
   */
  public void setImage( BufferedImage imageIn )
  {
    this.image = new BufferedImage(512, 512,  BufferedImage.TYPE_INT_RGB);
    this.image.getGraphics().drawImage(imageIn, 0,0, 512,512, null);
    setPreferredSize( new Dimension(image.getWidth(), image.getHeight()) );
    scale=imageIn.getHeight()/512;
    System.out.println(scale);
    repaint();
    invalidate();
  }
}
