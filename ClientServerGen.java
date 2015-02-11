import java.io.IOException;
import java.io.*;
import java.net.*;
import java.lang.*;
import java.util.*;

public class ClientServerGen{
    public static String filename;
    public static String serverPort;
    public static String selfIp;
    public static String client1;
    public static String client2;
    public static SortedSet<PeerNode> peerList = new TreeSet<PeerNode>(new Comp());
    public static void main(String[] args){
        serverPort = args[2];
        filename = args[3];
        selfIp = args[4];
        client1 = args[5];
        client2 = args[6];
        Thread udpThread = new Thread(new udpClient(Integer.parseInt(args[0]), args[1]));
        udpThread.start();

        /* Start a TCP receive thread */
        ServerSocket ss = null;
        try
		{
		ss = new ServerSocket(Integer.parseInt(serverPort));
		}
		catch(Exception e)
		{

		}
		Socket s=null;
		while(true){
            try {
	    	//ss = new ServerSocket(Integer.parseInt(serverPort));
                 s = ss.accept();
                System.out.println("Server:Accepted Connection");
		  		Thread server = new Thread(new Server(ss, s));
                System.out.println("ClientServer:Created Thread for " +s. getRemoteSocketAddress());
		server.start();
            } catch (Exception e) {
        	try{
				   s.close();
                   //Thread.sleep(1);   
		}catch(Exception e1){}
            }
        }
    }

}

class Comp implements Comparator<PeerNode>{
 
    @Override
    public int  compare(PeerNode pn1,PeerNode pn2) {
        if( pn1.getWeight() > pn2.getWeight())
			return 1;
		return -1 ; 
    }
}  
