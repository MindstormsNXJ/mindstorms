package de.fh.zwickau.mindstorms.server.view.graphic;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

import de.fh.zwickau.mindstorms.server.view.graphic.shape.Rectangle;


public class RenderTarget
{
    private Rectangle quad;
    private int frameBuffer, depthBuffer;
    private Texture[] textureBuffers;


    public RenderTarget(Texture textureBuffer){
    	textureBuffers = new Texture[1];
    	textureBuffers[1] = textureBuffer;
    	
    	Initialize();
    	setupFrameBuffer();
    }

    public RenderTarget(Texture[] textureBuffers)
    {
        this.textureBuffers = textureBuffers;

        Initialize();
        setupFrameBuffer();
    }

    private void Initialize()
    {
    	
    	depthBuffer = glGenRenderbuffers();
    	frameBuffer = glGenFramebuffers();

        quad = new Rectangle();

    }

    private void setupFrameBuffer()
    {
        // setup Depth Buffer
        glBindRenderbuffer(GL_RENDERBUFFER, depthBuffer);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT32F, textureBuffers[0].getWidth(), textureBuffers[0].getHeight());

        // setup FrameBuffer
        glBindFramebuffer(GL_RENDERBUFFER, frameBuffer);
        IntBuffer bufs = BufferUtils.createIntBuffer(textureBuffers.length);
        
        for(int i = 0; i < textureBuffers.length; i++) {

        	
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0 + i, GL_TEXTURE_2D, textureBuffers[i].getHandle(), 0);
            bufs.put(i, (GL_COLOR_ATTACHMENT0 + i));
        }

        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, depthBuffer);
        glDrawBuffers(bufs);

        
        if(glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            System.err.println("Could not generate RenderTarget[" + textureBuffers[0].getWidth() + "x"
                                    + textureBuffers[0].getHeight() + "] for " + textureBuffers[0].getName());
        }

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    /// <summary>
    /// Activate RenderTarget all suff that you
    /// draw now is burned in this RenderTarget
    /// </summary>
    public void Bind()
    {
        glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer);
    }

    /// <summary>
    /// Clearing Buffers
    /// </summary>
    public void Clear()
    {
        Bind();
        
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    /// <summary>
    /// Binding and Activate all used FrameBuffer Textures
    /// </summary>
    private void bindTextures()
    {
        for(int i = 0; i < textureBuffers.length; i++) {
            textureBuffers[i].Bind(i);
        } 
    }

    /// <summary>
    /// Unbind all Textures
    /// </summary>
    private void unbindTextures()
    {
        for(int i = 0; i < textureBuffers.length; i++) {
            textureBuffers[i].Unbind();
        }
    }

    /// <summary>
    /// Draw the RenderTarget Quad
    /// </summary>
    public void DrawToScreen()
    {
        glViewport(0, 0, textureBuffers[0].getWidth(), textureBuffers[0].getHeight());
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        Draw();
    }

    public void Draw()
    {
        bindTextures();
        quad.Draw();
        unbindTextures();
    }
}
