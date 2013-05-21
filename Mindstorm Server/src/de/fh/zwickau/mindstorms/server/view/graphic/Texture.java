package de.fh.zwickau.mindstorms.server.view.graphic;

import java.nio.ByteBuffer;

import de.fh.zwickau.mindstorms.server.view.graphic.shader.ShaderManager;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL20.*;

class Texture
{
    private String nameInShader;
    private int width, height;
    private int handle;

    private int bindingPoint;
    private int internalFormat;
    private int format;
    
    /**
     * Creating an empty new Texture on GPU, pixel data can be uploaded later with
     * the uploadPixelsToGPU method. The name must be the same as the name in a shader.
     * 
     * @param width Texture Width
     * @param height Texture Height
     * @param nameInShader The exact Name in the Shader
     */
    public Texture(int width, int height, String nameInShader)
    {
        this.nameInShader = nameInShader;
        this.width = width;
        this.height = height;

        internalFormat = GL_RGB;
        format = GL_RGB;

        handle = glGenTextures();
    }
    
    /**
     * Creating the buffer for the GPU and setting PixelFormats
     */
    public void SetupTextures()
    {
        glBindTexture(GL_TEXTURE_2D, handle);
        setupTextureParameter();
        glTexImage2D(GL_TEXTURE_2D, 0, internalFormat, width, height, 0, format, GL_UNSIGNED_BYTE, 0);
        glBindTexture(GL_TEXTURE_2D, 0);
    }
    
    /**
     * Setting some Texture Parameters
     */
    private void setupTextureParameter()
    {
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    }

    
    /**
     * Uploading Pixel-Data to GPU
     * @param pixel
     */
    public void UploadPixelsToGPU(ByteBuffer pixel)
    {
        // TODO: This area has not been tested
        // no one knows exactly what can happen =)
        glBindTexture(GL_TEXTURE_2D, handle);
        glTexImage2D(GL_TEXTURE_2D, 0, internalFormat, width, height, 0, format, GL_UNSIGNED_BYTE, pixel);
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    /**
     * Binding the current Texture to the global binding points
     * @param point
     */
    public void Bind(int point)
    {
        bindingPoint = GL_TEXTURE0 + point;
        glActiveTexture(bindingPoint);
        glBindTexture(GL_TEXTURE_2D, handle);
        glUniform1i(ShaderManager.getUniformLocation(nameInShader), point);
    }

    public void Unbind()
    {
        glActiveTexture(bindingPoint);
        glBindTexture(GL_TEXTURE_2D, 0);
    }
    
    public String getName(){
    	return nameInShader;
    }
    
    public int getHandle(){
    	return handle;
    }
    
    public int getWidth(){
    	return width;
    }
    
    public int getHeight(){
    	return height;
    }
}

