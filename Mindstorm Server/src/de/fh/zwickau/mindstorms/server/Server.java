package de.fh.zwickau.mindstorms.server;

import java.util.ArrayList;

import de.fh.zwickau.mindstorms.server.communication.ConnectionManager;
import de.fh.zwickau.mindstorms.server.navigation.TargetManager;
import de.fh.zwickau.mindstorms.server.navigation.mapping.Mapper;
import de.fh.zwickau.mindstorms.server.navigation.mapping.camera.Camera;
import de.fh.zwickau.mindstorms.server.navigation.mapping.camera.PhotoAnalyzer;
import de.fh.zwickau.mindstorms.server.view.Gui;


public class Server {
	
	private Gui view;
	private Mapper mapper;
	private Camera camera;
	private PhotoAnalyzer photoAnalyzer;
	private TargetManager targetManager;
	private ArrayList<ConnectionManager> connectionManagers;

	public Server(){
		connectionManagers = new ArrayList<ConnectionManager>();
		
		view = new Gui();
		mapper = new Mapper(4);
        camera = new Camera();
        photoAnalyzer= new PhotoAnalyzer(camera);
		targetManager = TargetManager.getInstance();
		
		mapper.setBallPosition(targetManager.getBallWaypoint());
		mapper.setGoalPosition(targetManager.getFinalTarget());
		
		registerObjects();
		
		view.start();	//new Thread for view
		
		connectionManagers.add(new ConnectionManager(mapper, "Picker", this));
	}
	
	private void registerObjects(){
		mapper.setController(this);
		view.setController(this);
		targetManager.setController(this);
		
		view.registerMapper(mapper);
		view.registerCamera(camera);
		view.registerTargetManager(targetManager);
		camera.registerMapper(mapper);
	}
	
	public void saveMap(){
		mapper.saveMap();
	}
	
	public void mapChanged(){
		view.mapChanged();
		for (ConnectionManager manager : connectionManagers)
			manager.mapChaged();
	}
	
	public void targetChanged(String robotName){
		//TODO forward the robot name was well
		view.targetChanged();
	}
	
	/**
	 * Removes a ConnectionManager ones the connection is terminated. The call will be made by the 
	 * manager itself.
	 * @param manager
	 */
	public void removeConnection(ConnectionManager manager) {
		connectionManagers.remove(manager);
	}
	
	 public void setPhotoAnalyzerVisible(){
		 photoAnalyzer.setPhotoAnalyzerVisible();
	 }
	
	public ConnectionManager getConnectionManager() {
		return connectionManagers.get(0);
	}
	
}

