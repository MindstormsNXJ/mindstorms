package de.fh.zwickau.mindstorms.server.communication;


import java.io.*;
import lejos.pc.comm.NXTConnector;

/**testclass 
 * to connect two specific nxts
 * @author Aismael
 *
 */
public class ConnectTwo
{

    public static NXTConnector link;
    public static NXTConnector link2;

    public ConnectTwo()
    {
    }

    public static void main(String args[])
    {
    	/**
    	 * Connect to the first specific nxt
    	 */
        link = new NXTConnector();
        if(link.connectTo("DruckFarbe2"))
            System.out.println("df1 connect");
        /**
    	 * Connect to the second specific nxt
    	 */
        link2 = new NXTConnector();
        if(link2.connectTo("CompassNXT"))
            System.out.println("comp connect");
        nxtPing(link);
        nxtPing(link2);
    }

    /**
     * method to ping a text to a nxt where BluetoothRe is running
     * @param uselink
     */
    private static void nxtPing(NXTConnector uselink)
    {
        try
        {
            DataOutputStream dataOut = new DataOutputStream(uselink.getOutputStream());
            dataOut.writeUTF((new StringBuilder("hello NXT!")).append(uselink.toString()).toString());
            dataOut.flush();
            java.io.InputStream is = uselink.getInputStream();
            DataInputStream dis = new DataInputStream(is);
            System.out.println(dis.readUTF());
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
