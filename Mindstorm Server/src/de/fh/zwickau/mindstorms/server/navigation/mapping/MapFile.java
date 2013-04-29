package de.fh.zwickau.mindstorms.server.navigation.mapping;

import java.io.*;

public class MapFile {
    static private String fileName = "mapfile.bm"; 
    
    public void load(MapGrid mapGrid){
        File file = new File(fileName);
        byte[][] grid = mapGrid.getByteGrid();
        
        try{
            FileInputStream fis = new FileInputStream(file);
            
            for(int i = 0; i < grid.length; i++){
                fis.read(grid[i]);
            }
            fis.close();
            
        } catch (IOException e){
            e.printStackTrace();
        }
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
