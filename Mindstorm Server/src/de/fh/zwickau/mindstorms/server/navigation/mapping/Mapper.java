package de.fh.zwickau.mindstorms.server.navigation.mapping;

import lejos.robotics.mapping.LineMap;
import lejos.robotics.navigation.Pose;

public class Mapper {
	private MapGrid mapGrid;
	private LineMap lineMap;
	
	public Mapper(){
		mapGrid = new MapGrid();
		lineMap = new LineMap();
	}
	
	public void addPose(Pose pose/*, ID id*/) {
		  
	}
	
	/**
	 * 
	 * @param pose robot pose
	 * @param dist located obstacle distance
	 */
	public void addObstacle(Pose pose, int dist/*, ID id*/) {
		//TODO: first check if its a other robot or a goal
		
		
		//TODO: calculate the right coordinates
		int ObstacleX = (int)pose.getX();
		int ObstacleY = (int)pose.getY();
		
		mapGrid.set(ObstacleX, ObstacleY);
		buildLineMap();
	}
	
	/**
	 * generate a new LineMap
	 */
	private void buildLineMap(){
		
	}
	
	public LineMap getLineMap(){
		return lineMap;
	}
	
}
