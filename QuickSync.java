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
    public static String cloudIP;
    public static boolean isCloud=false;

    public static void main(String[] args){
        
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
            System.out.println("Self IP is:"+selfIp);
        }catch(Exception e){
        }
        
        

        PeerNode self = new PeerNode(selfIp, Integer.parseInt(args[1]));
        self.setIPAddress(selfIp);
        peerList = new ListOfPeers(self);

        client1 = args[0];
        cloudIP= args[2];


        if(cloudIP.equals(selfIp))
        {
           System.out.println("I am the cloud");
           isCloud = true; 
        }

        /* Start UDP client thread */
        Thread udpClient = new Thread(new UdpClient(Integer.parseInt("8886"), "10.10.10.10", args[0], peerList));
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
                InetAddress cloudInetAddress = InetAddress.getByName(cloudIP);
                if(!isCloud && cloudInetAddress.isReachable(1000))
                {
                    System.out.println("=========================Adding Cloud to Peer List");
                    peerList.addPeerNode(new PeerNode(cloudIP,0)); 
                }
                else if (!isCloud )
                {
                    System.out.println("==========================Removing Cloud to Peer List");
                    peerList.removePeerNode(cloudIP);
                }
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
