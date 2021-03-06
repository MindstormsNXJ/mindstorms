package de.fh.zwickau.mindstorms.server.view.graphic;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;
import static org.lwjgl.opengl.GL11.*;


import java.awt.Canvas;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import lejos.geom.Line;
import lejos.geom.Point;
import lejos.robotics.navigation.Pose;
import lejos.robotics.pathfinding.Path;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import de.fh.zwickau.mindstorms.server.navigation.TargetManager;
import de.fh.zwickau.mindstorms.server.navigation.mapping.MapGrid;
import de.fh.zwickau.mindstorms.server.navigation.mapping.Mapper;
import de.fh.zwickau.mindstorms.server.navigation.mapping.camera.Camera;
import de.fh.zwickau.mindstorms.server.view.graphic.shader.ShaderManager;

/**
 * OpenGL View for mapping data the graphics will be
 * drawn to the parent canvas.
 * 
 * This class is also used to start computing for
 * obstacle detection on GPU when the camera has a
 * new Image.
 * 
 * @author Andre Furchner
 *
 */

public class GraphicCanvas extends Thread {
    
    private Canvas parent;				// Canvas where OpenGL will be drawn 
    private Mapper mapper;				// For map-data that will be drawn
    private CameraMapper cameraMapper;	// Compute obstacles from Image with GLSL
    private TargetManager targetM;		// Have Targets that will be drawn
    private Semaphore semaphore;		// For other Threads to sync
    
    private boolean mapChanged;			// true if map was changed
    private boolean targetChanged;		// true if targets was changed
    private boolean cameraChanged;		// true if camera has new Image
    
    // Draw Items
    private float[] tileVertices;		// tile vertex-data 
    private float[] tileColors;			// tile color data
    private float[] lineVertices;		// line vertex-data
    private float[] targetVertices;		// target position data

    //	Draw options
    boolean drawLine = true;
    boolean drawTile = true;
    
    
    public GraphicCanvas(Canvas parent) {
        this.parent = parent;
        this.mapChanged = new Boolean(true);
        this.targetChanged = new Boolean(true);
        this.cameraChanged = new Boolean(true);
        this.semaphore = new Semaphore(1);
        this.cameraMapper = new CameraMapper();
    }

    @Override
    public void run() {
        createWindow();
        initialize();

        while (!Display.isCloseRequested()) {
            update();
            
            cameraMapper.compute();
            drawMapOverview();
        }

        Display.destroy();
        System.exit(0);
    }

    /**
     * Create the View Window
     */
    private void createWindow() {
        try {
            Display.setParent(parent);   
            Display.create();
        } catch (LWJGLException e) {
            e.printStackTrace();
            System.err.println("Failed to create an OpenGL Window :(");
        }
    }

    /**
     * Initialize some OpenGL parameters and allocate memory
     * for vertex and color data.
     */
    private void initialize() {
        ShaderManager.Initialize();
        
        final int size = mapper.getGrid().getGridSize();
        
        // GL initialize
        glPointSize(Display.getWidth() / mapper.getGrid().getGridSize() - 0.51f);
        glClearColor(0.75f, 0.75f, 0.75f, 0.75f);
        
        
        // initialize pixel grid
        tileVertices = new float[size * size * 2];
        tileColors = new float[size * size * 3];
        float offset = -1.0f + 1.0f / size;
        int i = -1;
        int c = -1;
        
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                tileVertices[++i] = x / (size / 2.0f) + offset; // X
                tileVertices[++i] = y / (size / 2.0f) + offset; // Y

                tileColors[++c] = 0.5f; // red
                tileColors[++c] = 0.5f; // green
                tileColors[++c] = 0.5f; // blue
            }
        }
    }

    /**
     * Update data if map, targets or camera
     * was changed and check if user have started
     * a event.
     */
    private void update(){
    	
        // Map update if it was changed
        if (mapChanged) {
            rebuildMap();
            try {
                semaphore.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mapChanged = false;
            semaphore.release();
        }
        
        // update targets if it was changed
        if (targetChanged) {
            rebuildTargets();
            try {
                semaphore.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            targetChanged = false;
            semaphore.release();
        }
        
        // compute new obstacles if the camera has a new Image.
        if (cameraChanged) {
        	cameraMapper.update();
            try {
                semaphore.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            cameraChanged = false;
            semaphore.release();
        }
        
        input();    
    }
    
    /**
     * Update the colors of the MapGrid. Green = clear area,
     * Red = area with located obstacle.
     * 
     * And generate the new LineMap view.
     */
    private void rebuildMap() {
    
        MapGrid grid = mapper.getGrid();
        final int g_size = grid.getGridSize();
        final float gstsh = g_size * grid.getTileSize() / 2.0f; 
        
        //Rebuild tile colors
        int c = -1;
        float strength;
        for (int x = 0; x < g_size; x++) {
            for (int y = 0; y < g_size; y++) {
                if ((strength = grid.get(x, y)) > 0) {
                    tileColors[++c] = 4.0f * strength / 4.0f;                  //red
                    tileColors[++c] = 1.0f -  1.0f *strength/ 3.0f;            //green
                    tileColors[++c] = 0.0f;                                    //blue
                } else {
                    tileColors[++c] = 1.0f;                                    //red
                    tileColors[++c] = 1.0f;                                    //green
                    tileColors[++c] = 1.0f;                                    //blue
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
    
    private void rebuildTargets(){
        MapGrid grid = mapper.getGrid();
        final float gstsh = grid.getGridSize() * grid.getTileSize() / 2.0f;
        
        Path points; 
        if((points = targetM.getPath("Picker")) != null){
            targetVertices = new float[points.size() * 2 + 2];
            targetVertices[0] = targetVertices[1] = 0.0f;                      //this is the start point [0,0]
            int j = 1;
            for (int i = 0; i < points.size(); i++){
                targetVertices[++j] = points.get(i).x / gstsh;
                targetVertices[++j] = points.get(i).y / gstsh;
            }
        }
    }
    
    
    /**
     * Draw all data on OpenGL canvas.
     */
    private void drawMapOverview() {
        
        glViewport(0, 0, 512, 512);
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
            glLineWidth(2.0f);
            size = lineVertices.length -1;
            i = -1;
            
            if(drawLine){                                                          
                glColor3f(0.0f, 0.5f, 1.0f);
                glBegin(GL_LINES);                                             //Begin to draw Points
                while(i < size){
                    glVertex2f(lineVertices[++i], lineVertices[++i]);          //Set new LinePoint
                }
                glEnd();                                                       //End with Line draw
            }                                                         
        }
        
        // draw the center lines
        glLineWidth(1.0f);
        glColor3f(0.8f, 0.4f, 0.3f);
        glBegin(GL_LINES);
        glVertex2f(-1.0f, 0.0f); glVertex2f(1.0f, 0.0f);
        glVertex2f(0.0f, -1.0f); glVertex2f(0.0f, 1.0f);
        glEnd();
        
        drawTargets();
        drawRobots();
        drawObjects();
        
        Display.update();                                                      	// Bring it to the screen.
    }
    
    private void drawTargets(){
        int size = targetVertices.length - 1;
        int i = -1;

        glLineWidth(1.51f);
        glColor3f(1.0f, 0.75f, 0.0f);
        glBegin(GL_LINE_STRIP);                                                	// Begin to draw Lines
        while (i < size) {
            glVertex2f(targetVertices[++i], targetVertices[++i]);              	// make a point
        }
        glEnd();                                                               	// End with target draw
    }
    
    private void drawRobots(){
        glEnable(GL_POINT_SMOOTH);
        glLineWidth(1.51f);
        
        String[] r_names = mapper.getTracer().getTracedNames();
        ArrayList<Pose> poses;
        
        float offset = mapper.getGrid().getGridSize() / 2 * mapper.getGrid().getTileSize();
        
        for(int i = 0; i < r_names.length; i++){
            // Define different colors for an other robot
            switch (i) {
                case 0: glColor3f(0.3f, 0.5f, 0.8f); break;
                case 1: glColor3f(0.8f, 0.4f, 0.6f); break;
                case 2: glColor3f(0.2f, 0.8f, 0.2f); break;
                
                default: glColor3f(0.8f, 0.0f, 0.0f); break;
            }
            
            poses = mapper.getTracer().getTracedPoseList(r_names[i]);
            // Draw the traced line
            
            glBegin(GL_LINE_STRIP);
            for (Pose pose : poses) {
                glVertex2f(pose.getX() / offset, pose.getY() / offset);
            }
            glEnd();
            
            //Draw a Point for the current robot position and a line for heading
            Pose current_pose = poses.get(poses.size()-1);
            float xp = current_pose.getX() / offset;
            float yp = current_pose.getY() / offset;
            
            glBegin(GL_POINTS);
            glVertex2f(xp, yp);
            glEnd();
            
            // Draw heading vector
            float[] normal = new float[2];  
            normal[0] = (float)(sin(toRadians(current_pose.getHeading())) * 0.05 + xp);
            normal[1] = (float)(cos(toRadians(current_pose.getHeading())) * 0.05 + yp);
            
            glBegin(GL_LINES);
            glVertex2f(xp, yp);
            glVertex2f(normal[0], normal [1]);
            glEnd();
        }
        glDisable(GL_POINT_SMOOTH);
    }

    /**
     * Draw Objects like Ball and Goal
     */
    private void drawObjects(){
        float offset = mapper.getGrid().getGridSize() / 2 * mapper.getGrid().getTileSize();
        
        Point ball = mapper.getBallPosition();
        Point goal = mapper.getGoalPosition();
        
        
        glBegin(GL_POINTS);
        if(ball != null){
            glColor3f(1.0f, 0.25f, 0.0f);
            glVertex2f(ball.x / offset, ball.y / offset);
        }
        if(goal != null){
            glColor3f(0.25f, 1.0f, 0.0f);
            glVertex2f(goal.x / offset, goal.y / offset);
        }
        glEnd();
    }
    
    /**
     * Handle user input
     */
    private void input(){

        boolean l_Button = Mouse.isButtonDown(0); //left
        boolean r_Button = Mouse.isButtonDown(1); //right
        
        if(l_Button){   // create a Obstacle
            int view_offset = Display.getWidth() / mapper.getGrid().getGridSize();
            
            int X = Mouse.getX() / view_offset;
            int Y = Mouse.getY() / view_offset;
            
            mapper.addObstacle(X, Y);
        }
        
        if(r_Button){   // remove a Obstacle
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
    public void setMapper(Mapper mapper) {
        this.mapper = mapper;
    }
    
    /**
     * Register the TargetManager to be observed.
     * 
     * @param tM TargetManager
     */
    public void setTargetManager(TargetManager tM) {
        this.targetM = tM;
    }

    /**
     * Register the Camera to be observed.
     * 
     * @param camera Camera
     */
    public void setCamera(Camera camera){
    	cameraMapper.setCamera(camera); 
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

    /**
     * Tells view that the Target has changed (Thread safe)
     */
    public void targetChanged() {
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        targetChanged = true;
        semaphore.release();
    }
}
