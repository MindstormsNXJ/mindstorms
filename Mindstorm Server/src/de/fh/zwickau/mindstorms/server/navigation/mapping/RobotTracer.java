package de.fh.zwickau.mindstorms.server.navigation.mapping;

import java.util.ArrayList;
import java.util.HashMap;

import lejos.robotics.navigation.Pose;

/**
 * The RobotTracer is used to trace robot Poses.
 * Add a Pose with trace(name,pose);
 * 
 * @author Andre Furchner
 * 
 */
public class RobotTracer {
    ArrayList<String> nxtNames;
    HashMap<String, ArrayList<Pose>> traceLines;

	public RobotTracer(){
		traceLines = new HashMap<String, ArrayList<Pose>>();
		nxtNames = new ArrayList<String>();
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
			nxtNames.add(nxtName);
			traceLines.put(nxtName, list);
		}
		list.add(pose);
	}
	
	/**
	 * The names of all traced nxts
	 * @return name array
	 */
	public String[] getTracedNames(){
	    String[] names = new String[nxtNames.size()];
		for(int i = 0; i < nxtNames.size(); i++){
		    names[i] = nxtNames.get(i);
		}
		return names;
	}
	
	/**
	 * Traced Pose List for specific NXT
	 * @param nxtName
	 * @return pose list
	 */
	public ArrayList<Pose> getTracedPoseList(String nxtName){
	    return traceLines.get(nxtName);
	}
}
