import java.net.*;
import java.io.*;
import java.lang.*;
import java.security.*;
import java.util.*;

public class udpClient implements Runnable
{
    private DatagramSocket clientSocket;
    private String broadcastAdd;
    private int port;

    udpClient(int port, String broadcastAdd){
        System.out.println("Starting UDP client on port" + port);
        try{
            //this.clientSocket = new DatagramSocket(port, InetAddress.getByName("137.110.90.255"));
            this.clientSocket = new DatagramSocket();
            this.clientSocket.setBroadcast(true);
        }catch(Exception e){
            e.printStackTrace();
        }
        this.broadcastAdd = broadcastAdd;
        this.port = port;
    }

    void sendUdpPacket(byte[] data, InetAddress remoteIp){
        try{
            DatagramPacket packet = new DatagramPacket(data, data.length, remoteIp, this.port);
            this.clientSocket.send(packet);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    void broadcastUdpPacket(byte[] data, String ip){
        /*
        try{
            Enumeration interfaces = NetworkInterface.getNetworkInterfaces();
            while(interfaces.hasMoreElements()){
                NetworkInterface networkInterface = (NetworkInterface)interfaces.nextElement();
                if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                    continue; // Don't want to broadcast to the loopback interface
                }
                for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                    InetAddress broadcast = interfaceAddress.getBroadcast();
                    System.out.println(broadcast);
                    if (broadcast == null) {
                        continue;
                    }
                    try{
                        DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName(broadcastAdd), 61001);
                        //DatagramPacket packet = new DatagramPacket(data, data.length, broadcast, 61001);
                        this.clientSocket.send(packet);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    //System.out.print("-----Broadcasting to "+ broadcastAdd);
                }
            }
        }catch(Exception e){
        }
        */
        try{
                        //DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName(broadcastAdd), 61001);
                        DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName(ip), 61001);
                        //DatagramPacket packet = new DatagramPacket(data, data.length, broadcast, 61001);
                        this.clientSocket.send(packet);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
    }
    
    public void run(){
        /* Start a udp server */
        int i ;
        byte[] buf = new byte[100];
        Thread server = new Thread(new udpServer(61001));
        server.start();

        try{
            System.out.println("Local Add "+ clientSocket.getInetAddress());
            //String ipPort = this.clientSocket.getLocalAddress()+":"+this.port;
            StringBuilder a = new StringBuilder();
            a.append("blah blah");
            a.append(String.valueOf(port));
            String ipPort = "blah blah";
            byte[] bytes = ipPort.getBytes("UTF-8");
            System.out.println("Created stream");
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(bytes);

            PeerNode host = new PeerNode(digest, 1); 

            ByteArrayOutputStream b = new ByteArrayOutputStream();
            ObjectOutputStream o = new ObjectOutputStream(b);
            o.writeObject(host);
            buf = b.toByteArray();
            System.out.println("Created digest");
        }catch(Exception e){
            e.printStackTrace();
        }


        /* Send Broadcast info */
        while(true){
            //for(i = 1; i <= 255; i++){
                broadcastUdpPacket(buf, ClientServerGen.client1);
   
   				broadcastUdpPacket(buf, ClientServerGen.client2);
           try {
                  Thread.sleep(5000); //milliseconds

            } catch (InterruptedException e){
                e.printStackTrace();
            } 
     

		 	}
    }
}
