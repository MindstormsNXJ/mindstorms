package de.fh.zwickau.mindstorms.server.navigation.mapping;

import java.awt.BorderLayout;
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

import de.fh.zwickau.mindstorms.server.navigation.mapping.camera.Camera;

public class PhotoAnalyzer {

	private JFrame jFrame;
	private JButton buttonClosePhotoAnalyzer, buttonMakeScaling;
	private Camera camera;
	private ImageComponent IC;
	private JScrollPane SC;
	private Container mainPanel;

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
				System.out.println("wo ist die syso hin");
			makeScaling();
			}

		});
	}

	public void makeScaling() {
		SC.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub

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
			}
		});
		mainPanel.add(SC, BorderLayout.CENTER);
	}
}
