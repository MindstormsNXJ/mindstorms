package de.fh.zwickau.mindstorms.server.navigation.mapping;

import java.util.ArrayList;

import lejos.geom.Line;
import lejos.geom.Point;
import lejos.geom.Rectangle;
import lejos.robotics.mapping.LineMap;

public class ConverterV2 {

	public static LineMap convertGridToLineMap(MapGrid gridMap, int halfRoboterSize) {
		int maxAbsValue = gridMap.getGridSize() / 2;
		Rectangle bounds = new Rectangle(-maxAbsValue, maxAbsValue, 2 * maxAbsValue, 2 * maxAbsValue);
		ArrayList<Line> lineList = new ArrayList<Line>();
		byte[][] bytes = gridMap.getByteGrid();
		bytes = flipArray(bytes);
		int arrayLength = bytes.length; //value for both dimensions
		for (int j = 0; j < arrayLength; ++j) {
//			System.out.println();
			for (int i = 0; i < arrayLength; ++i) {
//				System.out.print(bytes[i][j] + " ");
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
							; //do nothing, it's a vertical line
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
							; //do nothing, it's a horizontal line
						} else {
							verticalLine = new Line(i, j, i, y);
						}
					}
					if (horizontalLine != null)
						lineList.add(horizontalLine);
					if (verticalLine != null)
						lineList.add(verticalLine);
				}
			}
		}		
		Line[] lines = broadenLines(lineList, halfRoboterSize);
		lines = movePointsToWorldCoordinates(lines, maxAbsValue);
		LineMap lineMap = new LineMap(lines, bounds);
		return lineMap;
	}
	
	private static byte[][] flipArray(byte[][] bytes) {
		byte[][] returnArray = new byte[bytes.length][bytes.length];
		for (int j = 0; j < bytes.length; ++j) {
			for (int i = 0; i < bytes.length; ++i) {
				returnArray[i][j] = bytes[i][bytes.length - 1 - j];
			}
		}
		return returnArray;
	}
	
	private static boolean partOfLine(ArrayList<Line> lineList, int xCor, int yCor) {
		for (Line line : lineList)
			if (containsPoint(line, xCor, yCor)) 
				return true;
		return false;
	}
	
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
	
	private static Line[] broadenLines(ArrayList<Line> lineList, int halfRoboterSize) {
		Line[] lines = new Line[lineList.size() * 4];
		int index = 0;
		for (Line line : lineList) {
			if (isHorizontalLine(line)) {
				lines[index] = new Line(line.x1 - halfRoboterSize, line.y1 - halfRoboterSize, line.x2 + halfRoboterSize, line.y2 - halfRoboterSize);
				++index;
				lines[index] = new Line(line.x2 + halfRoboterSize, line.y2 - halfRoboterSize, line.x2 + halfRoboterSize, line.y2 + halfRoboterSize);
				++index;
				lines[index] = new Line(line.x2 + halfRoboterSize, line.y2 + halfRoboterSize, line.x1 - halfRoboterSize, line.y1 + halfRoboterSize);
				++index;
				lines[index] = new Line(line.x1 - halfRoboterSize, line.y1 + halfRoboterSize, line.x1 - halfRoboterSize, line.y1 - halfRoboterSize);
				++index;
			} else {
				lines[index] = new Line(line.x1 - halfRoboterSize, line.y1 - halfRoboterSize, line.x1 + halfRoboterSize, line.y1 - halfRoboterSize);
				++index;
				lines[index] = new Line(line.x1 + halfRoboterSize, line.y1 - halfRoboterSize, line.x2 + halfRoboterSize, line.y2 + halfRoboterSize);
				++index;
				lines[index] = new Line(line.x2 + halfRoboterSize, line.y2 + halfRoboterSize, line.x2 - halfRoboterSize, line.y2 + halfRoboterSize);
				++index;
				lines[index] = new Line(line.x2 - halfRoboterSize, line.y2 + halfRoboterSize, line.x1 - halfRoboterSize, line.y1 - halfRoboterSize);
				++index;
			}
		}
		return lines;
	}
	
	private static boolean isHorizontalLine(Line line) {
		if (line.y1 == line.y2)
			return true;
		else
			return false;
	}
	
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
//			switch (q1) {
//			case FIRST:
//				p1.x = p1.x - maxAbsValue;
//				break;
//			case SECOND:
//				p1.x = -(maxAbsValue - p1.x);
//				break;
//			case THIRD: 
//				p1.x = -(maxAbsValue - p1.x);
//				break;
//			case FOURTH: 
//				p1.x = p1.x - maxAbsValue;
//				break;
//			}
//			p1.y = maxAbsValue - p1.y;
//			switch (q2) {
//			case FIRST:
//				p2.x = p2.x - maxAbsValue;
//				break;
//			case SECOND:
//				p2.x = -(maxAbsValue - p2.x);
//				break;
//			case THIRD: 
//				p2.x = -(maxAbsValue - p2.x);
//				break;
//			case FOURTH: 
//				p2.x = p2.x - maxAbsValue;
//				break;
//			}
//			p2.y = maxAbsValue - p2.y;
			returnArray[index] = new Line(p1.x, p1.y, p2.x, p2.y);
			++index;
		}
		return returnArray;
	}
	
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
	
	private enum Quadrant {
		FIRST, SECOND, THIRD, FOURTH;
	}
	
}
