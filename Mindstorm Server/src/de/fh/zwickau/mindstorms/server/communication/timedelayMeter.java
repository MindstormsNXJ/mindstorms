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
	 static byte[] b= new byte [256];
	 static byte[] c= new byte [256];

    public timedelayMeter()
    {
    }

    public static void main(String args[])
    {
    	for(int i =0;i<256;i++){
			b[i]=(byte) i;
		}
    	long durcha=0;
        long durchb=0;
        for(int x = 1;x<101;x++){
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
        	if (x>1){
        	durcha=((nxtPing(link,1)+durcha)/2);
        	System.out.println("durchschnitt brick 1 "+durcha);
        	}else{
        		durcha=(nxtPing(link,1));
            	System.out.println("durchschnitt brick 1 "+durcha);
        	}
        	}
        if(trigger2){
        	if (x>1){
            	durchb=((nxtPing(link2,2)+durchb)/2);
            	System.out.println("durchschnitt brick 2 "+durchb);
            	}else{
            		durcha=(nxtPing(link2,2));
                	System.out.println("durchschnitt brick 2 "+durchb);
            	}
            }
            
        
        	linkcloser();
		
        }
    }

    /**
     * method to ping a text to a nxt where BluetoothRe is running
     * @param uselink
     */
    private static long nxtPing(NXTConnector uselink,int linknr)
    {
        try
        {
        	long zstVorher;
        	long zstNachher;

        	zstVorher = System.currentTimeMillis();
        	
            DataOutputStream dataOut = new DataOutputStream(uselink.getOutputStream());
            dataOut.write(b);
            dataOut.flush();
            System.out.println("gesendet");
            java.io.InputStream is = uselink.getInputStream();
            DataInputStream dis = new DataInputStream(is);
            dis.read(c);
            System.out.println("gelesen");
            zstNachher = System.currentTimeMillis();
            System.out.println("brick"+linknr+"Zeit benötigt: " + ((zstNachher - zstVorher)) + " millisec");
   
            return (zstNachher - zstVorher);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
		return 0;
    }
    static void linkcloser(){
    	try {
			link.close();
			link2.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			linkcloser();
		}
    }
}
