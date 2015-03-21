import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.util.*;
import java.sql.Timestamp;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class TcpServer implements Runnable {
    private ServerSocket ss;
    private Socket s;
    ListOfPeers peerList;
    static String homeDir = System.getProperty("user.home");
    static String folder = "QuickSync";
    static String path = homeDir + "/" + folder;
    boolean isFileSocket = false;
    PeerNode peerNode;  
    String peerId;
    public TcpServer(ServerSocket ss, Socket s, ListOfPeers peerList) {
        this.ss = ss;
        this.s = s;
        this.peerList = peerList;
        peerNode = peerList.getPeerNodeFromIP(s.getInetAddress().getHostAddress());
        peerId = peerNode.getId();
    }

    @Override
    public void run() {
        int count = 0;
        InputStream inFromServer = null;
        ObjectInputStream in = null;
         
        try {
            inFromServer = s.getInputStream(); in = new ObjectInputStream(inFromServer);
            while (!s.isClosed()) {
                JSONObject obj = getMessage(s, in );

                 
                if (obj == null) {
                    System.out.println("obj null!!!!!!!!!!!!!!!!");
                } else if (obj.get("type").equals("Control")) {
                     
                    String str = (String) obj.get("value");
                     
                    File file = new File(path + "/" + str);
                    JSONObject obj2 = JSONManager.getJSON(file);
                    Thread client = new Thread(new TcpClient(s.getInetAddress().getHostAddress(), "60010", obj2, peerList));
                    client.start();
                    break;
                } else if (obj.get("type").toString().substring(0, 4).equals("File")) {
                    isFileSocket = true;
                    String fileContent = (String) obj.get("value");
                     
                    String receivedPath = obj.get("type").toString().substring(4);
                     
                     
                    if (!peerList.removeFileInTransit(receivedPath, peerNode.getId())) {
                        System.out.println("Error!!! File not found in hash set. Something is wrong");
                    }

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
                    System.out.println("_" + peerNode.getId() + "_" + t + "_" + receivedPath);
                } else if (obj.get("type").equals("ArrayList")) {
                     
                    ArrayList list = (ArrayList) obj.get("value");
                     

                    if (peerNode == null) {
                         
                    } else {
                        ListOfFiles lof = new ListOfFiles(list);
                        peerNode.setListOfFiles(lof);
                    }
                    break;
                } else if (obj.get("type").equals("HashMap")) {
                    System.out.println("TcpServer:run: Got an HashMap from ***********:" + s.getInetAddress().toString());
                    HashMap map = (HashMap) obj.get("value");
                    peerList.getSelf().setHashMapFilePeer(map);
                    break;
                } else if (obj.get("type").equals("ArrayListFiles")) {
                     
                    ArrayList < String > fileArray = (ArrayList < String > ) obj.get("value");
                     
                    Thread client = new Thread(new TcpClient(s.getInetAddress().getHostAddress(), "60010", fileArray, false, peerList));
                    client.start();
                    break;
                } else if (obj.get("type").equals("EOFFileList")) {
                     
                    break;
                } else {
                    break;
                     
                }
            }
             
             
            s.close();
        } catch (StreamCorruptedException ee) {
             
            ee.printStackTrace();
        } catch (Exception e) {
            try {
                 
                s.close();
                e.printStackTrace();
            } catch (Exception ee) {}
        } finally {
            if (isFileSocket) peerList.syncMap("", peerId, "clearForPeer");
        }

         
    }

    JSONObject getMessage(Socket s, ObjectInputStream in ) {

        JSONObject obj = null;
        try {
             
            Message obj2 = (Message) in .readObject();
            obj = (JSONObject)(obj2.obj);
        } catch (Exception e) {
             
            e.printStackTrace();
            try {
                s.close();
            } catch (Exception ee) {}
        }
        return obj;
    }


    void find(int x) {
         
        Iterator < PeerNode > it = peerList.getList().iterator();
        while (it.hasNext()) {
            PeerNode peerNode = it.next();
            ArrayList < String > lof = peerNode.getListOfFiles().getList();
             
        }
         
    }
}