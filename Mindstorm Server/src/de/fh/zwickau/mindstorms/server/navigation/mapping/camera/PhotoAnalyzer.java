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

	public PhotoAnalyzer(Camera camera) {
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
				buttonTakeGoal.setForeground(Color.cyan);
				takeSth(camera.getGoal(),buttonTakeGoal);
				
			}

		});
		buttonTakeBall = new JButton("TakeObstacle");
		buttonTakeBall.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				buttonTakeBall.setForeground(Color.cyan);
				takeSth(camera.getBall(),buttonTakeBall);
				
			}

		});
		buttonTakeObstacle = new JButton("TakeBall");
		buttonTakeObstacle.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				buttonTakeObstacle.setForeground(Color.cyan);
				takeSth(camera.getObstacle(),buttonTakeObstacle);
				
			}

		});
	}

	protected void takeSth(float[] RGBCamera, JButton buttonToChange2) {
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
				int pixel = IC.getImage().getRGB(e.getX(), e.getY());
				R = (float) ((pixel >> 16) & 0xFF) / 255; // Red
				G=(float)((pixel >> 8) & 0xFF)/255;
				B=(float)(pixel & 0xFF)/255;
				System.out.println("pick");
				SC.removeMouseListener(this);
				buttonToChange.setForeground(Color.black);
						// component
			}
		});
		mainPanel.add(SC, BorderLayout.CENTER);
		RGBCamera[0]=R;
		RGBCamera[1]=G;
		RGBCamera[2]=B;
	}
		
	
	

	public void makeScaling() {
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
				System.out.println(e.getX());
				System.out.println(e.getY());
				int pixel = IC.getImage().getRGB(e.getX(), e.getY());
				System.out.println((float) ((pixel >> 16) & 0xFF) / 255); // Red
																			// component
				System.out.println(((pixel >> 8) & 0xFF)); // Green component
				System.out.println((pixel & 0xFF)); // Blue component
			}
		});
		mainPanel.add(SC, BorderLayout.CENTER);
	}

}