package de.fh.zwickau.mindstorms.server.view;

import java.util.concurrent.Semaphore;

import lejos.geom.Line;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import de.fh.zwickau.mindstorms.server.navigation.mapping.MapGrid;
import de.fh.zwickau.mindstorms.server.navigation.mapping.Mapper;
import static org.lwjgl.opengl.GL11.*;

/**
 * OpenGL View for mapping data
 * 
 * @author Andre Furchner
 *
 */
public class View extends Thread {

	private Mapper mapper;
	private boolean mapChanged;
	private Semaphore semaphore;
	
	// Draw Items
	private float[] tileVertices;
	private float[] tileColors;
	private float[] lineVertices;

	//Draw options
	boolean drawLine = true;
	boolean drawTile = true;
	
	public View() {
		this.mapChanged = true;
		this.semaphore = new Semaphore(1);
	}

	@Override
	public void run() {
		createWindow();
		initialize();

		while (!Display.isCloseRequested()) {
			update();
			draw();
		}

		mapper.saveMap();
		Display.destroy();
	}

	/**
	 * Create the View Window
	 */
	private void createWindow() {
		try {
			Display.setDisplayMode(new DisplayMode(512 + 1, 512 + 1));
			Display.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.err.println("Failed to create an OpenGL Window :(");
		}

	}

	/**
	 * Initialize some OpenGL parameters and allocate memory
	 */
	private void initialize() {
		final int size = mapper.getGrid().getGridSize();
		
		// GL initialize
		glLineWidth(3.0f);
		glPointSize(Display.getWidth() / mapper.getGrid().getGridSize()+0.51f);
		glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		
		
		// initialize pixel grid
		tileVertices = new float[size * size * 2];
		tileColors = new float[size * size * 3];

		int i = -1;
		int c = -1;
		float offset = -1.0f + 1.0f / size;
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				tileVertices[++i] = x / ((float) size / 2.0f) + offset; // X
				tileVertices[++i] = y / ((float) size / 2.0f) + offset; // Y

				tileColors[++c] = 0.5f; // red
				tileColors[++c] = 0.5f; // green
				tileColors[++c] = 0.5f; // blue
			}
		}
	}

	/**
	 * Update the colors of the MapGrid. Green = clear area,
	 * Red = area with located obstacle.
	 * 
	 * And generate the new LineMap view.
	 * 
	 * @throws InterruptedException
	 */
	private void rebuildMap() throws InterruptedException {
		semaphore.acquire();
		mapChanged = false;
		semaphore.release();

		MapGrid grid = mapper.getGrid();
		final int g_size = grid.getGridSize();
		final float gstsh = g_size * grid.getTileSize() / 2.0f; 
		
		//Rebuild tile colors
		int c = -1;
		float strength;
		for (int x = 0; x < g_size; x++) {
			for (int y = 0; y < g_size; y++) {
				if ((strength = (float)grid.get(x, y)) > 0) {
					tileColors[++c] = 4.0f * strength / 4.0f;        // red
					tileColors[++c] = 1.0f -  1.0f *strength/ 3.0f; // green
					tileColors[++c] = 0.0f;                         // blue
				} else {
					tileColors[++c] = 1.0f; // red
					tileColors[++c] = 1.0f; // green
					tileColors[++c] = 1.0f; // blue
				}
			}
		}
		
		//Rebuild Line vertices
		Line[] lines;
		if((lines = mapper.getLineMap().getLines()) != null){
    		lineVertices = new float[lines.length * 4];
    		int j = -1;
    		for (int i = 0; i < lines.length; i++){
    			lineVertices[++j] = lines[i].x1 / gstsh;
    			lineVertices[++j] = lines[i].y1 / gstsh;
    			lineVertices[++j] = lines[i].x2 / gstsh;
    			lineVertices[++j] = lines[i].y2 / gstsh;
    		}
		}
	}

	private void update(){
		//Map update if it was changed
		if (mapChanged) {
			try {
				rebuildMap();
			} catch (InterruptedException e) {
				// then use the old one...
			}
		}
		
		input();
		
	}
	
	/**
	 * Draw all data on OpenGL canvas.
	 */
	private void draw() {
		glClear(GL_COLOR_BUFFER_BIT);
			
		int size = tileVertices.length - 1;
		int i = -1;
		int c = -1;

		if(drawTile){
			glBegin(GL_POINTS);                                                //Begin to draw Points
			while (i < size) {
				glColor3f(tileColors[++c], tileColors[++c], tileColors[++c]);  //set pixel color
				glVertex2f(tileVertices[++i], tileVertices[++i]);              //make a point
			}
			glEnd();                                                           //End with tile draw
		}
		
		if(lineVertices != null){
    		size = lineVertices.length -1;
    		i = -1;
    		
    		if(drawLine){                                                          
    			glColor3f(0.0f, 0.5f, 1.0f);
    			glBegin(GL_LINES);                                                 //Begin to draw Points
    			while(i < size){
    				glVertex2f(lineVertices[++i], lineVertices[++i]);              //Set new LinePoint
    			}
    			glEnd();                                                           //End with Line draw
    		}
    		                                                     
		}
		Display.update();                                                          // Bring it to the screen.
	}

	/**
	 * Handle user input
	 */
	private void input(){

		boolean l_Button = Mouse.isButtonDown(0); //left
		boolean r_Button = Mouse.isButtonDown(1); //right
		
		if(l_Button){	// create a Obstacle
			int view_offset = Display.getWidth() / mapper.getGrid().getGridSize();
			
			int X = Mouse.getX() / view_offset;
			int Y = Mouse.getY() / view_offset;
			
			mapper.addObstacle(X, Y);
		}
		
		if(r_Button){	// remove a Obstacle
			int view_offset = Display.getWidth() / mapper.getGrid().getGridSize();
			
			int X = Mouse.getX() / view_offset;
			int Y = Mouse.getY() / view_offset;
			
			mapper.removeObstacle(X, Y);
		}
		
		//Draw Lines? 
		if(Keyboard.isKeyDown(Keyboard.KEY_F1))
			drawLine = false;
		else
			drawLine = true;
		
		//Draw Tiles?
		if(Keyboard.isKeyDown(Keyboard.KEY_F2))
			drawTile = false;
		else
			drawTile = true;
	}
	
	/**
	 * Register the Mapper to be observed.
	 * 
	 * @param mapper
	 */
	public void registerMapper(Mapper mapper) {
		this.mapper = mapper;
	}

	/**
	 * Tells view that the map has changed (Thread safe)
	 */
	public void mapChanged() {

		try {
			semaphore.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		mapChanged = true;
		semaphore.release();
	}
	
}
