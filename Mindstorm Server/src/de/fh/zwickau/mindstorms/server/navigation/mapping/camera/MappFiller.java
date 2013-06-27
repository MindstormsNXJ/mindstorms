package de.fh.zwickau.mindstorms.server.navigation.mapping.camera;

import java.nio.FloatBuffer;

import de.fh.zwickau.mindstorms.server.navigation.mapping.Mapper;
/**
 * The Klass which fills the map with the Given datas with given scaling
 * @author Aismael
 *
 */
public class MappFiller {

	Mapper mapper;//the Mapper where the points to register
	private float yScaleFor64Grid;//y scaling for a 64*64 grid
	private float xScaleFor64Grid;//x scaling for a 64*64 grid
	
	/*
	 * registers the mapper
	 */
	public void setMapper(Mapper mapper) {
		this.mapper=mapper;
		
	}

	/**
	 * takes the Mapdatas and fills the map with the to 64 Grid converted points
	 * @param ballPoint position of the ball
	 * @param goalPoint position of the Goal
	 * @param computedBuffer 64*64 Grid for the obstacles
	 * @param xScaleFor64Grid
	 * @param yScaleFor64Grid
	 */
	public void analyzeAndWriteDataRegister(Point ballPoint, Point goalPoint,FloatBuffer computedBuffer, float xScaleFor64Grid, float yScaleFor64Grid) {
		this.yScaleFor64Grid=yScaleFor64Grid;
		this.xScaleFor64Grid=xScaleFor64Grid;
		mapper.setBallPosition(makePointTolejosPoint(ballPoint));
		mapper.setGoalPosition(makePointTolejosPoint(goalPoint));
		System.out.println("Registriere Photodaten");
		analyzeObstaclemap(computedBuffer);
		System.out.println("Registrierung beendet");
	}
	
	/**
	 *  make solo points out of the 64*64 Grid for the obstacles and register them on the Map
	 * @param computedBuffer
	 */
	private void analyzeObstaclemap(FloatBuffer computedBuffer){
		float[] obstaclePoints=new float[computedBuffer.remaining()];
		computedBuffer.get(obstaclePoints);
		int k=0;
		float verg = 1.0f;
		for (int i = 0; i < 64; i++){
			for (int j = 0; j< 64; j++){
				if(obstaclePoints[k] == verg){
					Point obPoint = new Point(j, i);
					mapper.addObstacle(makePointTolejosPoint(obPoint));
				}
				k++;
			}
		}
	}
	
	/**
	 * Make a Double Point from the Original image to a point on the 64*64 Grid
	 * @param toChange Double Point who is to Change
	 * @returnLejos Point on a 64*64 Grid
	 */
	private lejos.geom.Point makePointTolejosPoint(Point toChange){
		lejos.geom.Point lePoint = new lejos.geom.Point (((float)toChange.getX() - 32) * xScaleFor64Grid, ((float)toChange.getY() - 32) * yScaleFor64Grid);
		return lePoint;
	}
}


