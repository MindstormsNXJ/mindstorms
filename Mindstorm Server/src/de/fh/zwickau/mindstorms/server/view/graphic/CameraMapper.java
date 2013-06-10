package de.fh.zwickau.mindstorms.server.view.graphic;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

import java.nio.ByteBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;

import de.fh.zwickau.mindstorms.server.navigation.mapping.camera.Camera;
import de.fh.zwickau.mindstorms.server.view.graphic.shader.ShaderManager;
import de.fh.zwickau.mindstorms.server.view.graphic.shape.Rectangle;

/**
 * The CameraMapper is able to use the GPU
 * for Obstacle detection the code for this
 * is in the compute.frag file
 * 
 * @author Andre Furchner
 *
 */
public class CameraMapper {
    private Camera camera;														// Main Camera
    private Texture tex_camera;													// Camera Image
    private ByteBuffer computeOutput;											// Output from compute.frag
    private int resolution = 64;												// Output resolution
    
    
    public CameraMapper(){

    }
    
    public void setCamera(Camera camera){
        this.camera = camera;
    }
    
    /**
     * CameraMapper will be load a new Image from Camera
     * and upload this Image as a ByteBuffer to the GPU.
     */
    public void update(){
        
        tex_camera = new Texture(camera.getImageWidth(), camera.getImageHeight(), "tex");
        tex_camera.SetupTextures(camera.getByteBuffer());
    }
    
    /**
     * The Image from Camera will be proceed thru the compute.frag shader on the GPU.
     * The Output is going to the Camera class as a ByteBuffer.
     */
    public void compute(){
        float[] ball = camera.getBall();
        float[] goal = camera.getGoal();
        float[] obst = camera.getObstacle();

        glClear(GL_COLOR_BUFFER_BIT);
        glViewport(0, 0, resolution, resolution);
        
        ShaderManager.useShader("compute");
        glUniform4f(ShaderManager.getUniformLocation("v4_obstacle"), obst[0], obst[1], obst[2], 0.2f);
        glUniform4f(ShaderManager.getUniformLocation("v4_ball"), ball[0], ball[1], ball[2], 0.20f);
        glUniform4f(ShaderManager.getUniformLocation("v4_goal"), goal[0], goal[1], goal[2], 0.20f);
        
        tex_camera.Bind(0);
        
        Rectangle quad = new Rectangle();
        quad.Draw();
        tex_camera.Unbind();
        Display.update();
        
        readFromFrameBuffer();
    }
    
    /**
     * Read the output from FrameBuffer
     */
    private void readFromFrameBuffer(){
        computeOutput = BufferUtils.createByteBuffer(resolution*resolution);
    	glReadPixels(0, 0, resolution, resolution, GL_RED, GL_BYTE, computeOutput);
    	camera.setComputedBuffer(computeOutput);
    }
    
    public ByteBuffer getComputedOutput(){
        return computeOutput;
    }
}
