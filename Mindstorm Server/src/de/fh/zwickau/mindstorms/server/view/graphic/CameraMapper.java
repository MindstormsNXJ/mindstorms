package de.fh.zwickau.mindstorms.server.view.graphic;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

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
        glClear(GL_COLOR_BUFFER_BIT);
        
        ShaderManager.useShader("compute");
        glUniform4f(ShaderManager.getUniformLocation("color"),0.5f,1.0f,0.0f,1.0f); // not needed later
        glUniform4f(ShaderManager.getUniformLocation("v4_obstacle"), camera.getObstacle()[0],  camera.getObstacle()[1],  camera.getObstacle()[2], 0.2f);
        glUniform4f(ShaderManager.getUniformLocation("v4_obstacle2"),camera.getObstacle()[0],  camera.getObstacle()[1],  camera.getObstacle()[2], 0.1f);
        glUniform4f(ShaderManager.getUniformLocation("v4_ball"),    camera.getBall()[0],  camera.getBall()[1],  camera.getBall()[2], 0.30f);
        glUniform4f(ShaderManager.getUniformLocation("v4_goal"),    camera.getGoal()[0],  camera.getGoal()[1],  camera.getGoal()[2], 0.30f);
        glUniform4f(ShaderManager.getUniformLocation("v4_robot"),    0.0f, 0.0f, 0.0f, 0.5f);
        
        tex_camera.Bind(0);
        
        Rectangle quad = new Rectangle();
        quad.Draw();
        tex_camera.Unbind();
        Display.update();
    }
    
}
