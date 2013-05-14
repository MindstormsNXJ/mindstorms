package de.fh.zwickau.mindstorms.server;

import java.util.ArrayList;

import javax.naming.OperationNotSupportedException;

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
		view = new View();
		mapper = new Mapper(1);
		targetManager = TargetManager.getInstance();
		
		registerObjects();
		
		view.start();	//new Thread for view
		
		try {
			connectionManagers.add(new ConnectionManager(mapper, "Picker"));
		} catch (OperationNotSupportedException e) {
			System.err.println("Please make sure the connection manager is initialized for the robot with the name \"Picker\" - other robot types are currently not supported");
		}
		
		
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
