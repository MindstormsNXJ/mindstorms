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
	
	private void createWindow() {
		try {
			Display.setDisplayMode(new DisplayMode(800,600));
			Display.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.err.println("Failed to create an OpenGL Window :(");
		}
		
		
	}
	
	private void initialize(){
		
	}
	
	private void rebuildMap() throws InterruptedException{
		semaphore.acquire();
		mapChanged = false;
		semaphore.release();
		
		MapGrid grid = mapper.getGrid();
		final int size = grid.getGridSize();
		
		for(int x = 0; x < size; x++){
			for(int y = 0; y < size; y++){
				
			}
		}
	}
	
	private void draw(){
		glColor3f(1.0f, 0.125f, 0.0f);
		glBegin(GL_TRIANGLES);
		glVertex2f(0, 1);
		glColor3f(1.0f, 0.5f, 0.0f);
		glVertex2f(-1, -1);
		glVertex2f(1, -1);
		glEnd();
		
		// swap buffer
		Display.update();
	}
	
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
