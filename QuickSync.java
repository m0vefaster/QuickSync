import java.io.IOException;
import java.io.*;
import java.net.*;
import java.lang.*;
import java.util.*;

public class QuickSync{
    public static String selfIp;
    public static String client1;
    public static String client2;
    public static ListOfPeers peerList;
    public static Sync sync;
    public static String serverPort;
    public static void main(String[] args){
        PeerNode self = new PeerNode(args[4], 1);
        peerList = new ListOfPeers(self);
        /* Start UDP client thread */
        Thread udpClient = new Thread(new UdpClient(Integer.parseInt(args[0]), args[3], args[5]));
        udpClient.start();

        /* Start UDP server thread */
        Thread udpServer = new Thread(new UdpServer(Integer.parseInt(args[1])));
        udpServer.start();

        /* Start Sync thread */
        Thread sync = new Thread(new Sync(peerList));
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
