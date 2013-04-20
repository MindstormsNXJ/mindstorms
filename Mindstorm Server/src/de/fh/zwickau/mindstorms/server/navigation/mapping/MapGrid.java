package de.fh.zwickau.mindstorms.server.navigation.mapping;

public class MapGrid {
	private final int G_SIZE = 128; // GridSize
	private byte[][] grid;
	
	public MapGrid(){
		grid = new byte[G_SIZE][G_SIZE];
	}
	
	public void set(int x, int y){
		grid[x][y]++;
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
}
