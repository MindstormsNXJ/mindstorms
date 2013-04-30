package de.fh.zwickau.mindstorms.server.navigation.mapping;

import de.fh.zwickau.mindstorms.server.view.View;
import lejos.robotics.mapping.LineMap;
import lejos.robotics.navigation.Pose;

/**
 * The Mapper generate a GridMap of obstacles.
 * Obstacles can be added by local or world
 * coordinates.
 * 
 * @author Andre Furchner
 *
 */
public class Mapper {
	private View observer;
	private RobotTracer tracer;
	private MapGrid mapGrid;
	private LineMap lineMap;

	/**
	 * New Instance of Mapper
	 * @param tileSize tile size for grid
	 */
	public Mapper(float tileSize) {
		mapGrid = new MapGrid(tileSize);
		lineMap = new LineMap();
		tracer = new RobotTracer();
		loadMap();
		buildLineMap();
	}

	/**
	 * Add a Pose to trace the position for a robot.
	 * @param pose
	 * @param robotName
	 */
	public void addPose(Pose pose, String robotName) {
		tracer.trace(robotName, pose);
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
		
		if(isNotRobotOrGoal){
			addObstacle(obstacle_position);
		}
	}

	/**
	 * Add an Obstacle at the world position without
	 * check if its a other robot or a goal.
	 * @param pos position
	 */
	private void addObstacle(float[] pos){
		addObstacle((int)(pos[0]/mapGrid.getTileSize() + 0.5f) + mapGrid.getGridSize() / 2,
		            (int)(pos[1]/mapGrid.getTileSize() + 0.5f) + mapGrid.getGridSize() / 2);
	}
	
	/**
	 * Add an Obstacle at the local position without
	 * check if its a other robot or a goal.
	 * @param x position
	 * @param y position
	 */
	public void addObstacle(int x, int y){
		try {
			mapGrid.set(x, y);
			buildLineMap();
			observer.mapChanged();
		} catch (ArrayIndexOutOfBoundsException e){
			//ignore it
		}
	}
	
	/**
	 * Remove a Obstacle at the local position
	 * @param x	position
	 * @param y position
	 */
	public void removeObstacle(int x, int y){
		try {
			mapGrid.clear(x, y);
			buildLineMap();
			observer.mapChanged();
		} catch (ArrayIndexOutOfBoundsException e){
			//ignore it
		}
	}

	/**
	 * generate a new LineMap
	 */
	private void buildLineMap() {
		lineMap = Converter.gridToLineMap(mapGrid);
	}

	public void loadMap(){
	    MapFile file = new MapFile();
	    file.load(mapGrid);
	}
	
	public void saveMap(){
	    MapFile file = new MapFile();
	    file.save(mapGrid.getByteGrid());
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
