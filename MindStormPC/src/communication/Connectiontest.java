package communication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;

import lejos.nxt.LCD;
import lejos.nxt.remote.RemoteMotor;
import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTConnector;
import lejos.pc.tools.NXJControl;

/**
 * This class is a automatic connector.
 * This little application is running on a PC. It receives
 * a String from the NXT and sends a String to the NXT for test
 * purposes. Communication is done over IO-streams.
 * 
 * @author aismael
 * 
 */
public class Connectiontest {

	public static NXTConnector link;
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		link = new NXTConnector();
		if(link.connectTo()){
			System.out.println("linked successfull");
		}
		try {
		DataOutputStream dataOut = new DataOutputStream(link.getOutputStream());
		dataOut.writeUTF("hello NXT!");
		dataOut.flush();

		InputStream is = link.getInputStream();
		DataInputStream dis = new DataInputStream(is);
		System.out.println(dis.readUTF());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
