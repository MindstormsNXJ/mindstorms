package de.fh.zwickau.mindstorms.server.view.graphic.shader;

import java.io.IOException;
import java.util.HashMap;
import org.lwjgl.opengl.GL20;

/**
 * This Class provides an easy use and
 * managing process with GLSL Shader
 * @author Andre Furchner
 *
 */
public class ShaderManager {
    static private HashMap<String, Integer> shader;
    static private int activeShader;
    
    public static void Initialize(){
        shader = new HashMap<String,Integer>();
        
        // Load all needed Shader
        load("compute");
    }
    
    /**
     * Load a single shader with vertex and fragment files
     * @param FileName and future name of the shader
     */
    private static void load(String name){
        String vertPath = ("content\\shader\\" + name + ".vert").toString();
        String fragPath = ("content\\shader\\" + name + ".frag").toString();
        Shader shaderProg = null;
        
        try{ // to load shader to GPU
            try { // to load files on Windows
                shaderProg = new Shader(vertPath, fragPath);
            } catch (java.io.FileNotFoundException e) {
                vertPath = vertPath.replace('\\', '/');
                fragPath = fragPath.replace('\\', '/');
                try {  // to load files on Linux
                    shaderProg = new Shader(vertPath, fragPath);
                } catch (IOException ioe) {
                    System.err.println("Unable to load the Shader on Linux: " + name
                            + "!");
                    ioe.printStackTrace();
                    return;
                }
    
            } catch (IOException e) {
                System.err.println("Unable to load the Shader on Windows: " + name
                        + "!");
                e.printStackTrace();
                return;
            }
        } catch (Exception e){
            // there was an error on the GPU =(
            e.printStackTrace();
            return;
        }
        
        // if we are here then we have a fully functional shader on the GPU =)
        shader.put(name, shaderProg.getHandle());
    }
    
    public static void useShader(String shaderName){
        activeShader = shader.get(shaderName);
        GL20.glUseProgram(activeShader);
    }
    
    public static int getAttribLocation(String name){
        return GL20.glGetAttribLocation(activeShader, name);
    }
    
    public static int getUniformLocation(String name){
        return GL20.glGetUniformLocation(activeShader, name);
    }
}

