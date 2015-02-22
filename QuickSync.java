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
        int count = 0;
        ArrayList<String> client = new ArrayList<String>();
        try{
            Enumeration e = NetworkInterface.getNetworkInterfaces();
            NetworkInterface intface = null;
            while(e.hasMoreElements())
            {
                NetworkInterface n = (NetworkInterface) e.nextElement();
                String interfaceName = n.getName();
                if(interfaceName.equals("wlan0") || interfaceName.equals("en0")){
                    intface = n;
                }else if(interfaceName.equals("eth0")){
                    intface = n;
                }else{
                    continue;
                }
                
            }
            if(intface != null){
                Enumeration ee = intface.getInetAddresses();
                while (ee.hasMoreElements())
                {
                    InetAddress i = (InetAddress) ee.nextElement();
                    if(i.getHostAddress().indexOf(":") == -1){
                        selfIp = i.getHostAddress();
                    }
                }
            }
            System.out.println(selfIp);
        }catch(Exception e){
        }
        
        PeerNode self = new PeerNode(selfIp, Integer.parseInt(args[1]));
        self.setIPAddress(selfIp);
        peerList = new ListOfPeers(self);

        /* By pass 2 arguments */
        if(args.length > 2){
            while(count < args.length - 2){
                client.add(args[0]);
                count++;
            }
        }
        
        /* Start UDP client thread. Broadcast IP is hard-coded to "255.255.255.255" for now. Change if needed. */
        Thread udpClient = new Thread(new UdpClient(Integer.parseInt("8886"), "255.255.255.255", client, peerList));
        udpClient.start();
        
        /* Start UDP server thread */
        Thread udpServer = new Thread(new UdpServer(Integer.parseInt("61001"), peerList));
        udpServer.start();
        
        /* Start Sync thread */
        Thread sync = new Thread(new Sync(peerList));
        sync.start();
        
        /* Start a TCP receive thread */
        ServerSocket ss = null;
        
        try
        {
            serverPort = "60010";
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
                Thread server = new Thread(new TcpServer(ss, s,peerList));
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
