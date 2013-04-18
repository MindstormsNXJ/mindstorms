package communication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommFactory;

/**
 * This class belongs to the class USBreceiver.
 * This little application is running on a PC. It receives
 * a String from the NXT and sends a String to the NXT for test
 * purposes. Communication is done over IO-streams.
 * 
 * @author simon
 * 
 */
public class USBInitiator {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			NXTComm comm = NXTCommFactory.createNXTComm(NXTCommFactory.USB);
			comm.open(comm.search(null)[0]);
			DataOutputStream dataOut = new DataOutputStream(comm.getOutputStream());
			dataOut.writeUTF("hello NXT!");
			dataOut.flush();
			InputStream is = comm.getInputStream();
			DataInputStream dis = new DataInputStream(is);
			System.out.println(dis.readUTF());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
