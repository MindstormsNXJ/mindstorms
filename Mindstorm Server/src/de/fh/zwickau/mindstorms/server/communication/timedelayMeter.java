package de.fh.zwickau.mindstorms.server.communication;


import java.io.*;
import java.sql.Time;

import lejos.pc.comm.NXTConnector;

/**testclass 
 * to connect two specific nxts
 * @author Aismael
 *
 */
public class timedelayMeter
{

    public static NXTConnector link;
    public static NXTConnector link2;
	private static boolean trigger1=false;
	private static boolean trigger2=false;

    public timedelayMeter()
    {
    }

    public static void main(String args[])
    {
    	/**
    	 * Connect to the first specific nxt
    	 */
        link = new NXTConnector();
        if(link.connectTo("DruckFarbe2")){
        	trigger1=true;
        }
            System.out.println("df.1 connect");
        /**
    	 * Connect to the second specific nxt
    	 */
        link2 = new NXTConnector();
        if(link2.connectTo("CompassNXT")){
        	trigger2=true;
        }
            System.out.println("comp.2 connect");
        if(trigger1){
        nxtPing(link,1);}
        if(trigger2){
        nxtPing(link2,2);}
        
    }

    /**
     * method to ping a text to a nxt where BluetoothRe is running
     * @param uselink
     */
    private static void nxtPing(NXTConnector uselink,int linknr)
    {
        try
        {
        	long zstVorher;
        	long zstNachher;

        	zstVorher = System.currentTimeMillis();
        	
            DataOutputStream dataOut = new DataOutputStream(uselink.getOutputStream());
            byte[] b= new byte [256];
			for(int i =0;i<256;i++){
				b[i]=(byte) i;
			}
            dataOut.write(b);
            dataOut.flush();
            System.out.println("gesendet");
            java.io.InputStream is = uselink.getInputStream();
            DataInputStream dis = new DataInputStream(is);
            byte[] c= new byte [256];
            dis.read(c);
            System.out.println("gelesen");
            zstNachher = System.currentTimeMillis();
            System.out.println("brick"+linknr+"Zeit benötigt: " + ((zstNachher - zstVorher)) + " millisec");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
