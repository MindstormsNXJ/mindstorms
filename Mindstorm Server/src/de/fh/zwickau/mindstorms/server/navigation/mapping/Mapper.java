package de.fh.zwickau.mindstorms.server.navigation.mapping;
import de.fh.zwickau.mindstorms.server.view.View;

import lejos.robotics.mapping.LineMap;
import lejos.robotics.navigation.Pose;

public class Mapper {
	private View observer;
	private MapGrid mapGrid;
	private LineMap lineMap;
	private int tileSize;

	public Mapper(int tileSize) {
		this.tileSize = tileSize;
		mapGrid = new MapGrid();
		lineMap = new LineMap();
	}

	public void addPose(Pose pose/* , ID id */) {

	}

	/**
	 * Add an Obstacle.
	 * The Mapper will check if its a other robot or a goal.
	 * @param pose robot pose 
	 * @param dist located obstacle distance
	 */	
	public void addObstacle(Pose pose, int dist/*, ID id*/) {
		
		//calculate the right coordinates
		float[] obstacle_position = Converter.calculateObstaclePosition(pose, dist);
		
		//TODO: check if its a other robot or a goal
		boolean isNotRobotOrGoal = true; // =)
		
		if(isNotRobotOrGoal)
			addObstacle(obstacle_position);
	}

	/**
	 * Add an Obstacle at the world position without
	 * check if its a other robot or a goal.
	 * @param pos position
	 */
	private void addObstacle(float[] pos){
		addObstacle((int)(pos[0]/(float)tileSize + 0.5f) + 32,(int)(pos[1]/(float)tileSize + 0.5f) + 32);
	}
	
	/**
	 * Add an Obstacle at the local position without
	 * check if its a other robot or a goal.
	 * @param x position
	 * @param y position
	 */
	public void addObstacle(int x, int y){
		mapGrid.set(x, y);
		buildLineMap();
		observer.mapChanged();
	}
	
	public void removeObstacle(int x, int y){
		mapGrid.clear(x, y);
		buildLineMap();
		observer.mapChanged();
	}

	/**
	 * generate a new LineMap
	 */
	private void buildLineMap() {
		
	}

	public MapGrid getGrid() {
		return mapGrid;
	}

	public LineMap getLineMap() {
		return lineMap;
	}

	public void setObserverView(View observer) {
		this.observer = observer;
	}

}
