import java.net.*;
import java.io.*;
import java.lang.*;
import java.security.*;
public class udpClient implements Runnable
{
    private DatagramSocket clientSocket;
    private String broadcastAdd;
    private int port;

    udpClient(int port, String broadcastAdd){
        try{
            this.clientSocket = new DatagramSocket(port);
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

    void broadcastUdpPacket(byte[] data){
        try{
            DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName(broadcastAdd), this.port);
            this.clientSocket.send(packet);
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void run(){
        /* Start a udp server */
        byte[] buf = new byte[10000];
        Thread server = new Thread(new udpServer(8888));
        server.start();

        try{
            String ipPort = clientSocket.getLocalAddress()+":"+this.port;
            byte[] bytes = ipPort.getBytes(ipPort);
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(bytes);

            peerNode host = new peerNode(digest, 1); 

            ByteArrayOutputStream b = new ByteArrayOutputStream();
            ObjectOutputStream o = new ObjectOutputStream(b);
            o.writeObject(host);
            buf = b.toByteArray();
        }catch(Exception e){
            e.printStackTrace();
        }


        /* Send Broadcast info */
        while(true){
            broadcastUdpPacket(buf);
            try {
                Thread.sleep(100); //milliseconds
            } catch (InterruptedException e){
            } 
        }
    }
}
