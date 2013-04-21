package de.fh.zwickau.mindstorms.server.navigation.mapping;
import static java.lang.Math.*;
import de.fh.zwickau.mindstorms.server.view.View;

import lejos.robotics.mapping.LineMap;
import lejos.robotics.navigation.Pose;

public class Mapper {
	private View observer;
	private MapGrid mapGrid;
	private LineMap lineMap;

	public Mapper() {
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
		//TODO: first check if its a other robot or a goal
		
		//TODO: calculate the right coordinates
		//--- Start
		float xa = pose.getX();
		float ya = pose.getY();
		float xh, yh , xz , yz, n1,n2;
		
		xh = xa;
		
		n1 =(float) (-(-ya/2)+(sqrt((ya*ya/4)-(ya*ya-dist*dist))));
		n2 =(float) (-(-ya/2)-(sqrt((ya*ya/4)-(ya*ya-dist*dist))));
		
		if(n1>n2||n1==n2) {
			yh =n1;
		} else if(n2>n1){
			yh = n2;
		} else {
			System.out.println("Error. Helpvector has no zero point");
			yh = -1;
		}
		//Nur zu überprüfung gedacht - Später gelöscht.
		System.out.println("Länger Der Distanz ist:" + dist 
				+ " . Und die länger des hilfsvektors ist:" + sqrt((yh-ya)*(yh-ya)) );
		
		//Translation 0,0
		xh =- xa;
		yh =- ya;
		//Rotation
		xz =  (float) (xh * cos(pose.getHeading()) + yh * sin(pose.getHeading()));
		yz =  (float) (yh * cos(pose.getHeading()) - xh * sin(pose.getHeading()));
		//Rücktranslation zu xa,ya;
		xz =+ xa;
		yz =+ ya;
		
		//Nur zu überprüfung gedacht - Später gelöscht.
		System.out.println("Länger Der Distanz ist:" + dist 
				+ " . Und die länger des hilfsvektors ist:" + sqrt((yz-ya)*(yz-ya)+(xz-xa)*(xz-xa)) );				
				
		//--- stop.
		addObstacle((int)(xz +0.5f),(int)(yz + 0.5f));
	}

	/**
	 * Add an Obstacle at the absolute position without
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
