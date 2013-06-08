package de.fh.zwickau.mindstorms.server.navigation.mapping.camera;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import javax.imageio.ImageIO;
import org.lwjgl.BufferUtils;

/**
 * The camera is used to create/load images from the Environment.
 * 
 * @author Martin Pezold
 * 
 */
public class Camera {

	ByteBuffer byteBuffer = null;
	BufferedImage image = null;
	float[] obstacle = { 0.2f, 0.2f, 0.15f, 0.2f };
	float[] obstacle2 = { 0.0f, 0.0f, 0.0f, 0.2f };
	float[] ball = { 0.0f, 0, 0.8f, 0.2f };
	float[] robot = { 0.0f, 0.0f, 0.0f, 0.2f };
	float[] goal = { 1.0f, 1.0f, 0.0f, 0.2f };
	double xScale,yScale=10;

	public double getxScale() {
		return xScale;
	}

	public void setxScale(double xScale) {
		this.xScale = xScale;
	}

	public double getyScale() {
		return yScale;
	}

	public void setyScale(double yScale) {
		this.yScale = yScale;
	}

	public float[] getBall() {
		return ball;
	}

	public void setBall(float[] ball) {
		this.ball = ball;
	}

	public float[] getObstacle() {
		return obstacle;
	}

	public void setObstacle(float[] obstacle) {
		this.obstacle = obstacle;
	}

	public float[] getObstacle2() {
		return obstacle2;
	}

	public void setObstacle2(float[] obstacle2) {
		this.obstacle2 = obstacle2;
	}

	public float[] getRobot() {
		return robot;
	}

	public void setRobot(float[] robot) {
		this.robot = robot;
	}

	public float[] getGoal() {
		return goal;
	}

	public void setGoal(float[] goal) {
		this.goal = goal;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	int width, height;

	public Camera() {
		readNewPhoto("bild.bmp");
	}

	/**
	 * Read a new Image from disk.
	 * 
	 * @param input
	 *            filename
	 */
	public void readNewPhoto(String input) {

		try {
			File imagefile = new File("content/photos/" + input);
			image = ImageIO.read(imagefile);
			width = image.getWidth();
			height = image.getHeight();
		} catch (IOException e) {
			System.err.println("Could not load camera Image: content/photos/" + input);	
			e.printStackTrace();
		}
		byteBuffer = makeByteBuffer(image);
		System.out.println("Success");
	}

	/**
	 * Convert an Image to a ByteBuffer.
	 * 
	 * @param image
	 *            Image
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

	public int getImageWidth() {
		return width;
	}

	public int getImageHeight() {
		return height;
	}

	public ByteBuffer getByteBuffer() {
		return byteBuffer;
	}

	public BufferedImage getImage() {
		return image;
	}

}
