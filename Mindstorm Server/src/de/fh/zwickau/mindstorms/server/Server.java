package de.fh.zwickau.mindstorms.server;

import java.util.ArrayList;

import de.fh.zwickau.mindstorms.server.communication.ConnectionManager;
import de.fh.zwickau.mindstorms.server.navigation.TargetManager;
import de.fh.zwickau.mindstorms.server.navigation.mapping.Mapper;
import de.fh.zwickau.mindstorms.server.view.View;

public class Server {
	
	private View view;
	private Mapper mapper;
	private TargetManager targetManager;
	private ArrayList<ConnectionManager> connectionManagers;

	public Server(){
		connectionManagers = new ArrayList<ConnectionManager>();
		
		view = new View();
		mapper = new Mapper(1);
		targetManager = TargetManager.getInstance();
		
		mapper.setBallPosition(targetManager.getBallWaypoint());
		mapper.setGoalPosition(targetManager.getFinalTarget());
		
		registerObjects();
		
		view.start();	//new Thread for view
		
		connectionManagers.add(new ConnectionManager(mapper, "Picker"));
	}
	
	private void registerObjects(){
		mapper.setController(this);
		view.setController(this);
		targetManager.setController(this);
		
		view.registerMapper(mapper);
		view.registerTargetManager(targetManager);
	}
	
	public void saveMap(){
		mapper.saveMap();
	}
	
	public void mapChanged(){
		view.mapChanged();
//		for (ConnectionManager manager : connectionManagers)
//			manager.mapChaged();
	}
	
	public void targetChanged(){
		view.targetChanged();
	}
}
