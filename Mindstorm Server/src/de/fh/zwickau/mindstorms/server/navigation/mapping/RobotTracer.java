package de.fh.zwickau.mindstorms.server.navigation.mapping;

import java.util.ArrayList;
import java.util.HashMap;
import lejos.robotics.navigation.Pose;

public class RobotTracer {
HashMap<String, ArrayList<Pose>> traceLines;

	public RobotTracer(){
		traceLines = new HashMap<String, ArrayList<Pose>>();
	}
	
	/**
	 * Trace a new Pose for a specific NXT Robot
	 * nxtName is the NXTInfo.name attribute from the brick.
	 * @param nxtName
	 * @param pose
	 */
	public void trace(String nxtName, Pose pose){
		ArrayList<Pose> list = traceLines.get(nxtName);
		if(list == null){
			list = new ArrayList<Pose>();
			traceLines.put(nxtName, list);
		}
		list.add(pose);
	}
	
	public void getTraceLines(){
		
	}
}
