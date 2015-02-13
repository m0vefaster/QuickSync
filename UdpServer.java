import java.net.*;
import java.io.*;
import java.lang.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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
        String data = new String();
        DatagramPacket recvPacket = new DatagramPacket(recvBuf, recvBuf.length);

        /* Start listening on the UDP server port */
        while(true){
            try{
                this.serverSocket.receive(recvPacket);

                if(recvPacket.getAddress().getHostAddress().toString().compareTo(peerList.getSelf().getId()) == 0 ||
                        recvPacket.getAddress().getHostAddress().toString().compareTo("127.0.0.1") == 0){
                    continue;
                }

                /* Check if it is from the same client. Parse peerList */
                if(peerList.getPeerNode(recvPacket.getAddress().getHostAddress()) != null){
                    break;
                }

                ByteArrayInputStream b = new ByteArrayInputStream(recvPacket.getData());
                //ObjectInputStream o = new ObjectInputStream(b);
                //String data = (String)o.readObject();
                JSONObject JSONobj = (JSONObject)(JSONManager.convertStringToJSON(b.toString()));
                if(JSONobj.get("type").equals("control")){
                    data = (String)JSONobj.get("value");
                }

                String[] components = data.split(":");

                PeerNode peer = new PeerNode(components[0], Integer.parseInt(components[1]));

                /* Store the sender info in the linked list */
                peerList.addPeerNode(peer);
                System.out.println("Added to peer list size " + recvPacket.getAddress().getHostAddress());
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}
