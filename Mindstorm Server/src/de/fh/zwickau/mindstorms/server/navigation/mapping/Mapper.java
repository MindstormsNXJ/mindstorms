package de.fh.zwickau.mindstorms.server.navigation.mapping;


import de.fh.zwickau.mindstorms.server.view.View;
import lejos.robotics.mapping.LineMap;
import lejos.robotics.navigation.Pose;

public class Mapper {
	private View observer;
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
		observer.mapChanged();
	}
	
	/**
	 * generate a new LineMap
	 */
	private void buildLineMap(){
		
	}
	
	public MapGrid getGrid(){
		return mapGrid;
	}
	
	public LineMap getLineMap(){
		return lineMap;
	}
	
	public void setObserverView(View observer){
		this.observer = observer;
	}
	
	
}
