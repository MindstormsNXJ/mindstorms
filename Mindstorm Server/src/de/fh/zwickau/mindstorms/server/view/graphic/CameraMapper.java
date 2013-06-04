package de.fh.zwickau.mindstorms.server.view.graphic;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;

import de.fh.zwickau.mindstorms.server.navigation.mapping.camera.Camera;
import de.fh.zwickau.mindstorms.server.view.graphic.shader.ShaderManager;
import de.fh.zwickau.mindstorms.server.view.graphic.shape.Rectangle;

public class CameraMapper {
    private Camera camera;
    private Texture tex_camera;
    
    public CameraMapper(){

    }
    
    public void setCamera(Camera camera){
        this.camera = camera;
    }
    
    public void update(){
        
        tex_camera = new Texture(camera.getImageWidth(), camera.getImageHeight(), "tex");
        tex_camera.SetupTextures(camera.getByteBuffer());
        //tex_camera.UploadPixelsToGPU(camera.getByteBuffer());
    }
    
    public void draw(){
        float[] ball = camera.getBall();
        float[] goal = camera.getGoal();
        float[] obst = camera.getObstacle();

        glClear(GL_COLOR_BUFFER_BIT);
        
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
    
    private void readFromFrameBuffer(){
    	ByteBuffer buffer = BufferUtils.createByteBuffer(512*512*3);
    	glReadPixels(0, 0, 512, 512, GL_RGB, GL_BYTE, buffer);
    }
    
}
