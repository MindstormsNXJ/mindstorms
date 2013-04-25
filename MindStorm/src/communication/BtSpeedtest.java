package communication;

import java.io.*;

import lejos.nxt.*;
import lejos.nxt.comm.*;
import lejos.util.Delay;

/**
 * Simple test for connection between NXT and PC via bluetooth. In this case the
 * NXT is the receiver and the PC the initiator. After the connection is
 * established two strings are exchanged via Input- and Output streams. This
 * class is running on the NXT and waiting for USB-connection. The corresponding
 * Class for the PC is called USBInitiator in the project MindStormPC, package
 * communication.
 * 
 * @author simon
 */
public class BtSpeedtest {
	static NXTConnection connection=null;
	static DataOutputStream dataOut=null;
	static DataInputStream dataIn=null;
	public static void main(String[] args) {
			// the NXT is waiting for a usb connection
		byte[] b = new byte[256];
		byte[] c = new byte[256];
		while (true) {
			System.out.println("waiting for Bluetooth...");
			 
				 connect();
			 

			

			// try to exchange information over IO-streams

			try {
				// write a String in the dataOutStream and flush it
				for (int i = 0; i < 256; i++) {
					b[i] = (byte) i;
				}
				dataOut.write(b);
				dataOut.flush();

				System.out.println("gesendet");

				dataIn.read(c);
				System.out.println("gelesen");

				Sound.beep();
				connection.close();
			} catch (IOException e) {
				System.out.println(" write error " + e);
			}
		}
	}
	static void connect(){
		try{
		connection = Bluetooth.waitForConnection();
		// get input- and output stream from connection
			dataOut = connection.openDataOutputStream();
			dataIn = connection.openDataInputStream();
		}catch(Exception e){
			System.out.println("Streamlesefehler");
			System.out.println("waiting for Bluetooth...");
			Delay.msDelay(1000);
			connect();
		}
	}
}