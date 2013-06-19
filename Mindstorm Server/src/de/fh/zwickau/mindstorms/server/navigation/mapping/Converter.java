package de.fh.zwickau.mindstorms.server.navigation.mapping;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;

import java.util.ArrayList;

import lejos.geom.Line;
import lejos.geom.Point;
import lejos.geom.Rectangle;
import lejos.robotics.mapping.LineMap;
import lejos.robotics.navigation.Pose;

/**
 * Helper class to convert MapGrid to LineMap
 * and Pose/distance Data to World coordinates.
 * 
 * @author Patrick Rosenkranz, Andre Furchner
 *
 */
public class Converter {

	/**
	 * Calculate the World Position of a barrier.
	 * @param pose Robot Pose
	 * @param dist distance
	 * @return float[2] point
	 */
	public static Point calculateObstaclePosition(Pose pose, int dist) {
	
		Point target = new Point(0);	
		target.x = (float)(sin(toRadians(pose.getHeading())) * dist + pose.getX());
		target.y = (float)(cos(toRadians(pose.getHeading())) * dist + pose.getY());
		
		return target;
	}
	
	/**
	 * Convert a MapGrid to a LineMap
	 * 
	 * @return LineMap
	 */
	public static LineMap gridToLineMap(MapGrid grid) {
		
		final int g_size = grid.getGridSize();
		float gsh = g_size / 2.0f;					// gridSize / 2
		final float t_size = grid.getTileSize();
		ArrayList<Line> line_array = new ArrayList<Line>();
		
		for(int x = 0; x < g_size; x++) {
			for(int y = 0; y < g_size; y++) {
				if(grid.get(x, y) == 0) {

					//right
					if(x != g_size -1){
						if(grid.get(x + 1, y) > 0) {
							line_array.add(new Line((x + 1 - gsh) * t_size, (y - gsh) * t_size, (x + 1 - gsh) * t_size, (y + 1 - gsh) * t_size));
						}
					}
					
					//left
					if(x != 0){
						if(grid.get(x - 1, y) > 0) {
							line_array.add(new Line((x - gsh) * t_size, (y - gsh) * t_size, (x - gsh) * t_size, (y + 1 - gsh) * t_size));
						}
					}
					
					//up
					if(y != g_size -1){
						if(grid.get(x, y + 1) > 0) {
							line_array.add(new Line((x - gsh) * t_size, (y + 1 - gsh) * t_size, (x + 1 - gsh) * t_size, (y + 1 - gsh) * t_size));
						}
					}
					
					//down
					if(y != 0){
						if(grid.get(x, y - 1) > 0) {
							line_array.add(new Line((x - gsh) * t_size, (y - gsh) * t_size, (x + 1 - gsh) * t_size, (y - gsh) * t_size));
						}
					}
				}
			}
		}
		
		//Create the LineArray for LineMap constructor.
		Line[] lines = new Line[line_array.size()];
		for(int i = 0; i < lines.length; i++){
			lines[i] = line_array.get(i);
		}
		
		//calculate rectangle
		float recp = g_size * grid.getTileSize() / 2.0f;
		return new LineMap(lines, new Rectangle(-recp, -recp, recp, recp));
	}
}
