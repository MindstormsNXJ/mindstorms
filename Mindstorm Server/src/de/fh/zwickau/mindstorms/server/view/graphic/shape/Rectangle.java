package de.fh.zwickau.mindstorms.server.view.graphic.shape;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

/// <summary>
/// A simple rectangle for UI building
/// </summary>
public class Rectangle
{
	private static boolean BufferIsUploaded = false;
    private static int vao;
    private static IntBuffer vbos = BufferUtils.createIntBuffer(2);
    
    private static FloatBuffer vertices = BufferUtils.createFloatBuffer(12);
    private static float[] verticesf = {
         1.0f, 1.0f, -1.0f,
         -1.0f, 1.0f, -1.0f,
         -1.0f, -1.0f, -1.0f,
         1.0f, -1.0f, -1.0f
    };

    private static FloatBuffer uv = BufferUtils.createFloatBuffer(8);
    private static float[] uvf = {
         1.0f, 0.0f,
         0.0f, 0.0f,
         0.0f, 1.0f,
         1.0f, 1.0f
    };

    public Rectangle() 
    {

        if(!BufferIsUploaded){
        	vertices.put(verticesf);
        	uv.put(uvf);
        	
        	vertices.rewind();
        	uv.rewind();
            UploadToGPU();
            
        }
    }

    public void Draw()
    {
        glBindVertexArray(vao);  
        glDrawArrays(GL_QUADS, 0, 4);
        glBindVertexArray(0);
    }

    public void UploadToGPU()
    {
        glGenBuffers(vbos);
        vao = glGenVertexArrays();

        glBindVertexArray(vao);

        glBindBuffer(GL_ARRAY_BUFFER, vbos.get(0));
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 12, 0);
        
        glBindBuffer(GL_ARRAY_BUFFER, vbos.get(1));
        glBufferData(GL_ARRAY_BUFFER, uv, GL_STATIC_DRAW);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 8, 0);
        
        glBindVertexArray(0);
        
        BufferIsUploaded = true;
    }

    public void DeleteOnGPU()
    {
        if (BufferIsUploaded) {
            glDeleteBuffers(vbos);
        }
    }
}


