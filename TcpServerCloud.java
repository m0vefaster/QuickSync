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

public class TcpServerCloud implements Runnable
{
    private Socket s;
    static String homeDir = System.getProperty("user.home");
    static String folder = "QuickSync";
    static String path = homeDir + "/" + folder ;
    private ListOfPeers listOfPeers;
    
    public TcpServerCloud(Socket s, ListOfPeers listOfPeers)
    {
      try {
        this.s = s;
        this.listOfPeers = listOfPeers;
      }
      catch(Exception e)
      {
	      e.printStackTrace();
      }
    }

    @Override
    public void run()
    {
        //System.out.println("TcpServer:run: Socket closed? "+s.isClosed());
        //System.out.println("TcpServer:run: Server running "+s.toString());
        PeerNode self = listOfPeers.getSelf();

        /* Send your Init */
        String data = self.getId() + ":" + String.valueOf(self.getWeight());
        JSONObject JSONobj = JSONManager.getJSON(data, "Init");
        self.sendMessage(JSONobj);
        while(true){
            try {
                JSONObject obj = getMessage();

                //Check for NULL Object
                if(obj.get("type").equals("Control"))
                {
                    String str = (String)obj.get("value");
                    //System.out.println("TcpServer:run: Got an Control Message from:"+s.getInetAddress().toString() + " message: " + str);
                    //Send the file from ...
                    File file= new File(path+"/"+str);
                    JSONObject obj2 = JSONManager.getJSON(file);

                    PeerNode node = listOfPeers.getPeerNode(s.getInetAddress().getHostAddress());
                    if(node.isCloud()){
                        self.sendMessage(obj2);
                        //System.out.println("TcpServerCloud:run:Sending file " + str + " to cloud");
                    }else{
                        Thread client = new Thread(new TcpClient(s.getInetAddress().getHostAddress(), "60010", obj2));
                        client.start();
                    }
                }
                else if(obj.get("type").toString().substring(0,4).equals("File"))
                {
                    
                    //System.out.println("TcpServer:run: Got an File from:"+s.getInetAddress().toString());
                    String fileContent = (String)obj.get("value");
                    //Store this File...
                    String receivedPath = obj.get("type").toString().substring(4);
                    String[] splits = receivedPath.split("/");
                    int noOfSplits = splits.length;
                    String newPath = path;

                    while(noOfSplits > 1){
                        newPath = newPath + "/" + splits[splits.length - noOfSplits];
                        File theDir = new File(newPath);
                        if(!theDir.exists()){
                            theDir.mkdir();
                        }
                        noOfSplits--;
                    }
                    
                    File file = new File(path+"/"+ receivedPath);
                    file.createNewFile();
                    FileOutputStream fos = new FileOutputStream(file);
                    BufferedOutputStream bos = new BufferedOutputStream(fos);
                    bos.write(fileContent.getBytes());
                    bos.close();
			SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
	     
			final TimeZone utc = TimeZone.getTimeZone("UTC");
			dateFormatter.setTimeZone(utc);
	     
			String t = dateFormatter.format(new java.util.Date());
			System.out.println("From cloud" + self.getListOfFiles().getArrayListOfFiles().size() + " " + t);

                }
                else if(obj.get("type").equals("HashMap"))
                {
                    //System.out.println("TcpServer:run: Got an HashMap from:"+s.getInetAddress().toString());
                    HashMap map = (HashMap)obj.get("value");
		    System.out.println("Hashmap size: "+map.size());
                    self.setHashMapFilePeer(map);
                }
                else
                {
                    //System.out.println("TcpServer:run: Got an Invalid Message from:"+s.getInetAddress().toString());
                }
                
                //CLOSE SOCKET HERE
                //s.close();
            }catch(StreamCorruptedException ee){
		System.out.println("StreamCorruptedException !!!!!!");
                ee.printStackTrace();
	    }catch (Exception e) {
                try{
                    //System.out.println("*********************" + e.getClass());
                    if(e instanceof EOFException){
                        PeerNode nodeToBeRemoved = listOfPeers.getPeerNodeFromIP(s.getInetAddress().getHostAddress());
                        System.out.println("Removing PeerNode:" + nodeToBeRemoved.getId() + ":" + listOfPeers.removePeerNode(nodeToBeRemoved));
                        //listOfPeers.printPeerList();
                        listOfPeers.getSelf().setSocket(null);
                        s.close();
                        System.out.println("TcpServerCloud: closing cloud socket");
                    }
                    e.printStackTrace();
                    break;
                }catch(Exception ee)
                {
                }
            }
            System.out.println();        
        }
    }
    
    JSONObject getMessage()
    {
        
        JSONObject obj = null;
        PeerNode self = listOfPeers.getSelf();
        ObjectInputStream in = self.getInputStream();
        try
        {
            Message obj2 = (Message)in.readObject();
            obj = (JSONObject)(obj2.obj);
        }
        catch(EOFException e)
        {
            try{
                    System.out.println("*********************" + e.getClass());
                    PeerNode nodeToBeRemoved = listOfPeers.getPeerNodeFromIP(s.getInetAddress().getHostAddress());
                    System.out.println("Removing PeerNode:" + nodeToBeRemoved.getId() + ":" + listOfPeers.removePeerNode(nodeToBeRemoved));
//                    listOfPeers.printPeerList();
                    listOfPeers.getSelf().setSocket(null);
                    s.close();
                    System.out.println("TcpServerCloud: closing cloud socket");
                    QuickSync.count = 0;
                    e.printStackTrace();
                }catch(Exception ee)
                {
                }
        }
        catch(SocketException e)
        {
            try{
                    System.out.println("*********************" + e.getClass());
                    PeerNode nodeToBeRemoved = listOfPeers.getPeerNodeFromIP(s.getInetAddress().getHostAddress());
                    System.out.println("Removing PeerNode:" + nodeToBeRemoved.getId() + ":" + listOfPeers.removePeerNode(nodeToBeRemoved));
//                    listOfPeers.printPeerList();
                    listOfPeers.getSelf().setSocket(null);
                    s.close();
                    System.out.println("TcpServerCloud: closing cloud socket");
                    QuickSync.count = 0;
                    e.printStackTrace();
                }catch(Exception ee)
                {
                }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return obj;
    }

       
    
    /*
    void find(int x)
    {
        System.out.println("========Inside find" + x + "===========");
        Iterator<PeerNode> it = peerList.getList().iterator();
        while (it.hasNext())
        {
            PeerNode peerNode = it.next();
            ArrayList<String> lof = peerNode.getListOfFiles().getList();
            System.out.println("For peer node:"+peerNode.getId()+" list of files is:"+lof.toString());
        }
        System.out.println("========Leaving find()===========");
    }
    */
}
