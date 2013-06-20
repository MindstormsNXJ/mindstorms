package de.fh.zwickau.mindstorms.server.view.graphic;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_RED;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glReadPixels;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL20.glUniform4f;
import static org.lwjgl.opengl.GL20.glUseProgram;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

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
    private FloatBuffer computeOutput;											// Output from compute.frag
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
        float[] obst = camera.getObstacle();

        glClear(GL_COLOR_BUFFER_BIT);
        glViewport(0, 0, resolution, resolution);
        
        ShaderManager.useShader("compute");
        glUniform4f(ShaderManager.getUniformLocation("v4_obstacle"), obst[0], obst[1], obst[2], 0.2f);
       
        tex_camera.Bind(0);
        
        Rectangle quad = new Rectangle();
        quad.Draw();
        tex_camera.Unbind();
        
        readFromFrameBuffer();
        
        glUseProgram(0);
    }
    
    /**
     * Read the output from FrameBuffer
     */
    private void readFromFrameBuffer(){
        computeOutput = BufferUtils.createFloatBuffer(resolution*resolution);
    	glReadPixels(0, 0, resolution, resolution, GL_RED, GL_FLOAT, computeOutput);
    	computeOutput.rewind();
    	camera.setComputedBuffer(computeOutput);
    }
    
    public FloatBuffer getComputedOutput(){
        return computeOutput;
    }
}
