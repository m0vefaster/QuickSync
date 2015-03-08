import java.net.*;
import java.io.*;
import java.lang.*;
import java.util.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
public class TcpClientCloud implements Runnable
{
    
    String serverName ;
    int port;
    private Thread t;
    private String threadName = "ClientToCloud";
    private ListOfPeers listOfPeers;
    OutputStream outToServer = null;
    ObjectOutputStream out = null;
    Socket client = null;

    TcpClientCloud (String serverName, String port, ListOfPeers listOfPeers)
    {
        
        this.serverName = serverName;
        this.port = Integer.parseInt(port);
        this.listOfPeers = listOfPeers;
    }
    
    public void run()
    {
        File myFile;
        String file;
        int count = 0;
        PeerNode self = listOfPeers.getSelf();
        
        try
        {
            client = null;
            do
            {
                try
                {
                    client = new Socket(serverName, port);
		    outToServer = client.getOutputStream();
		    out = new ObjectOutputStream(outToServer);
                }
                catch (Exception anye)
                {
                    try
                    {
                        t.sleep(100);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }while(client==null);
            //System.out.println("Client:Just connected to cloud" + client.getRemoteSocketAddress());

            //System.out.println("\nQuickSync:main:Adding Cloud to Peer List");
            PeerNode cloudNode = new PeerNode(serverName, serverName, 0);
            cloudNode.setIsCloud(true);
            listOfPeers.addPeerNode(cloudNode);

            self.setSocket(client);

            String data = self.getId() + ":" + String.valueOf(self.getWeight());
            JSONObject JSONobj = JSONManager.getJSON(data, "Init");
            sendMessage(JSONobj);
            //System.out.println("Client:Sent Init to cloud");
            //System.out.println("TcpCient:run: Socket closed? "+ client.isClosed());
            
            /* Start TCP server for cloud */
            Thread fromCloud = new Thread(new TcpServerCloud(client, listOfPeers));
            fromCloud.start();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            try{
                /*
                PeerNode nodeToBeRemoved = listOfPeers.getPeerNodeFromSocket(client);
                System.out.println("Removing PeerNode:" + nodeToBeRemoved.getId() + ":" + listOfPeers.removePeerNode(nodeToBeRemoved));
                listOfPeers.printPeerList();
                client.close();
                */
                System.out.println("TcpClientCloud: Exiting clientCloud thread");
            }
            catch (Exception ee)
            {
            }
        }
        System.out.println();
    }
    
    void start ()
    {
        System.out.println("TcpClient:start: Starting " +  threadName );
        if (t == null)
        {
            t = new Thread (this, threadName);
            t.start ();
        }
    }

    void sendMessage(JSONObject obj)
    {
        if(client == null){
            return;
        }
        try
        {
            byte[] outputArray = obj.toString().getBytes();
            int len = obj.toString().length();
            out.writeObject(len);
            out.writeObject(outputArray);
            //client.shutdownOutput();
            //out.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
