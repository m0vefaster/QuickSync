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
        System.out.println("Starting UDP client on port" + port);
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
            DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName(broadcastAdd), 8888);
            this.clientSocket.send(packet);
            System.out.print("-----Broadcasting to "+ broadcastAdd);
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void run(){
        /* Start a udp server */
        byte[] buf = new byte[100];
        Thread server = new Thread(new udpServer(8888));
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
            broadcastUdpPacket(buf);
            try {
                Thread.sleep(100); //milliseconds
            } catch (InterruptedException e){
            } 
        }
    }
}
