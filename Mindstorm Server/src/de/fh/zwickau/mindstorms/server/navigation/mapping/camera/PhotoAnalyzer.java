package de.fh.zwickau.mindstorms.server.navigation.mapping.camera;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class PhotoAnalyzer {

	private JFrame jFrame;//main Window of the analyze gui
	protected JButton buttonClosePhotoAnalyzer, buttonMakeScaling,
			buttonTakeGoal, buttonTakeBall, buttonTakeRobot,
			buttonTakeObstacle;//buttons for Analyzation
	private Camera camera;//the camera which holds all Datas
	private ImageComponent shownAnalyzePicture;//the shown Picture wich to analyze
	private JScrollPane jScrollPane;
	private Container mainPanel;
	protected float R;//intern Redfloat
	protected float G;//intern Greenfloat
	protected float B;//intern BlueFloat
	private JButton buttonToChange;//intern button whos parameters are to change
	private int count=0;//clickcount
	private Point[] scalePoints;//internpoints for scaling
	private boolean pickInprocess=false;//boolean if in the Moment is a piccking process running

	/**
	 * Konstruktor of the Photoanalyzer gui
	 * @param camera the camera which holds all Datas
	 */
	public PhotoAnalyzer(Camera camera) {
		scalePoints=new Point[4];
		this.camera = camera;
		initButtons();
		jFrame = new JFrame(); // main window
		jFrame.setSize(900, 600);
		jFrame.setVisible(false);
		mainPanel = new JPanel();
		JPanel buttonPanel = new JPanel();// JPanel for the Buttons
		// Make Scaled Image out of the Photo
		shownAnalyzePicture = new ImageComponent();
		shownAnalyzePicture.setImage(camera.getImage());

		// Initialize Panels on the Windows
		mainPanel.setLayout(new BorderLayout());
		jScrollPane = new JScrollPane(shownAnalyzePicture);
		mainPanel.add(jScrollPane, BorderLayout.CENTER);
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);
		buttonPanel.add(buttonClosePhotoAnalyzer);
		buttonPanel.add(buttonMakeScaling);
		buttonPanel.add(buttonTakeGoal);
		buttonPanel.add(buttonTakeBall);
		buttonPanel.add(buttonTakeObstacle);
		
		camera.setScaleFromOriginal(shownAnalyzePicture.getScale());
		jFrame.getContentPane().add(mainPanel);

	}

	/**
	 * Method who calculate the Scaling from the given Points
	 * @param choords given Points
	 */
	private void calculateScaling(Point[] choords){
		double x =Math.abs(scalePoints[0].getX()-scalePoints[1].getX());
		double y =Math.abs(scalePoints[2].getY()-scalePoints[3].getY());
		camera.setxScale((x*shownAnalyzePicture.getScale())/60);
		camera.setyScale((y*shownAnalyzePicture.getScale())/60);
		System.out.println(camera.getxScale());
		System.out.println(camera.getyScale());
	}

	/**
	 * method who Inits a specific actionlistener on every button
	 */
	private void initButtons() {

		buttonClosePhotoAnalyzer = new JButton("Analyze Photo Completed");
		buttonClosePhotoAnalyzer.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				camera.fillMap();
				jFrame.setVisible(false);
			}

		});

		buttonMakeScaling = new JButton("Make Scaling");
		buttonMakeScaling.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				makeScaling();
			}

		});
		buttonTakeGoal = new JButton("TakeGoal");
		buttonTakeGoal.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				takeColorandPositionOfSomething(camera.getGoal(),camera.getGoalPointOn64Grid(),buttonTakeGoal);
				
			}

		});
		buttonTakeObstacle = new JButton("TakeObstacle");
		buttonTakeObstacle.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				takeColorandPositionOfSomething(camera.getObstacle(),camera.getObstaclePoint(),buttonTakeObstacle);
				
			}

		});
		buttonTakeBall = new JButton("TakeBall");
		buttonTakeBall.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				takeColorandPositionOfSomething(camera.getBall(),camera.getBallPointOn64Grid(),buttonTakeBall);
				
			}

		});
	}

	/**
	 * Method for the Picking Process of the Scaling points
	 */
	private void makeScaling() {
		
		if(!pickInprocess){
			pickInprocess=true;
			buttonMakeScaling.setForeground(Color.cyan);
		System.out.println("als erstes zwei Punkte der X Scalieung angeben");
		System.out.println("danach zwei Punkte der Y Scalieung ");
		jScrollPane.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				
				if(takeMouseCoordinates(e.getX(), e.getY())){
					
				}else{
				jScrollPane.removeMouseListener(this);
				buttonMakeScaling.setForeground(Color.black);
				count=0;
				pickInprocess=false;
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {

			}

			@Override
			public void mouseExited(MouseEvent e) {

			}

			@Override
			public void mousePressed(MouseEvent e) {

			}

			@Override
			public void mouseReleased(MouseEvent e) {

			}
		});
		mainPanel.add(jScrollPane, BorderLayout.CENTER);
		}
	}
		
	
	

	/**
	 * make the PhotoAnalyzer Visible
	 */
	public void setPhotoAnalyzerVisible() {
		jFrame.setVisible(true);
	}
/**
 * Method who reads the position and color of a Pixel when a button is pressed
 * @param RGBCamera place where the Color is to write
 * @param TargetPosition place where the Position is to write
 * @param buttonToChange2 the button who had to be pressed 
 */
	private void takeColorandPositionOfSomething(float[] RGBCamera,Point TargetPosition, JButton buttonToChange2) {
		final float[] camera=RGBCamera;
		final Point cameraPoint=TargetPosition;
		if(!pickInprocess){
			pickInprocess=true;
			buttonToChange2.setForeground(Color.cyan);
		buttonToChange=buttonToChange2;
		jScrollPane.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				cameraPoint.setX(e.getX()/8-32);
				cameraPoint.setY(e.getY()/8-32);
				int pixel = shownAnalyzePicture.getImage().getRGB(e.getX(), e.getY());
				R = (float) ((pixel >> 16) & 0xFF) / 255; // Red
				G=(float)((pixel >> 8) & 0xFF)/255;
				B=(float)(pixel & 0xFF)/255;
				System.out.println("pick");
				jScrollPane.removeMouseListener(this);
				buttonToChange.setForeground(Color.black);
				pickInprocess=false;
				camera[0]=R;
				camera[1]=G;
				camera[2]=B;
		
						// component
			}
			@Override
			public void mouseEntered(MouseEvent e) {
			}
			@Override
			public void mouseExited(MouseEvent e) {
			}
			@Override
			public void mousePressed(MouseEvent e) {
			}
			@Override
			public void mouseReleased(MouseEvent e) {
			}
		});
		mainPanel.add(jScrollPane, BorderLayout.CENTER);
	
		}
	}
	
	/**
	 * Method who tooks a double Points into the scalepoint arrey out of the Mousechoords
	 * @param x mouse choord x
	 * @param y mouse choord y
	 * @return boolean if the scalepoint arrey is full
	 */
	private boolean takeMouseCoordinates(double x,double y){
		System.out.println(count);
		scalePoints[count]=new Point(x, y);
	if(count<3){
		count++;
		return true;
	}else{
		calculateScaling(scalePoints);
		return false;
	}
	}
}