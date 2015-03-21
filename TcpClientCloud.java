import java.net.*;
import java.io.*;
import java.lang.*;
import java.util.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
public class TcpClientCloud implements Runnable {

    String serverName;
    int port;
    private Thread t;
    private String threadName = "ClientToCloud";
    private ListOfPeers listOfPeers;
    Socket client = null;

    TcpClientCloud(String serverName, String port, ListOfPeers listOfPeers) {

        this.serverName = serverName;
        this.port = Integer.parseInt(port);
        this.listOfPeers = listOfPeers;
    }

    public void run() {
        File myFile;
        String file;
        int count = 0;
        PeerNode self = listOfPeers.getSelf();

        try {
            client = null;
            do {
                try {
                    client = new Socket(serverName, port);
                } catch (Exception anye) {
                    try {
                        t.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } while (client == null);
             

             
            PeerNode cloudNode = new PeerNode(serverName, serverName, 0);
            cloudNode.setIsCloud(true);
            listOfPeers.addPeerNode(cloudNode);

            self.setSocket(client);

             
            OutputStream outToServer = client.getOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(outToServer);
            InputStream inFromServer = client.getInputStream();
            ObjectInputStream in = new ObjectInputStream(inFromServer);

            self.setOutputStream(out);
            self.setInputStream( in );


             
             

             
            Thread fromCloud = new Thread(new TcpServerCloud(client, listOfPeers));
            fromCloud.start();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                 
                System.out.println("TcpClientCloud: Exiting clientCloud thread");
            } catch (Exception ee) {}
        }
         
    }

    void start() {
         
        if (t == null) {
            t = new Thread(this, threadName);
            t.start();
        }
    }

     
}