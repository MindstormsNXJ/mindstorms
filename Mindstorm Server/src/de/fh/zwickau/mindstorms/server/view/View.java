package de.fh.zwickau.mindstorms.server.view;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Event;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.text.html.HTMLDocument.HTMLReader.PreAction;

import de.fh.zwickau.mindstorms.server.Server;
import de.fh.zwickau.mindstorms.server.navigation.TargetManager;
import de.fh.zwickau.mindstorms.server.navigation.mapping.Mapper;
import de.fh.zwickau.mindstorms.server.navigation.mapping.camera.Camera;
import de.fh.zwickau.mindstorms.server.view.graphic.GraphicCanvas;


/**
 * OpenGL View for mapping data
 * 
 * @author Andre Furchner
 *
 */
public class View {
    
	private Server controller;
	
    private GraphicCanvas graphicCanvas;    // OpenGL View
    private JFrame jFrame;
    
    // Buttons
    private JButton buttonSaveMap;
    private JButton buttonOpenPhotoAnalyzer;;

	
	public View() {
	    initButtons();
	    
	    jFrame = new JFrame();                                                 // main window
	    jFrame.setSize(900, 600);
	    jFrame.setVisible(true);
	    
	    JPanel mainPanel = new JPanel();
	    
	    // Add OpenGL Panel /////////////////////////////////////////////////////
	    Canvas graphicPanel = new Canvas();

	    int n_pixel = 512; // must be power of 2 (64 , 128, 256 ...)
	    graphicPanel.setSize(n_pixel + 1, n_pixel + 1);
	    
	    mainPanel.add(graphicPanel);
	    /////////////////////////////////////////////////////////////////////////
	    
	    
	    
	    // Add Button Panel /////////////////////////////////////////////////////
	    JPanel buttonPanel = new JPanel();
	    
	    buttonPanel.add(buttonSaveMap);
	    buttonPanel.add(buttonOpenPhotoAnalyzer);
	    mainPanel.add(buttonPanel);
	    /////////////////////////////////////////////////////////////////////////
	    jFrame.getContentPane().add(mainPanel);
	    
	    graphicCanvas = new GraphicCanvas(graphicPanel);
	}
	
	private void initButtons(){
	    buttonSaveMap = new JButton("Save Map");
	    buttonSaveMap.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent arg0) {
                controller.saveMap();
            }
	    });
	    buttonOpenPhotoAnalyzer= new JButton("Analyze Photo");
	    buttonOpenPhotoAnalyzer.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent arg0) {
                controller.setPhotoAnalyzerVisible();
            }
	    });

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
		this.controller = controller;
	}
	
}
