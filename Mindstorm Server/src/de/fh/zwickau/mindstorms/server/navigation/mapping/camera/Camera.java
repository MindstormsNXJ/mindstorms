package de.fh.zwickau.mindstorms.server.navigation.mapping.camera;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.imageio.ImageIO;

import org.jfree.chart.util.HexNumberFormat;
import org.lwjgl.BufferUtils;

import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;

import de.fh.zwickau.mindstorms.server.navigation.mapping.Mapper;
import de.fh.zwickau.mindstorms.server.view.graphic.CameraMapper;

/**
 * The camera is used to create/load images from the Environment.
 * 
 * @author Martin Pezold
 * 
 */
public class Camera {

	ByteBuffer byteBuffer = null;
	FloatBuffer computedBuffer = null;
	BufferedImage image = null;
	float[] ball = { 0.0f, 0, 0.8f, 0.2f };
	float[] robot = { 0.0f, 0.0f, 0.0f, 0.2f };
	float[] goal = { 1.0f, 1.0f, 0.0f, 0.2f };
	Point goalPointOn64Grid=new Point(0, 0);
	Point ballPointOn64Grid=new Point(0, 0);;
	Point obstaclePoint=new Point(0, 0);
	float scaleFromOriginal;
	double xScale, yScale = 10;
	Mapper mapper;


	public float getScaleFromOriginal() {
		return scaleFromOriginal;
	}

	public void setScaleFromOriginal(float scaleFromOriginal) {
		this.scaleFromOriginal = scaleFromOriginal;
	}


	float[] obstacle = { 0.2f, 0.2f, 0.15f, 0.2f };

	public Point getGoalPointOn64Grid() {
		return goalPointOn64Grid;
	}

	public void setGoalPointOn64Grid(Point goalPoint) {
		this.goalPointOn64Grid = goalPoint;
	}

	public Point getBallPointOn64Grid() {
		return ballPointOn64Grid;
	}

	public void setBallPointOn64Grid(Point ballPoint) {
		this.ballPointOn64Grid = ballPoint;
	}

	public Point getObstaclePoint() {
		return obstaclePoint;
	}

	public void setObstaclePoint(Point obstaclePoint) {
		this.obstaclePoint = obstaclePoint;
	}

	public Mapper getMapper() {
		return mapper;
	}

	public void registerMapper(Mapper mapper) {
		this.mapper = mapper;
	}

	public double getxScale() {
		return xScale;
	}

	public float  getxScaleFor64Grid() {
		float pixelPerCm=(float) ((xScale/scaleFromOriginal)/8.0f);
		float cmPerPixel=1/pixelPerCm;
		return cmPerPixel;
	}

	public void setxScale(double xScale) {
		this.xScale = xScale;
	}

	public double getyScale() {
		return yScale;
	}

	public float getyScaleFor64Grid() {
		float pixelPerCm=(float) ((yScale/scaleFromOriginal)/8.0f);
		float cmPerPixel=1/pixelPerCm;
		return cmPerPixel;
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

	public void setComputedBuffer(FloatBuffer computeOutput) {
		this.computedBuffer = computeOutput;
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

	public void analyzeDataRegister() {
		mapper.setBallPosition(makePointTolejosPoint(ballPointOn64Grid));
		mapper.setGoalPosition(makePointTolejosPoint(goalPointOn64Grid));
		System.out.println("Registriere Photodaten");
		analyzeObstaclemap();
		System.out.println("Registrierung beendet");
	}
	
	public void analyzeObstaclemap(){
		float[] obstaclePoints=new float[computedBuffer.remaining()];
		computedBuffer.get(obstaclePoints);
		int k=0;
		float verg= 1.0f;
		for (int i=0;i<64;i++){
			for (int j=0;j<64;j++){
				if(obstaclePoints[k]==verg){
					System.out.println("ein hinderniss "+obstaclePoints[k]);
					Point obPoint= new Point(j*8, i*8);
					mapper.addObstacle(makePointTolejosPoint(obPoint));
				}else{
					System.out.println("kein hinderniss "+obstaclePoints[k]);
				}
			
				k++;
			}
		}
	}

	private lejos.geom.Point makePointTolejosPoint(Point toChange){
		lejos.geom.Point lePoint=new lejos.geom.Point (((float)toChange.getX()-32)*getxScaleFor64Grid(),( (float)toChange.getY()-32)*getyScaleFor64Grid());
		return lePoint;
	}
}
