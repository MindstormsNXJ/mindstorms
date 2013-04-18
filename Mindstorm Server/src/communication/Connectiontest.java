package communication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import lejos.pc.comm.NXTConnector;


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
