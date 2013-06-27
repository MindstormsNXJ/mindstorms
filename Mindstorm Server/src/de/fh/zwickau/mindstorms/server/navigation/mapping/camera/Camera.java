package de.fh.zwickau.mindstorms.server.navigation.mapping.camera;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;

import de.fh.zwickau.mindstorms.server.navigation.mapping.Mapper;


/**
 * The camera is used to create/load images from the Environment and put the results onto the Map
 * 
 * @author Martin Pezold
 * 
 */
public class Camera {

	public Camera() {
		readNewPhoto("bild.BMP");
		mapfiller= new MappFiller();
	}

	MappFiller mapfiller ;
	ByteBuffer byteBuffer = null;	//	the intern Bytebuffer from the Original image,  
	FloatBuffer computedBuffer = null;//the intern Floatbuffer from the Original image
	BufferedImage image = null;//the intern BufferedImage from the Original image
	float[] ball = { 0.0f, 0, 0.8f, 0.2f };//Color of the Ball
	float[] goal = { 1.0f, 1.0f, 0.0f, 0.2f };//Color of the Goal
	float[] obstacle = { 0.2f, 0.2f, 0.15f, 0.2f };//Color of the obstacles
	Point goalPoint=new Point(0, 0);//Position of the goal 
	Point ballPoint=new Point(0, 0);//Position of the ball 
	float scaleFromOriginal;//the Scalingrate from the original Buffered image to the points who given from the Gui
	double xScale = 10;//scalings on the X   axes pixels per cm
	double yScale=10;//scalings on the Y   axes pixels per cm
	int width, height;//Width and high of the Original image to  make a correct bytebuffer



	/**
	 * Read a new Image from disk on a specifik folder
	 * 
	 * @param input name of the Image to Read
	 *            filename
	 */
	public void readNewPhoto(String input) {
	
		try {
			File imagefile = new File("content/photos/" + input);
			image = ImageIO.read(imagefile);
			width = image.getWidth();
			height = image.getHeight();
		} catch (IOException e) {
			System.err.println("Could not load camera Image: content/photos/"
					+ input);
			e.printStackTrace();
		}
		byteBuffer = makeByteBuffer(image);
		System.out.println("Success");
	}







	/**
	 * Convert an Image to a ByteBuffer.
	 * 
	 * @param image the Image who is to convert
	 * @return ByteBuffer from image
	 */
	public static ByteBuffer makeByteBuffer(BufferedImage image) {
	
		int[] pixels = new int[image.getWidth() * image.getHeight()];
		image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0,
				image.getWidth());
	
		ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth()
				* image.getHeight() * 3);
	
		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				int pixel = pixels[y * image.getWidth() + x];
				buffer.put((byte) ((pixel >> 16) & 0xFF)); // Red component
				buffer.put((byte) ((pixel >> 8) & 0xFF)); // Green component
				buffer.put((byte) (pixel & 0xFF)); // Blue component
			}
		}
		buffer.rewind();
		return buffer;
	}

	

	



	public float[] getBall() {
		return ball;
	}

	public Point getBallPointOn64Grid() {
		return ballPoint;
	}

	public ByteBuffer getByteBuffer() {
		return byteBuffer;
	}

	public float[] getGoal() {
		return goal;
	}

	public Point getGoalPointOn64Grid() {
		return goalPoint;
	}

	public BufferedImage getImage() {
		return image;
	}

	public int getImageHeight() {
		return height;
	}

	public int getImageWidth() {
		return width;
	}

	public float[] getObstacle() {
		return obstacle;
	}

	public Point getObstaclePoint() {
		return new Point(0,0);
	}

	public float getScaleFromOriginal() {
		return scaleFromOriginal;
	}

	public int getWidth() {
		return width;
	}

	public double getxScale() {
		return xScale;
	}

	public float  getxScaleFor64Grid() {
		float pixelPerCm=(float) ((xScale/scaleFromOriginal)/8.0f);
		float cmPerPixel=1/pixelPerCm;
		return cmPerPixel;
	}

	public double getyScale() {
		return yScale;
	}

	public float getyScaleFor64Grid() {
		float pixelPerCm=(float) ((yScale/scaleFromOriginal)/8.0f);
		float cmPerPixel=1/pixelPerCm;
		return cmPerPixel;
	}

	

	public void registerMapper(Mapper mapper) {
		mapfiller.setMapper(mapper);
	}

	public void setBall(float[] ball) {
		this.ball = ball;
	}

	public void setComputedBuffer(FloatBuffer computeOutput) {
		this.computedBuffer = computeOutput;
	}

	public void setGoal(float[] goal) {
		this.goal = goal;
	}

	public void setObstacle(float[] obstacle) {
		this.obstacle = obstacle;
	}

	public void setScaleFromOriginal(float scaleFromOriginal) {
		this.scaleFromOriginal = scaleFromOriginal;
	}

	public void setWidth(int width) {
		this.width = width;
	}
	
	public void setxScale(double xScale) {
		this.xScale = xScale;
	}

	public void setyScale(double yScale) {
		this.yScale = yScale;
	}






/**
 * give the Mapfiller the command and data to fill the map
 */
	public void fillMap() {
		mapfiller.analyzeAndWriteDataRegister(ballPoint, goalPoint,computedBuffer,getxScaleFor64Grid(),getyScaleFor64Grid());
		
	}
}
