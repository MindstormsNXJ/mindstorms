package de.fh.zwickau.mindstorms.server.view;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;

public class View extends Thread{
	
	@Override
	public void run(){
		create();
	}
	
	public void create() {
		try {
			Display.setDisplayMode(new DisplayMode(800,600));
			Display.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		// init OpenGL here
		
		while (!Display.isCloseRequested()) {
			
			// render OpenGL here
			glColor3f(1.0f, 0.125f, 0.0f);
			glBegin(GL_TRIANGLES);
			glVertex2f(0, 1);
			glColor3f(1.0f, 0.5f, 0.0f);
			glVertex2f(-1, -1);
			glVertex2f(1, -1);
			glEnd();
			
			Display.update();
		}
		
		Display.destroy();
	}
}
