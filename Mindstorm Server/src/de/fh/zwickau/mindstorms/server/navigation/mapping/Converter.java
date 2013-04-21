package de.fh.zwickau.mindstorms.server.navigation.mapping;

import static java.lang.Math.*;

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
}
