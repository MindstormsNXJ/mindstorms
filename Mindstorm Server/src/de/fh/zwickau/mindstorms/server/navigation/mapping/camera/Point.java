package de.fh.zwickau.mindstorms.server.navigation.mapping.camera;

/**
 * A Class of a Point with X and Y Choords as Double
 * @author Aismael
 *
 */
public class Point {

	double x,y;
	public Point(double x,double y){
		this.x=x;
		this.y=y;
	}
	public double getX() {
		return x;
	}
	public double getY() {
		return y;
	}
	public void setX(double x) {
		this.x = x;
	}
	public void setY(double y) {
		this.y = y;
	}

}
