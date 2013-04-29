package de.fh.zwickau.mindstorms.server.navigation.mapping;

import java.io.*;

public class MapFile {
    static private String fileName = "mapfile.bm"; 
    
    public MapGrid load(){
        return null;
    }
    
    public void save(byte[][] grid){
        File file = new File(fileName);
        try{
            FileOutputStream fos = new FileOutputStream(file);
            
            for(int x = 0; x < grid[0].length; x++){
                fos.write(grid[x]);
            }
            
            fos.close();
            
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
