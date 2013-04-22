package de.fh.zwickau.mindstorms.server.navigation.mapping;

import static java.lang.Math.*;

import lejos.robotics.mapping.LineMap;
import lejos.robotics.navigation.Pose;

/**
 * Helper class
 * @author Patrick Rosenkranz
 *
 */
public class Converter {

	/**
	 * Calculate the World Position of a barrier.
	 * @param pose Robot Pose
	 * @param dist distance
	 * @return float[2] point
	 */
	public static float[] calculateObstaclePosition(Pose pose, int dist) {
	
		float[] target = new float[2];	
		target[0] = (float)(sin(toRadians((double)pose.getHeading())) * (double)dist + pose.getX());
		target[1] = (float)(cos(toRadians((double)pose.getHeading())) * (double)dist + pose.getY());
		
		return target;
	}
	
	public static LineMap gridToLineMap(MapGrid grid) {
		
		final int size = grid.getGridSize();
		
		for(int x = 0; x < size; x++) {
			for(int y = 0; y < size; y++) {
				
				if(grid.get(x, y) == 0) {
					
					if(x != size -1){
						if(grid.get(x + 1, y) > 0) {
							
						}
					}
					
					if(x != 0){
						if(grid.get(x - 1, y) > 0) {
							
						}
					}
					
					if(y != size -1){
						if(grid.get(x, y + 1) > 0) {
							
						}
					}
					if(y != 0){
						if(grid.get(x, y - 1) > 0) {
							
						}
					}
				}
				
				
				
			}
			
		}
		
		
		return null;
		
	}
}
