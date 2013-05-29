package de.fh.zwickau.mindstorms.server.navigation.mapping.camera;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import javax.imageio.ImageIO;
import org.lwjgl.BufferUtils;

/**
 * The camera is used to create/load images from the
 * Environment.
 * 
 * @author Martin Pezold
 *
 */
public class Camera {
    
	ByteBuffer byteBuffer = null;
	
	int width, height;
	
    public Camera(){
    	readNewPhoto("bild.bmp");
    }
    
    
    /**
     * Read a new Image from disk.
     * @param input filename
     */
    public void readNewPhoto(String input){
		
    	BufferedImage image = null;
    	
        try {
            File imagefile = new File("content/photos/"+input);
            image = ImageIO.read(imagefile);
            width = image.getWidth();
            height = image.getHeight();

        } catch (IOException e) {
              e.printStackTrace();
        }
        byteBuffer=makeByteBuffer(image);
        System.out.println("Success");
    }

    /**
     * Convert an Image to a ByteBuffer.
     * @param image Image
     * @return ByteBuffer from image
     */
    public static ByteBuffer makeByteBuffer(BufferedImage image){
       
        int[] pixels = new int[image.getWidth() * image.getHeight()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

        ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 3);
         
        for(int y = 0; y < image.getHeight(); y++){
            for(int x = 0; x < image.getWidth(); x++){
                int pixel = pixels[y * image.getWidth() + x];
                buffer.put((byte) ((pixel >> 16) & 0xFF));     // Red component
                buffer.put((byte) ((pixel >> 8) & 0xFF));      // Green component
                buffer.put((byte) (pixel & 0xFF));             // Blue component
            }
        }
        buffer.rewind();
        return buffer;
    }

    public int getImageWidth(){
        return width;
    }
    
    public int getImageHeight(){
        return height;
    }
    
	public ByteBuffer getByteBuffer() {
		return byteBuffer;
	}

}
    