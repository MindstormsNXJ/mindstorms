package de.fh.zwickau.mindstorms.server.navigation.mapping;

import java.util.ArrayList;

import lejos.geom.Line;
import lejos.geom.Point;
import lejos.geom.Rectangle;
import lejos.robotics.mapping.LineMap;

/**
 * This class is responsible for converting a MapGrid into a LineMap.
 * 
 * @author Tobias Schießl
 * @version 1.1
 */
public class ConverterV2 {

	/**
	 * This method converts a MapGrid into a LineMap which can be used for path finding.
	 * 
	 * @param gridMap the grid map to convert
	 * @param halfRoboterSize the half robot size, which will be added to the grid positions to avoid collisions
	 * @return the line map which represents the grid map
	 */
	public static LineMap convertGridToLineMap(MapGrid gridMap, int halfRoboterSize) {
		int maxAbsValue = gridMap.getGridSize() / 2;
		Rectangle bounds = new Rectangle(-maxAbsValue, maxAbsValue, 2 * maxAbsValue, 2 * maxAbsValue);
		ArrayList<Line> lineList = new ArrayList<Line>();
		byte[][] bytes = gridMap.getByteGrid();
		bytes = flipArray(bytes); 
		int arrayLength = bytes.length; //value for both dimensions
		for (int j = 0; j < arrayLength; ++j) { //represents the y coordinate
			for (int i = 0; i < arrayLength; ++i) { //represents the x coordinate - we go through the grid line by line
				if (bytes[i][j] != 0 && !partOfLine(lineList, i, j)) { //something on field (i,j) that is not recognised yet
					int x = i;
					int y = j;
					Line horizontalLine = null;
					Line verticalLine = null;
					{
						//find right end of line
						++x; //one field next to the found field
						while (bytes[x][j] != 0) {
							++x; //the line continues
						}
						//x is the first empty field now
						--x; //and now the last filled field
						if (x == i) {
							; //do nothing, it's a vertical line or a point
						} else {
							horizontalLine = new Line(i, j, x, j);
						}
					}
					{
						//find bottom end of line
						++y; //one field under the found field
						while (bytes[i][y] != 0) {
							++y; //the line continues
						}
						//y is the first empty field now
						--y; //and now the last filled field
						if (y == j) {
							; //do nothing, it's a horizontal line or a point
						} else {
							verticalLine = new Line(i, j, i, y);
						}
					}
					if (horizontalLine != null)
						lineList.add(horizontalLine);
					if (verticalLine != null)
						lineList.add(verticalLine);
					if (horizontalLine == null && verticalLine == null) //only a single point was found
						lineList.add(new Line(i, j, i, j));
				}
			}
		}		
		Line[] lines = broadenLines(lineList, halfRoboterSize); //add the necessary buffer to avoid collisions
		lines = movePointsToWorldCoordinates(lines, maxAbsValue);
		LineMap lineMap = new LineMap(lines, bounds);
		return lineMap;
	}
	
	/**
	 * This method flips the byte array upside down - necessary because for my algorithm the point (0,0) is in the bottom left
	 * corner, not in the top left like in the MapGrid.
	 * 
	 * @param bytes the byte array to flip
	 * @return the flipped byte array
	 */
	private static byte[][] flipArray(byte[][] bytes) {
		byte[][] returnArray = new byte[bytes.length][bytes.length];
		for (int j = 0; j < bytes.length; ++j) 
			for (int i = 0; i < bytes.length; ++i) 
				returnArray[i][j] = bytes[i][bytes.length - 1 - j];
		return returnArray;
	}
	
	/**
	 * This method checks if the found point is already part of a found line.
	 * 
	 * @param lineList the list of lines already found
	 * @param xCor the x coordinate of the point
	 * @param yCor the y coordinate of the point
	 * @return true, if the point is already part of a line
	 */
	private static boolean partOfLine(ArrayList<Line> lineList, int xCor, int yCor) {
		for (Line line : lineList)
			if (containsPoint(line, xCor, yCor)) 
				return true;
		return false;
	}
	
	/**
	 * This method checks if a point is part of the given line or not.
	 * Can only be used for horizontal and vertical lines.
	 * 
	 * @param line the line which has to be checked
	 * @param xCor the points x coordinate
	 * @param yCor the points y coordinate
	 * @return true, if the line contains the given point
	 */
	private static boolean containsPoint(Line line, int xCor, int yCor) {
		//only to use for our horizontal and vertical lines
		if (line.y1 == line.y2) { //horizontal line
			if (yCor != line.y1)
				return false;
			else {
				if (line.x1 < line.x2 && (xCor < line.x1 || xCor > line.x2))
					return false;
				else if (line.x1 > line.x2 && (xCor > line.x1 || xCor < line.x2))
					return false;
				else
					return true;
			}
		} else { //vertical line
			if (xCor != line.x1)
				return false;
			else {
				if (line.y1 < line.y2 && (yCor < line.y1 || yCor > line.y2))
					return false;
				else if (line.y1 > line.y2 && (yCor > line.y1 || yCor < line.y2))
					return false;
				else
					return true;
			}
		}
	}
	
	/**
	 * This method broadens all lines from the line list by the given half robot size to avoid collisions. It will also
	 * convert the list into an array which will be needed for initialising the LineMap itself. For every line in the list
	 * there will be 4 lines in the array.
	 * 
	 * @param lineList the list of found lines
	 * @param halfRoboterSize the half robot size which will be added
	 * @return the new array of lines
	 */
	private static Line[] broadenLines(ArrayList<Line> lineList, int halfRoboterSize) {
		Line[] lines = new Line[lineList.size() * 4];
		int index = 0;
		for (Line line : lineList) {
			if (isHorizontalLine(line)) {
				lines[index] = new Line(line.x1 - halfRoboterSize, line.y1 - halfRoboterSize + 1, line.x2 + halfRoboterSize, line.y2 - halfRoboterSize + 1);
				++index;
				lines[index] = new Line(line.x2 + halfRoboterSize, line.y2 - halfRoboterSize + 1, line.x2 + halfRoboterSize, line.y2 + halfRoboterSize + 1);
				++index;
				lines[index] = new Line(line.x2 + halfRoboterSize, line.y2 + halfRoboterSize + 1, line.x1 - halfRoboterSize, line.y1 + halfRoboterSize + 1);
				++index;
				lines[index] = new Line(line.x1 - halfRoboterSize, line.y1 + halfRoboterSize + 1, line.x1 - halfRoboterSize, line.y1 - halfRoboterSize + 1);
				++index;
			} else {
				lines[index] = new Line(line.x1 - halfRoboterSize, line.y1 - halfRoboterSize + 1, line.x1 + halfRoboterSize, line.y1 - halfRoboterSize + 1);
				++index;
				lines[index] = new Line(line.x1 + halfRoboterSize, line.y1 - halfRoboterSize + 1, line.x2 + halfRoboterSize, line.y2 + halfRoboterSize + 1);
				++index;
				lines[index] = new Line(line.x2 + halfRoboterSize, line.y2 + halfRoboterSize + 1, line.x2 - halfRoboterSize, line.y2 + halfRoboterSize + 1);
				++index;
				lines[index] = new Line(line.x2 - halfRoboterSize, line.y2 + halfRoboterSize + 1, line.x1 - halfRoboterSize, line.y1 - halfRoboterSize + 1);
				++index;
			}
		}
		return lines;
	}
	
	/**
	 * Checks if the given line is horizontal or not.
	 * 
	 * @param line the line to check
	 * @return true, if the line is horizontal
	 */
	private static boolean isHorizontalLine(Line line) {
		if (line.y1 == line.y2)
			return true;
		else
			return false;
	}
	
	/**
	 * Converts the given array of lines into world coordinates (so far, they are all relative to the point (0,0) in the bottom
	 * left corner). We assume, that the max absolute value is equal on both sides.
	 * 
	 * @param lines the line array to convert
	 * @param maxAbsValue the maximal absolute value to both sides for the point (0,0)
	 * @return the converted line array
	 */
	private static Line[] movePointsToWorldCoordinates(Line[] lines, int maxAbsValue) {
		Line[] returnArray = new Line[lines.length];
		int index = 0;
		for (Line line : lines) {
			Point p1 = line.getP1();
			Point p2 = line.getP2();
			Quadrant q1 = getQuadrant(p1, maxAbsValue);
			Quadrant q2 = getQuadrant(p2, maxAbsValue);
			p1 = getPointInWorldCoordinates(p1, q1, maxAbsValue);
			p2 = getPointInWorldCoordinates(p2, q2, maxAbsValue);
			returnArray[index] = new Line(p1.x, p1.y, p2.x, p2.y);
			++index;
		}
		return returnArray;
	}
	
	/**
	 * Calculates the quadrant a point would have in a 2D coordinate system. The point is still relative to the bottom left corner.
	 * 
	 * @param point the point which's quadrant is needed, relative to the bottom left corner
	 * @param maxAbsValue the maximal absolute value to both sides in the coordinate system
	 * @return the quadrant of the given point
	 */
	private static Quadrant getQuadrant(Point point, int maxAbsValue) {
		if (point.x < maxAbsValue) { //second or third
			if (point.y < maxAbsValue)
				return Quadrant.SECOND;
			else
				return Quadrant.THIRD;
		} else { //first or fourth
			if (point.y < maxAbsValue)
				return Quadrant.FIRST;
			else
				return Quadrant.FOURTH;
		}
	}
	
	/**
	 * Returns a new point which represents the old one (relative to the bottom left corner) in world coordinates.
	 * 
	 * @param point the old point to convert, relative to the bottom left corner
	 * @param quadrant the new points theoretical quadrant
	 * @param maxAbsValue the maximal absolute value to both sides of the coordinate system
	 * @return the new point in the 2D coordinate system
	 */
	private static Point getPointInWorldCoordinates(Point point, Quadrant quadrant, int maxAbsValue) {
		switch (quadrant) {
		case FIRST:
			point.x = point.x - maxAbsValue;
			break;
		case SECOND:
			point.x = -(maxAbsValue - point.x);
			break;
		case THIRD: 
			point.x = -(maxAbsValue - point.x);
			break;
		case FOURTH: 
			point.x = point.x - maxAbsValue;
			break;
		}
		point.y = maxAbsValue - point.y;
		return new Point(point.x, point.y);
	}
	
	/**
	 * This enumeration represents the quadrant a point can have.
	 * 
	 * @author Tobias Schießl
	 * @version 1.0
	 */
	private enum Quadrant {
		FIRST, SECOND, THIRD, FOURTH;
	}
	
}
