package de.fh.zwickau.mindstorms.server.view;

import java.util.concurrent.Semaphore;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import de.fh.zwickau.mindstorms.server.navigation.mapping.MapGrid;
import de.fh.zwickau.mindstorms.server.navigation.mapping.Mapper;
import static org.lwjgl.opengl.GL11.*;

public class View extends Thread{
	
	private Mapper mapper;
	private boolean mapChanged;
	private Semaphore semaphore;
	
	//Draw Items
	private float[] vertices;
	private float[]	colors;
	
	public View(){
		this.mapChanged = true;
		this.semaphore = new Semaphore(1);
	}

	@Override
	public void run(){
		createWindow();
		initialize();
		
		while(!Display.isCloseRequested()){
			if(mapChanged) {
				try { 
					rebuildMap();
				} catch (InterruptedException e) {
					//then use the old one...
				}
			}
			draw();
		}
		
		Display.destroy();
	}
	
	/**
	 * Create the View Window
	 */
	private void createWindow() {
		try {
			Display.setDisplayMode(new DisplayMode(512+1,512+1));
			Display.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.err.println("Failed to create an OpenGL Window :(");
		}
		
		
	}
	
	/**
	 * Initialize some OpenGL parameters and allocate memory
	 */
	private void initialize(){
		final int size = mapper.getGrid().getGridSize();
		
		//GL initialize
		glPointSize(Display.getWidth() / size -1);
		
		//initialize pixel grid
		vertices = new float[size * size * 2];
		colors = new float[size * size * 3];
		
		int i = -1;
		int c = -1;
		float offset = -1.0f + 1.0f / size;
		for(int x = 0; x < size; x++){
			for(int y = 0; y < size; y++){
				vertices[++i] = x/((float)size / 2.0f) + offset; //X
				vertices[++i] = y/((float)size / 2.0f) + offset; //Y
				
				colors[++c] = 0.5f; //red
				colors[++c] = 0.5f; //green
				colors[++c] = 0.5f; //blue
			}
		}
	}
	
	/**
	 * Update the colors of the MapGrid. 
	 * Green = clear area
	 * Red	 = area with located obstacle
	 * @throws InterruptedException
	 */
	private void rebuildMap() throws InterruptedException{
		semaphore.acquire();
		mapChanged = false;
		semaphore.release();
		
		MapGrid grid = mapper.getGrid();
		final int size = grid.getGridSize();
		
		int c = -1;
		for(int x = 0; x < size; x++){
			for(int y = 0; y < size; y++){
				if(grid.get(x, y) > 0){
					colors[++c] = 1.0f;	//red
					colors[++c] = 0.0f;	//green
					colors[++c] = 0.0f; //blue
				} else {
					colors[++c] = 0.0f; //red
					colors[++c] = 1.0f; //green
					colors[++c] = 0.0f; //blue
				}
			}
		}
	}
	
	/**
	 * Draw all data on OpenGL canvas.
	 */
	private void draw(){
		int size = vertices.length -1;
		int i = -1;
		int c = -1;
		
		glBegin(GL_POINTS);										//Begin to draw Points
		while(i < size){
			glColor3f(colors[++c], colors[++c], colors[++c]); 	//set pixel color
			glVertex2f(vertices[++i], vertices[++i]);			//make a point
		}
		glEnd();												//End with draw
		
		Display.update();										//Bring it to the screen.		
	}
	
	/**
	 * Register the Mapper to be observed.
	 * @param mapper
	 */
	public void registerMapper(Mapper mapper){
		this.mapper = mapper;
	}
	
	/**
	 * Tells view that the map has changed (Thread safe)
	 */
	public void mapChanged(){
		
		try {
			semaphore.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		mapChanged = true;
		semaphore.release();
	}
}
