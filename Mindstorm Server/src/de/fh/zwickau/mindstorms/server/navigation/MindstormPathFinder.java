package de.fh.zwickau.mindstorms.server.navigation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.naming.OperationNotSupportedException;

import lejos.geom.Line;
import lejos.geom.Point;
import lejos.geom.Rectangle;
import lejos.robotics.mapping.LineMap;
import lejos.robotics.navigation.DestinationUnreachableException;
import lejos.robotics.navigation.Pose;
import lejos.robotics.navigation.Waypoint;
import lejos.robotics.navigation.WaypointListener;
import lejos.robotics.pathfinding.Path;
import lejos.robotics.pathfinding.PathFinder;
import de.fh.zwickau.mindstorms.server.navigation.mapping.MapGrid;
import de.fh.zwickau.mindstorms.server.navigation.mapping.Mapper;

public class MindstormPathFinder implements PathFinder {

	private LineMap lineMap;
	private MapGrid gridMap;
	private Map<Point, ArrayList<Point>> connections = new HashMap<Point, ArrayList<Point>>();
	
	public MindstormPathFinder(Mapper mapper) {
//		lineMap = mapper.getLineMap(); //TODO enable when bugs are fixed
		Line[] lines = new Line[2];
		lines[0] = new Line(0,5,5,5);
		lines[1] = new Line(5,5,5,0);
		lineMap = new LineMap(lines, new Rectangle(-31, -31, 62, 62));
		
		gridMap = mapper.getGrid();
		parseMapToGraph();
	}
	
	private void parseMapToGraph() {
		ArrayList<Point> points = getPoints();
		//TODO do something with the points that are directly on the map's border
		initGraph(points);
	}

	private ArrayList<Point> getPoints() {
		Line[] lines = lineMap.getLines();
		ArrayList<Point> points = new ArrayList<Point>();
		for (int i = 0; i < lines.length; ++i) {
			Line line = lines[i];
			Point p1 = line.getP1();
			Point p2 = line.getP2();
			if (!points.contains(p1))
				points.add(p1);
			if (!points.contains(p2))
				points.add(p2);
		}
		return points;
	}
	
	private void initGraph(ArrayList<Point> points) {
		for (int i = 0; i < points.size(); ++i) {
			Point point = points.get(i);
			connections.put(point, new ArrayList<Point>());
			for (int j = 0; j < points.size(); ++j) {
				Point targetPoint = points.get(j);
				boolean reachable = isReachable(point, targetPoint);
				if (reachable)
					connections.get(point).add(targetPoint);
			}
		}
	}
	
	private boolean isReachable(Point startPoint, Point targetPoint) {
		Line[] lines = lineMap.getLines();
		Line pointToTargetPoint = new Line(startPoint.x, startPoint.y, targetPoint.x, targetPoint.y);
		boolean reachable = true;
		for (int i = 0; i < lines.length; ++i) {
			Line line = lines[i];
			reachable = !intersect(pointToTargetPoint, line);
			if (!reachable)
				break;
		}
		return reachable;
	}

	//TODO should return false if the lines intersect at a corner
	private boolean intersect(Line line1, Line line2) {
		float x1 = line1.x1;
		float y1 = line1.y1;
		float x2 = line1.x2;
		float y2 = line1.y2;
		float x3 = line2.x1;
		float y3 = line2.y1;
		float x4 = line2.x2;
		float y4 = line2.y2;
		
		// Return false if either of the lines have zero length
	    if (x1 == x2 && y1 == y2 || x3 == x4 && y3 == y4)
	    	return false;
	    
	    // Fastest method, based on Franklin Antonio's "Faster Line Segment Intersection" topic "in Graphics Gems III" book (http://www.graphicsgems.org/)
	    double ax = x2-x1;
	    double ay = y2-y1;
	    double bx = x3-x4;
	    double by = y3-y4;
	    double cx = x1-x3;
	    double cy = y1-y3;

	    double alphaNumerator = by*cx - bx*cy;
	    double commonDenominator = ay*bx - ax*by;
	    if (commonDenominator > 0)
	        if (alphaNumerator < 0 || alphaNumerator > commonDenominator)
	            return false;
	    else if (commonDenominator < 0)
	        if (alphaNumerator > 0 || alphaNumerator < commonDenominator)
	            return false;
	    
	    double betaNumerator = ax*cy - ay*cx;
	    if (commonDenominator > 0)
	        if (betaNumerator < 0 || betaNumerator > commonDenominator)
	            return false;
	    else if (commonDenominator < 0)
	        if (betaNumerator > 0 || betaNumerator < commonDenominator)
	            return false;
	    
	    if (commonDenominator == 0) {
	    	// This code wasn't in Franklin Antonio's method. It was added by Keith Woodward.
	        // The lines are parallel.
	        // Check if they're collinear.
	        double y3LessY1 = y3-y1;
	        double collinearityTestForP3 = x1*(y2-y3) + x2*(y3LessY1) + x3*(y1-y2);   // see http://mathworld.wolfram.com/Collinear.html
	        // If p3 is collinear with p1 and p2 then p4 will also be collinear, since p1-p2 is parallel with p3-p4
	        if (collinearityTestForP3 == 0) {
	            // The lines are collinear. Now check if they overlap.
	            if (x1 >= x3 && x1 <= x4 || x1 <= x3 && x1 >= x4 || x2 >= x3 && x2 <= x4 || x2 <= x3 && x2 >= x4 || x3 >= x1 && x3 <= x2 || x3 <= x1 && x3 >= x2) {
	            	if (y1 >= y3 && y1 <= y4 || y1 <= y3 && y1 >= y4 || y2 >= y3 && y2 <= y4 || y2 <= y3 && y2 >= y4 || y3 >= y1 && y3 <= y2 || y3 <= y1 && y3 >= y2) {
	            		return true;
	            	}
	            }
         	}
         	return false;
	    }
	    Point intersectionPoint = getLineLineIntersection(x1, y1, x2, y2, x3, y3, x4, y4);
	    if (intersectionPoint.equals(line1.getP1()) || intersectionPoint.equals(line1.getP2()))
	    	return false;
	    return true;
	}
	
	public static Point getLineLineIntersection(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
	    double det1And2 = det(x1, y1, x2, y2);
	    double det3And4 = det(x3, y3, x4, y4);
	    double x1LessX2 = x1 - x2;
	    double y1LessY2 = y1 - y2;
	    double x3LessX4 = x3 - x4;
	    double y3LessY4 = y3 - y4;
	    double det1Less2And3Less4 = det(x1LessX2, y1LessY2, x3LessX4, y3LessY4);
	    if (det1Less2And3Less4 == 0){
	        // the denominator is zero so the lines are parallel and there's either no solution (or multiple solutions if the lines overlap) so return null.
	        return null;
	    }
	    double x = (det(det1And2, x1LessX2, det3And4, x3LessX4) / det1Less2And3Less4);
	    double y = (det(det1And2, y1LessY2, det3And4, y3LessY4) / det1Less2And3Less4);
	    return new Point((float) x, (float) y);
	}
	
	protected static double det(double a, double b, double c, double d) {
	    return a * d - b * c;
	}

	@Override
	public Path findRoute(Pose start, Waypoint destination) throws DestinationUnreachableException {
		Path path = new Path();
		path.add(new Waypoint(start));
		if (isReachable(new Point(start.getX(), start.getY()), destination)) {
			path.add(destination);
		} else {
			HashMap<Path, Float> pathToDistance = new HashMap<Path, Float>();
			pathToDistance.put(path, (float) 0);
			HashMap<Path, ArrayList<Point>> notVisitedPoints = new HashMap<Path, ArrayList<Point>>();
			ArrayList<Point> pointsToVisit = new ArrayList<Point>(connections.keySet());
			pointsToVisit.add(destination);
			notVisitedPoints.put(path, pointsToVisit);
			while (true) {
				Path finalPath = nextStep(pathToDistance, notVisitedPoints, destination);
				if (finalPath != null) {
					path = finalPath;
					break;
				}
			}
		}
		return path;
	}

	private Path nextStep(HashMap<Path, Float> pathToDistance, HashMap<Path, ArrayList<Point>> notVisitedPoints, Waypoint target) {
		float minDistance = 0;
		Set<Path> paths = pathToDistance.keySet();
		Path pathToFollow = paths.iterator().next(); //first entry
		for (Path path : paths)
			if (pathToDistance.get(path) < minDistance) {
				minDistance = pathToDistance.get(path);
				pathToFollow = path;
			}
				
		Waypoint lastPoint = pathToFollow.get(pathToFollow.size() - 1);
		ArrayList<Point> reachablePoints = findReachablePoints(lastPoint, target);
		for (int i = 0; i < reachablePoints.size(); ++i) {
			Point point = reachablePoints.get(i);
			if (notVisitedPoints.get(pathToFollow).contains(point)) {
				Path pathToAdd = new Path();
				pathToAdd.addAll(pathToFollow);
				pathToAdd.add(new Waypoint(point));
				float distanceToAdd = (float) Math.sqrt(Math.pow(point.x - lastPoint.x, 2) + Math.pow(point.y - lastPoint.y, 2));
				pathToDistance.put(pathToAdd, minDistance + distanceToAdd);
				ArrayList<Point> newNotVisitedPointList = new ArrayList<Point>();
				newNotVisitedPointList.addAll(notVisitedPoints.get(pathToFollow));
				newNotVisitedPointList.remove(point);
				notVisitedPoints.put(pathToAdd, newNotVisitedPointList);
				if (point.x == target.x && point.y == target.y)
					return pathToAdd;
			}
		}
		
		notVisitedPoints.remove(pathToFollow);
		pathToDistance.remove(pathToFollow);
		return null;
	}

	private ArrayList<Point> findReachablePoints(Waypoint waypoint, Point target) {
		return findReachablePoints(new Pose(waypoint.x, waypoint.y, 0), target);
	}

	private ArrayList<Point> findReachablePoints(Pose start, Point target) {
		ArrayList<Point> reachablePoints = new ArrayList<Point>();
		Line[] lines = lineMap.getLines();
		ArrayList<Point> points = new ArrayList<Point>(connections.keySet());
		points.add(target);
		Iterator<Point> iterator = points.iterator();
		while (iterator.hasNext()) {
			Point point = iterator.next();
			Line toPoint = new Line(start.getX(), start.getY(), point.x, point.y);
			for (int j = 0; j < lines.length; ++j) {
				Line line = lines[j];
				if (!reachablePoints.contains(point) && !intersect(toPoint, line))
					reachablePoints.add(point);
			}
		}
		return reachablePoints;
	}

	@Override
	public void addListener(WaypointListener wpl) {
		try {
			throw new OperationNotSupportedException("Listeners are not supported");
		} catch (OperationNotSupportedException e) {
			//nothing
		}
	}

	@Override
	public void startPathFinding(Pose start, Waypoint end) {
		try {
			throw new OperationNotSupportedException("Use findRoute() instead");
		} catch (OperationNotSupportedException e) {
			//nothing
		}
	}

}
