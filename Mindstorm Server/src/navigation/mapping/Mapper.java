package navigation.mapping;

import lejos.robotics.navigation.Pose;

public class Mapper {
	private MapGrid mapGrid;
	
	public Mapper(){
		mapGrid = new MapGrid();
	}
	
	public void addPose(Pose pose/*, ID id*/) {
		  
	}

	public void addObstacle(Pose pose, int dist/*, ID id*/) {
		//Todo: first check if its a other robot or a goal
		
		
		//Todo: calculate the right coordinates
		int ObstacleX = (int)pose.getX();
		int ObstacleY = (int)pose.getY();
		
		mapGrid.addObstacle(ObstacleX, ObstacleY);
	}
	
}
