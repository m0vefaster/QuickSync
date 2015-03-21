import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.*;
import java.net.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.util.*;
import java.sql.Timestamp;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class TcpServerCloud implements Runnable {
    private Socket s;
    static String homeDir = System.getProperty("user.home");
    static String folder = "QuickSync";
    static String path = homeDir + "/" + folder;
    private ListOfPeers listOfPeers;

    public TcpServerCloud(Socket s, ListOfPeers listOfPeers) {
        try {
            this.s = s;
            this.listOfPeers = listOfPeers;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
         
         
        PeerNode self = listOfPeers.getSelf();

         
        String data = self.getId() + ":" + String.valueOf(self.getWeight());
        JSONObject JSONobj = JSONManager.getJSON(data, "Init");
        self.sendMessage(JSONobj);
        while (true) {
            try {
                JSONObject obj = getMessage();

                 
                if (obj.get("type").equals("Control")) {
                    String str = (String) obj.get("value");
                     
                     
                    File file = new File(path + "/" + str);
                    JSONObject obj2 = JSONManager.getJSON(file);

                    PeerNode node = listOfPeers.getPeerNode(s.getInetAddress().getHostAddress());
                    if (node.isCloud()) {
                        self.sendMessage(obj2);
                         
                    } else {
                        Thread client = new Thread(new TcpClient(s.getInetAddress().getHostAddress(), "60010", obj2, listOfPeers));
                        client.start();
                    }
                } else if (obj.get("type").toString().substring(0, 4).equals("File")) {

                     
                    String fileContent = (String) obj.get("value");
                     
                    String receivedPath = obj.get("type").toString().substring(4);
                    String[] splits = receivedPath.split("/");
                    int noOfSplits = splits.length;
                    String newPath = path;

                    while (noOfSplits > 1) {
                        newPath = newPath + "/" + splits[splits.length - noOfSplits];
                        File theDir = new File(newPath);
                        if (!theDir.exists()) {
                            theDir.mkdir();
                        }
                        noOfSplits--;
                    }

                    File file = new File(path + "/" + receivedPath);
                    file.createNewFile();
                    FileOutputStream fos = new FileOutputStream(file);
                    BufferedOutputStream bos = new BufferedOutputStream(fos);
                    bos.write(fileContent.getBytes());
                    bos.close();
                    SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");

                    final TimeZone utc = TimeZone.getTimeZone("UTC");
                    dateFormatter.setTimeZone(utc);

                    String t = dateFormatter.format(new java.util.Date());
                    System.out.println("_cloud_" + t + "_" + receivedPath);
                } else if (obj.get("type").equals("HashMap")) {
                    HashMap map = (HashMap) obj.get("value");
                    System.out.println("Hashmap size: " + map.size());
                    self.setHashMapFilePeer(map);
                } else {
                     
                }

                 
                 
            } catch (StreamCorruptedException ee) {
                System.out.println("StreamCorruptedException !!!!!!");
                ee.printStackTrace();
            } catch (Exception e) {
                try {
                     
                    if (e instanceof EOFException) {
                        PeerNode nodeToBeRemoved = listOfPeers.getPeerNodeFromIP(s.getInetAddress().getHostAddress());
                        System.out.println("Removing PeerNode:" + nodeToBeRemoved.getId() + ":" + listOfPeers.removePeerNode(nodeToBeRemoved));
                         
                        listOfPeers.getSelf().setSocket(null);
                        s.close();
                        System.out.println("TcpServerCloud: closing cloud socket");
                    }
                    e.printStackTrace();
                    break;
                } catch (Exception ee) {}
            }
             
        }
    }

    JSONObject getMessage() {

        JSONObject obj = null;
        PeerNode self = listOfPeers.getSelf();
        ObjectInputStream in = self.getInputStream();
        try {
            Message obj2 = (Message) in .readObject();
            obj = (JSONObject)(obj2.obj);
        } catch (EOFException e) {
            try {
                System.out.println("*********************" + e.getClass());
                PeerNode nodeToBeRemoved = listOfPeers.getPeerNodeFromIP(s.getInetAddress().getHostAddress());
                System.out.println("Removing PeerNode:" + nodeToBeRemoved.getId() + ":" + listOfPeers.removePeerNode(nodeToBeRemoved));
                 
                listOfPeers.getSelf().setSocket(null);
                s.close();
                System.out.println("TcpServerCloud: closing cloud socket");
                QuickSync.count = 0;
                e.printStackTrace();
            } catch (Exception ee) {}
        } catch (SocketException e) {
            try {
                System.out.println("*********************" + e.getClass());
                PeerNode nodeToBeRemoved = listOfPeers.getPeerNodeFromIP(s.getInetAddress().getHostAddress());
                System.out.println("Removing PeerNode:" + nodeToBeRemoved.getId() + ":" + listOfPeers.removePeerNode(nodeToBeRemoved));
                 
                listOfPeers.getSelf().setSocket(null);
                s.close();
                System.out.println("TcpServerCloud: closing cloud socket");
                QuickSync.count = 0;
                e.printStackTrace();
            } catch (Exception ee) {}
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }



     
}