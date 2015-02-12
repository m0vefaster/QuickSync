import java.net.*;
import java.io.*;
import java.lang.*;

public class udpServer implements Runnable
{
    private DatagramSocket serverSocket;
    private int port;
    private ConnectionTable table;

    udpServer(int port){
        try{
            this.serverSocket = new DatagramSocket(port);
            this.serverSocket.setBroadcast(true);
            InetAddress addr = InetAddress.getLocalHost();
            String ipAddress = addr.getHostAddress(); 
            table = new ConnectionTable();
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
       //         System.out.print(".");
                if(recvPacket.getAddress().getHostAddress().toString().compareTo(ClientServerGen.selfIp) == 0){
                    //System.out.println("------Moving on---------------" );
                    continue;
                }
                if(table.existsConnection(recvPacket.getAddress().getHostAddress(), ClientServerGen.selfIp, recvPacket.getPort(), port) == true){
                    continue;
                }

                ByteArrayInputStream b = new ByteArrayInputStream(recvPacket.getData());
                ObjectInputStream o = new ObjectInputStream(b);
                PeerNode peer = (PeerNode)o.readObject();
                if(table.addConnection(recvPacket.getAddress().getHostAddress(), ClientServerGen.selfIp, recvPacket.getPort(), port, peer) == false){
                    System.out.println("Can't insert");
                    continue;
                }


                /* Store the sender info in the linked list */
                ClientServerGen.peerList.addPeerNode(peer);
            }catch(Exception e){
                e.printStackTrace();
            }

            /* Start the client TCP */
            /*Thread client = new Thread(new Client(recvPacket.getAddress().getHostAddress(), ClientServerGen.serverPort, ClientServerGen.filename));
            client.start();
            System.out.println("Created client TCP connection");*/
        }
    }
}
