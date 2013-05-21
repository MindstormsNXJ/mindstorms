package de.fh.zwickau.mindstorms.server.navigation.mapping.camera;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;

import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;

public class Camera {
    
	ByteBuffer byteBuffer = null;
    public Camera(){
    	readNewPhoto("test.bmp");
    }
    
    
    public void readNewPhoto(String input){
		
    	BufferedImage image = null;
    	
        try {

              
            File imagefile = new File("content/photos/"+input);
            image = ImageIO.read(imagefile);

            

        } catch (IOException e) {
              e.printStackTrace();
        }
        byteBuffer=makeByteBuffer(image);
        System.out.println("Success");
    }
    
    public static ByteBuffer makeByteBuffer(BufferedImage image){
       
       int[] pixels = new int[image.getWidth() * image.getHeight()];
         image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

         ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 3);
         
         for(int y = 0; y < image.getHeight(); y++){
             for(int x = 0; x < image.getWidth(); x++){
                 int pixel = pixels[y * image.getWidth() + x];
                 buffer.put((byte) ((pixel >> 16) & 0xFF));     // Red component
//                 System.out.println(((pixel >> 16) & 0xFF));
                 buffer.put((byte) ((pixel >> 8) & 0xFF));      // Green component
//                 System.out.println(((pixel >> 8) & 0xFF));
                 buffer.put((byte) (pixel & 0xFF));               // Blue component
//                 System.out.println((pixel & 0xFF));
             }
         }
         buffer.rewind();
		return buffer;
}


	public ByteBuffer getByteBuffer() {
		return byteBuffer;
	}

}
    