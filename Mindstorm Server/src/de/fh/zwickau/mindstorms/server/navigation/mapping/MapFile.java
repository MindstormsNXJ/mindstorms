package de.fh.zwickau.mindstorms.server.navigation.mapping;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Class to save and load the current GridMap in a .bm-file
 * 
 * @author Patrick Rosenkranz, Andre Furchner
 *
 */
public class MapFile {
    static private String fileName = "mapfile.bm"; 
    
    /**
     * Load the current grid in mapfile.bm
     * @param mapGrid
     */
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
            System.out.println("Could not find the MapFile.bm , A new one will be created.");
        }
    }
    
    /**
     * Saves the current MapGrid
     * @param grid 
     */
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
