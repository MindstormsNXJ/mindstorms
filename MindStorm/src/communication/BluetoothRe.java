package communication;

import java.io.*;
import lejos.nxt.*;
import lejos.nxt.comm.*;
import lejos.util.Delay;

/**
 * Simple test for connection between NXT and PC via bluetooth.
 * In this case the NXT is the receiver and the PC the initiator.
 * After the connection is established two strings are exchanged
 * via Input- and Output streams.
 * This class is running on the NXT and waiting for USB-connection.
 * The corresponding Class for the PC is called USBInitiator in
 * the project MindStormPC, package communication.
 * 
 * @author simon
 */
public class BluetoothRe {
	public static void main(String[] args) {
		// the NXT is waiting for a usb connection
		System.out.println("waiting for Bluetooth...");
		NXTConnection connection = Bluetooth.waitForConnection();
		
		// get input- and output stream from connection
		DataOutputStream dataOut = connection.openDataOutputStream();
		DataInputStream dataIn = connection.openDataInputStream();
		
		// try to exchange information over IO-streams
		try {
			// write a String in the dataOutStream and flush it
			dataOut.writeUTF("hello computer!");
			dataOut.flush();
			
			// read DataInStream
			System.out.println(dataIn.readUTF());
			Sound.beep();
			Delay.msDelay(8000);
		} catch (IOException e) {
			System.out.println(" write error " + e);
		}
		
	}
}