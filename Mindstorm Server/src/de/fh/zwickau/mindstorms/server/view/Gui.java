package de.fh.zwickau.mindstorms.server.view;

import de.fh.zwickau.mindstorms.server.Server;
import de.fh.zwickau.mindstorms.server.navigation.TargetManager;
import de.fh.zwickau.mindstorms.server.navigation.mapping.Mapper;
import de.fh.zwickau.mindstorms.server.navigation.mapping.camera.Camera;
import de.fh.zwickau.mindstorms.server.view.graphic.GraphicCanvas;

import java.applet.Applet;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class Gui extends Applet implements ActionListener {
	private static final long serialVersionUID = 1L;
	JFrame window = new JFrame();
	private SystemOutConsole console;
	private Server server;
	private GraphicCanvas graphicCanvas;

	public Gui() {

		window.setSize(1000, 730);

		JPanel mainPane = new JPanel();
		{
			window.add(mainPane);
			GridBagLayout gbl_main = new GridBagLayout();
			gbl_main.columnWidths = new int[] { 520, 475 };
			gbl_main.rowHeights = new int[] { 730 };
			mainPane.setLayout(gbl_main);

			JPanel westPane = new JPanel();
			{
				mainPane.add(westPane, new GridBagConstraints(0, 0, 1, 1, 0, 0,GridBagConstraints.CENTER, GridBagConstraints.BOTH,new Insets(0, 0, 0, 0), 0, 0));
				GridBagLayout gbl_west = new GridBagLayout();
				gbl_west.columnWidths = new int[] {513};
				gbl_west.rowHeights = new int[] { 520, 205 };
				westPane.setLayout(gbl_west);
				westPane.setBackground(Color.BLACK);

				JPanel buttonPane = new JPanel();
				{
					GridBagConstraints c = new GridBagConstraints();
					GridBagLayout gridbag = new GridBagLayout();
					westPane.add(buttonPane, new GridBagConstraints(0, 1, 1, 1,0, 0, GridBagConstraints.CENTER,GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0,0));
					buttonPane.setLayout(gridbag);
					buttonPane.setBackground(Color.BLACK);

					c.fill = GridBagConstraints.BOTH;
					c.gridwidth = GridBagConstraints.REMAINDER;
					c.gridheight = 2;

					// textfield for forward backward turnto
					final JTextField text = new JTextField();
					gridbag.setConstraints(text, c);

					// save map button
					JButton saveMapButton = new JButton("save Map");
					gridbag.setConstraints(saveMapButton,c);
					saveMapButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							server.saveMap();
						}
					});
					
	                // Camera settings button
                    JButton cameraSettingsButton = new JButton("Camera Settings");
                    gridbag.setConstraints(cameraSettingsButton,c);
                    cameraSettingsButton.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            server.setPhotoAnalyzerVisible();
                        }
                    });
					
					// forward button
					JButton forbutton = new JButton("forward");
					forbutton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							final int number = Integer.parseInt(text.getText());
							server.getConnectionManager().sendForwardCommand(
									number);
						}
					});

					// backward button
					JButton bacbutton = new JButton("backward");
					gridbag.setConstraints(bacbutton, c);
					bacbutton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							final int number = Integer.parseInt(text.getText());
							server.getConnectionManager().sendBackwardCommand(
									number);
						}
					});

					// turn button
					JButton turbutton = new JButton("turn");
					gridbag.setConstraints(turbutton, c);
					turbutton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							final int number = Integer.parseInt(text.getText());
							server.getConnectionManager().sendTurnCommand(
									number);
						}
					});

					// pickbutton
					JButton picbutton = new JButton("pick");
					picbutton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							final int number = Integer.parseInt(text.getText());
							server.getConnectionManager().sendPickCommand(
									number);
						}
					});

					// dropbutton
					JButton drobutton = new JButton("drop");
					gridbag.setConstraints(drobutton, c);
					drobutton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							final int number = Integer.parseInt(text.getText());
							server.getConnectionManager().sendDropCommand(
									number);
						}
					});

					// button do cancel the connection to the brick
					JButton exibutton = new JButton("exit");
					gridbag.setConstraints(exibutton, c);
					exibutton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							server.getConnectionManager().terminate();
						}
					});

					// button to try to start a connection to the brick
					JButton stabutton = new JButton("start");
					gridbag.setConstraints(stabutton, new GridBagConstraints());
					stabutton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {

						}
					});

					buttonPane.add(saveMapButton);
					buttonPane.add(cameraSettingsButton);
					buttonPane.add(stabutton);
					buttonPane.add(exibutton);
					buttonPane.add(forbutton);
					buttonPane.add(bacbutton);
					buttonPane.add(turbutton);
					buttonPane.add(picbutton);
					buttonPane.add(drobutton);
					buttonPane.add(text);
				}
			}

			Canvas graphicPanel = new Canvas();
			{
				westPane.add(graphicPanel,new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0, 0));
			}
			
			graphicCanvas = new GraphicCanvas(graphicPanel);

			JPanel eastPane = new JPanel();
			{
				mainPane.add(eastPane, new GridBagConstraints(1, 0, 1, 1, 0, 0,GridBagConstraints.CENTER, GridBagConstraints.BOTH,new Insets(0, 0, 0, 0), 0, 0));
				eastPane.setLayout(new BorderLayout());
				eastPane.setBackground(Color.GREEN);
				console = new SystemOutConsole(System.out);
				{
					eastPane.add(console, BorderLayout.CENTER);
					JScrollPane consoleScrollPane = new JScrollPane(console);
					eastPane.add(consoleScrollPane);
					consoleScrollPane.setViewportBorder(null);
				}
			}
		}

		window.setVisible(true);
		window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}

	@Override
	public void actionPerformed(ActionEvent e) {

	}
	
	public void start(){
	    graphicCanvas.start();
	}
	
	/**
	 * Register the Mapper to be observed.
	 * 
	 * @param mapper
	 */
	public void registerMapper(Mapper mapper) {
	    graphicCanvas.setMapper(mapper);
	}
	
	/**
	 * Register the TargetManager to be observed.
	 * 
	 * @param tM TargetManager
	 */
	public void registerTargetManager(TargetManager tM) {
	    graphicCanvas.setTargetManager(tM);
	}
	
	/**
     * Register the Camera to be observed.
     * 
     * @param tM TargetManager
     */
    public void registerCamera(Camera camera) {
        graphicCanvas.setCamera(camera);
    }
	
    /**
     * Tells view that the map has changed (Thread safe)
     */
    public void mapChanged() {
        graphicCanvas.mapChanged();
        // Add Optional refresh for view here
    }

    /**
     * Tells view that the Target has changed (Thread safe)
     */
    public void targetChanged() {
        graphicCanvas.targetChanged();
        // Add Optional refresh for view here
    }
    
	public void setController(Server controller) {
		server = controller;
	}
}