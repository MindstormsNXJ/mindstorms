package de.fh.zwickau.mindstorms.server.view.graphic;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL20.glDrawBuffers;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.GL_DEPTH_ATTACHMENT;
import static org.lwjgl.opengl.GL30.GL_DEPTH_COMPONENT32F;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_COMPLETE;
import static org.lwjgl.opengl.GL30.GL_RENDERBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;
import static org.lwjgl.opengl.GL30.glBindRenderbuffer;
import static org.lwjgl.opengl.GL30.glCheckFramebufferStatus;
import static org.lwjgl.opengl.GL30.glFramebufferRenderbuffer;
import static org.lwjgl.opengl.GL30.glFramebufferTexture2D;
import static org.lwjgl.opengl.GL30.glGenFramebuffers;
import static org.lwjgl.opengl.GL30.glGenRenderbuffers;
import static org.lwjgl.opengl.GL30.glRenderbufferStorage;

import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

import de.fh.zwickau.mindstorms.server.view.graphic.shape.Rectangle;

/**
 * A RenderTarget can draw pixels
 * to a Texture.
 * 
 * @author Andre Furchner
 *
 */
public class RenderTarget
{
    private Rectangle quad;
    private int frameBuffer, depthBuffer;
    private Texture[] textureBuffers;


    /**
     * New RenderTarget with Texture to draw on it.
     * 
     * @param textureBuffer Texture to draw on it.
     */
    public RenderTarget(Texture textureBuffer){
    	textureBuffers = new Texture[1];
    	textureBuffers[1] = textureBuffer;
    	
    	initialize();
    	setupFrameBuffer();
    }

    /**
     * New RenderTarget with multiple Textures
     * to draw on it.
     * @param textureBuffers Textures to draw on it.
     */
    public RenderTarget(Texture[] textureBuffers)
    {
        this.textureBuffers = textureBuffers;

        initialize();
        setupFrameBuffer();
    }

    /**
     * Generate Memory Objects on GPU
     */
    private void initialize()
    {
    	
    	depthBuffer = glGenRenderbuffers();
    	frameBuffer = glGenFramebuffers();

        quad = new Rectangle();

    }

    /**
     * Define RenderTarget size and pixel formats on GPU
     * and bind textures to outputs. 
     */
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

    /**
     *  Activate RenderTarget all that now will be drawn
     *  is going into the TextureBuffers.
     */
    public void Bind()
    {
        glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer);
    }

    /**
     * Clear all TextureBuffers
     */
    public void Clear()
    {
        Bind();
        
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    /**
     * Binding and Activate all used FrameBuffer Textures.
     */
    private void bindTextures()
    {
        for(int i = 0; i < textureBuffers.length; i++) {
            textureBuffers[i].Bind(i);
        } 
    }

    /**
     * Unbind all Textures
     */
    private void unbindTextures()
    {
        for(int i = 0; i < textureBuffers.length; i++) {
            textureBuffers[i].Unbind();
        }
    }

    /**
     * Draw the RenterTarget as Rectangle
     * direct to the user screen.
     */
    public void DrawToScreen()
    {
        glViewport(0, 0, textureBuffers[0].getWidth(), textureBuffers[0].getHeight());
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        Draw();
    }

    /**
     * Draw RenderTarget.
     */
    public void Draw()
    {
        bindTextures();
        quad.Draw();
        unbindTextures();
    }
}
