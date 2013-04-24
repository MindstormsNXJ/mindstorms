package de.fh.zwickau.mindstorms.server.navigation.mapping;

/**
 * DataStructure to store detected obstacles
 * 
 * @author Andre Furchner
 *
 */
public class MapGrid {
	private final int G_SIZE = 64; //GridSize
	private float tileSize;
	private byte[][] grid;
	
	public MapGrid(float tileSize){
		this.tileSize = tileSize;
		grid = new byte[G_SIZE][G_SIZE];
	}
	
	public void set(int x, int y){
		grid[x][y]++;
		if(grid[x][y] > 3)
			grid[x][y] = 3;
	}
	
	public byte get(int x , int y){
		return grid[x][y];
	}
	
	public void clear(int x, int y){
		grid[x][y] = 0;
	}
	
	public int getGridSize(){
		return G_SIZE;
	}
	
	public float getTileSize(){
		return tileSize;
	}
}
