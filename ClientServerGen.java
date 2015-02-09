import java.io.IOException;
import java.io.*;
import java.net.*;
import java.lang.*;
import java.util.*;

public class ClientServerGen{
    public static String filename;
    public static String serverPort;
    public static LinkedList<peerNode> peerList = new LinkedList<peerNode>();
    public static void main(String[] args){
        serverPort = args[2];
        filename = args[3];
        Thread udpThread = new Thread(new udpClient(Integer.parseInt(args[0]), args[1]));
        udpThread.start();

        /* Start a TCP receive thread */
        ServerSocket ss = null;
        while(true){
            try {
	    	ss = new ServerSocket(Integer.parseInt(serverPort));
                Socket s = ss.accept();
                Thread server = new Thread(new Server(ss, s));
                System.out.println("ClientServer:Created Thread for " +s. getRemoteSocketAddress());
		server.start();
            } catch (Exception e) {
        	try{
                    Thread.sleep(100);   
		}catch(Exception e1){}
            }
        }
    }
}

