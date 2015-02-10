import java.net.*;
import java.io.*;
import java.lang.*;

public class udpServer implements Runnable
{
    private DatagramSocket serverSocket;
    private int port;

    udpServer(int port){
        try{
            this.serverSocket = new DatagramSocket(port);
            this.serverSocket.setBroadcast(true);
        }catch(Exception e){
            e.printStackTrace();
        }
        this.port = port;
    }

    public void run(){
        byte[] recvBuf = new byte[15000];
        DatagramPacket recvPacket = new DatagramPacket(recvBuf, recvBuf.length);

        /* Start listening on the UDP server port */
        while(true){
            try{
                this.serverSocket.receive(recvPacket);
                System.out.println("Received UDP packet from" + recvPacket.getAddress().getHostAddress() + " " + recvPacket.getPort());

                ByteArrayInputStream b = new ByteArrayInputStream(recvPacket.getData());
                ObjectInputStream o = new ObjectInputStream(b);
                PeerNode peer = (PeerNode)o.readObject();

                /* Store the sender info in the linked list */
                ClientServerGen.peerList.add(peer);
            }catch(Exception e){
                e.printStackTrace();
            }

            /* Start the client TCP */
            Thread client = new Thread(new Client(recvPacket.getAddress().getHostAddress(), ClientServerGen.serverPort, ClientServerGen.filename));
            client.start();
            System.out.println("Created client TCP connection");
        }
    }
}
