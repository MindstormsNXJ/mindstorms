package de.fh.zwickau.mindstorms.server.view.graphic.shader;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.lwjgl.opengl.*;

public class Shader {
    
    private int program;
    
    public Shader(String vertFile, String fragFile) throws Exception{
        int vert, frag;
        vert = loadShader(vertFile, ARBVertexShader.GL_VERTEX_SHADER_ARB);
        frag = loadShader(fragFile, ARBFragmentShader.GL_FRAGMENT_SHADER_ARB);
        
        
        if(vert > 0 || frag > 0){ // are that shader valid?
            program = ARBShaderObjects.glCreateProgramObjectARB();
            
            //GL20.glBindAttribLocation(program, 0, "in_Position");
            //GL20.glBindAttribLocation(program, 1, "in_Normal");
            
            ARBShaderObjects.glAttachObjectARB(program, vert);
            ARBShaderObjects.glAttachObjectARB(program, frag);
            
            ARBShaderObjects.glLinkProgramARB(program);
            checkProgramStatus();
            
            
            
            ARBShaderObjects.glValidateProgramARB(program);
            checkProgramStatus();

        }
        
    }
    
    private int loadShader(String fileName, int shaderType) throws Exception{
        int shader = ARBShaderObjects.glCreateShaderObjectARB(shaderType);
        ARBShaderObjects.glShaderSourceARB(shader, readFileAsString(fileName));
        ARBShaderObjects.glCompileShaderARB(shader);
        
        if(!checkShaderCompileStatus(shader)){
            return 0;
        }
        
        return shader;
    }
    
    /**
     * Check Shader Compile status from GPU
     * and print compile log if anything wrong.
     * @param shader to check
     * @return true if shader is successfully compiled
     */
    private boolean checkShaderCompileStatus(int shader){
        if (ARBShaderObjects.glGetObjectParameteriARB(shader, ARBShaderObjects.GL_OBJECT_COMPILE_STATUS_ARB) == GL11.GL_FALSE){
            getErrorLog(shader);
            return false;
        }
        return true;
    }
    
    /**
     * Check Program status from GPU
     * and print error log if anything wrong.
     */
    private void checkProgramStatus(){
        if (ARBShaderObjects.glGetObjectParameteriARB(program, ARBShaderObjects.GL_OBJECT_LINK_STATUS_ARB) == GL11.GL_FALSE) {
            getErrorLog(program);
            program = 0;
        }
    }
    
    private void getErrorLog(int handle){
        System.err.println( ARBShaderObjects.glGetInfoLogARB(handle,
                            ARBShaderObjects.glGetObjectParameteriARB(handle,
                            ARBShaderObjects.GL_OBJECT_INFO_LOG_LENGTH_ARB)));
    }
    
    private String readFileAsString(String filename) throws Exception {
        StringBuilder source = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename),"UTF-8"));
            
        String line;
        // Read file to end
        while((line = reader.readLine()) != null){
            source.append(line).append('\n');
        }
        
        reader.close();
        
        return source.toString();
    }

    public int getHandle(){
        return program;
    }
}

