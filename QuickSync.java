import java.io.IOException;
import java.io.*;
import java.net.*;
import java.lang.*;
import java.util.*;
import javax.swing.JOptionPane;
import java.sql.Timestamp;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class QuickSync {
    public static String selfIp;
    public static ListOfPeers peerList;
    public static Sync sync;
    public static String serverPort;
    public static String cloudIP;
    public static String hostName;
    public static boolean isCloud = false;
    public static int count;

    public static void main(String[] args) {

        InetAddress cloudInetAddress = null;
        System.setProperty("java.net.preferIPv4Stack", "true");

        ArrayList < String > client = new ArrayList < String > ();
        try {
            Enumeration e = NetworkInterface.getNetworkInterfaces();
            NetworkInterface intface = null;
            while (e.hasMoreElements()) {
                NetworkInterface n = (NetworkInterface) e.nextElement();
                String interfaceName = n.getName();
                if (interfaceName.equals("wlan0") || interfaceName.equals("en0")) {
                    intface = n;
                } else if (interfaceName.equals("eth0")) {
                    intface = n;
                } else {
                    continue;
                }

            }
            if (intface != null) {
                Enumeration ee = intface.getInetAddresses();
                while (ee.hasMoreElements()) {
                    InetAddress i = (InetAddress) ee.nextElement();
                    if (i.getHostAddress().indexOf(":") == -1) {
                        selfIp = i.getHostAddress();
                    }
                }
            }
             
        } catch (Exception e) {}


        try {
            Random rand = new Random();
             
            hostName = args[0];
            cloudIP = InetAddress.getByName("ec2-52-10-100-25.us-west-2.compute.amazonaws.com").getHostAddress();  
            Integer weight = Integer.parseInt(args[1]);
             
            PeerNode self = new PeerNode(hostName, selfIp, weight);  
            peerList = new ListOfPeers(self);
            peerList.setOffset(Integer.parseInt(args[1]));
            System.out.println("Node Details:\n" + hostName + "\n" + cloudIP + "\n" + weight + "\n\n");
        } catch (Exception e) {
            e.printStackTrace();
        }



         

         
         
        if (!isCloud) {
             
            Thread udpClient = new Thread(new UdpClient(Integer.parseInt("8886"), "255.255.255.255", client, peerList));
             
             
            udpClient.start();

             
            Thread udpServer = new Thread(new UdpServer(Integer.parseInt("61001"), peerList));
            udpServer.start();
        }
         
        Thread sync = new Thread(new Sync(peerList));
        sync.start();

         

         
        ServerSocket ss = null;

        try {
            serverPort = "60010";
            ss = new ServerSocket(Integer.parseInt(serverPort));
            ss.setSoTimeout(2000);
        } catch (Exception e) {

        }
        Socket s = null;

        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        final TimeZone utc = TimeZone.getTimeZone("UTC");
        dateFormatter.setTimeZone(utc);
        String t = dateFormatter.format(new java.util.Date());
        System.out.println("Started at " + t);

        count = 0;
         
        while (true) {
            try {
                cloudInetAddress = InetAddress.getByName(cloudIP);
                peerList.printPeerList();
                if (!isCloud && cloudInetAddress.isReachable(1000) && peerList.getPeerNodeFromIP(cloudIP) == null && count == 0) {
                     
                     
                    Thread toCloudClient = new Thread(new TcpClientCloud(cloudIP, "60011", peerList));
                    toCloudClient.start();
                    count = 1;
                }
                s = ss.accept();
                 
                Thread server = new Thread(new TcpServer(ss, s, peerList));
                 
                server.start();
            } catch (Exception e) {
                try {
                    s.close();
                } catch (Exception e1) {}
            }
        }
    }

    static String getCloudIp() {
        return cloudIP;
    }
}

class Comp implements Comparator < PeerNode > {

    @Override
    public int compare(PeerNode pn1, PeerNode pn2) {
        if (pn1.getWeight() > pn2.getWeight()) return 1;
        return -1;
    }
}