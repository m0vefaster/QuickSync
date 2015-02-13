import java.net.*;
import java.io.*;
import java.lang.*;

public class UdpServer implements Runnable
{
    private DatagramSocket serverSocket;
    private int port;
    private ListOfPeers peerList;

    UdpServer(int port, ListOfPeers peerList){
        try{
            this.serverSocket = new DatagramSocket(port);
            this.serverSocket.setBroadcast(true);
            InetAddress addr = InetAddress.getLocalHost();
            String ipAddress = addr.getHostAddress(); 
            this.peerList = peerList;
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

                /* Check if it is from the same client. Parse peerList */
                if(peerList.getPeerNode(recvPacket.getAddress().getHostAddress()) != null){
                    break;
                }

                ByteArrayInputStream b = new ByteArrayInputStream(recvPacket.getData());
                ObjectInputStream o = new ObjectInputStream(b);
                PeerNode peer = (PeerNode)o.readObject();

                /* Store the sender info in the linked list */
                peerList.addPeerNode(peer);
                System.out.println("Added to peer list size ");
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}
