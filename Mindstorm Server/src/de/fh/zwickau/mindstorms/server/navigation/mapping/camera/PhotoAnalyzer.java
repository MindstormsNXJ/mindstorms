package de.fh.zwickau.mindstorms.server.navigation.mapping.camera;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class PhotoAnalyzer {

	private JFrame jFrame;
	protected JButton buttonClosePhotoAnalyzer, buttonMakeScaling,
			buttonTakeGoal, buttonTakeBall, buttonTakeRobot,
			buttonTakeObstacle;
	private Camera camera;
	private ImageComponent IC;
	private JScrollPane SC;
	private Container mainPanel;
	protected float R;
	protected float G;
	protected float B;
	private JButton buttonToChange;
	private int count=0;
	private Point[] scalePoints;
	private boolean pickInprocess=false;

	public PhotoAnalyzer(Camera camera) {
		scalePoints=new Point[4];
		this.camera = camera;
		initButtons();
		jFrame = new JFrame(); // main window
		jFrame.setSize(900, 600);
		jFrame.setVisible(false);
		mainPanel = new JPanel();
		JPanel buttonPanel = new JPanel();// Jpanel for the Buttons
		// Make Scaled Image out of the Photo
		IC = new ImageComponent();
		IC.setImage(camera.getImage());

		// initialise Panels on the Windows
		mainPanel.setLayout(new BorderLayout());
		SC = new JScrollPane(IC);
		mainPanel.add(SC, BorderLayout.CENTER);
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);
		buttonPanel.add(buttonClosePhotoAnalyzer);
		buttonPanel.add(buttonMakeScaling);
		buttonPanel.add(buttonTakeGoal);
		buttonPanel.add(buttonTakeBall);
		buttonPanel.add(buttonTakeObstacle);
		
		camera.setScaleFromOriginal((float)IC.getScale());
		jFrame.getContentPane().add(mainPanel);

	}

	/**
	 * make the PhotoAnalyzer Visible
	 */
	public void setPhotoAnalyzerVisible() {
		jFrame.setVisible(true);
	}

	private void initButtons() {

		buttonClosePhotoAnalyzer = new JButton("Analyze Photo Completed");
		buttonClosePhotoAnalyzer.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				//jFrame.setVisible(false);
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
				takeColorOfSomething(camera.getGoal(),camera.getGoalPointOn64Grid(),buttonTakeGoal);
				
			}

		});
		buttonTakeObstacle = new JButton("TakeObstacle");
		buttonTakeObstacle.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				takeColorOfSomething(camera.getObstacle(),camera.getObstaclePoint(),buttonTakeObstacle);
				
			}

		});
		buttonTakeBall = new JButton("TakeBall");
		buttonTakeBall.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				takeColorOfSomething(camera.getBall(),camera.getBallPointOn64Grid(),buttonTakeBall);
				
			}

		});
	}

	protected void takeColorOfSomething(float[] RGBCamera,Point TargetPosition, JButton buttonToChange2) {
		final float[] camera=RGBCamera;
		final Point cameraPoint=TargetPosition;
		if(!pickInprocess){
			pickInprocess=true;
			buttonToChange2.setForeground(Color.cyan);
		buttonToChange=buttonToChange2;
		SC.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
			}
			@Override
			public void mousePressed(MouseEvent e) {
			}
			@Override
			public void mouseExited(MouseEvent e) {
			}
			@Override
			public void mouseEntered(MouseEvent e) {
			}
			@Override
			public void mouseClicked(MouseEvent e) {
				cameraPoint.setX(e.getX()/8-32);
				cameraPoint.setY(e.getY()/8-32);
				int pixel = IC.getImage().getRGB(e.getX(), e.getY());
				R = (float) ((pixel >> 16) & 0xFF) / 255; // Red
				G=(float)((pixel >> 8) & 0xFF)/255;
				B=(float)(pixel & 0xFF)/255;
				System.out.println("pick");
				SC.removeMouseListener(this);
				buttonToChange.setForeground(Color.black);
				pickInprocess=false;
				camera[0]=R;
				camera[1]=G;
				camera[2]=B;
		
						// component
			}
		});
		mainPanel.add(SC, BorderLayout.CENTER);
	
		}
	}
		
	
	

	public void makeScaling() {
		
		if(!pickInprocess){
			pickInprocess=true;
			buttonMakeScaling.setForeground(Color.cyan);
		System.out.println("als erstes zwei Punkte der X Scalieung angeben");
		System.out.println("danach zwei Punkte der Y Scalieung ");
		SC.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				// TO

			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseClicked(MouseEvent e) {
				
				boolean onOff=true;
				if(takeMouseCoordinates(e.getX(), e.getY())&onOff){
					
					onOff=false;
				}else{
				SC.removeMouseListener(this);
				buttonMakeScaling.setForeground(Color.black);
				count=0;
				}
				pickInprocess=false;
			}
		});
		mainPanel.add(SC, BorderLayout.CENTER);
		}
	}

	public boolean takeMouseCoordinates(double x,double y){
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
	public void calculateScaling(Point[] choords){
		double x =Math.abs(scalePoints[0].getX()-scalePoints[1].getX());
		double y =Math.abs(scalePoints[2].getY()-scalePoints[3].getY());
		camera.setxScale((x*IC.getScale())/60);
		camera.setyScale((y*IC.getScale())/60);
		System.out.println(camera.getxScale());
		System.out.println(camera.getyScale());
	}
}