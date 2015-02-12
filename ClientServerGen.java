import java.io.IOException;
import java.io.*;
import java.net.*;
import java.lang.*;
import java.util.*;

public class ClientServerGen{
    public static String selfIp;
    public static String client1;
    public static String client2;
    public static PeerList peerList = new PeerList();
    public static Sync sync;
    public static String serverPort;
    public static void main(String[] args){
        /* Start UDP client thread */
        Thread udpThread = new Thread(new udpClient(Integer.parseInt(args[0])));
        udpThread.start();

        /* Start UDP server thread */
        Thread server = new Thread(new udpServer(args[1]));
        server.start();

        /* Start Sync thread */
        Thread sync = new Thread(new Sync());
        sync.start();

        /* Start a TCP receive thread */
        ServerSocket ss = null;
        try
        {
            serverPort = args[2];
            ss = new ServerSocket(Integer.parseInt(serverPort));
        }
        catch(Exception e)
        {

        }
        Socket s=null;
        while(true){
            try {
                s = ss.accept();
                System.out.println("Server:Accepted Connection");
                Thread server = new Thread(new TcpServer(ss, s));
                System.out.println("ClientServer:Created Thread for " +s. getRemoteSocketAddress());
                server.start();
            } catch (Exception e) {
                try{
                   s.close();
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
