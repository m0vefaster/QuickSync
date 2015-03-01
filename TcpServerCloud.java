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

public class TcpServerCloud implements Runnable
{
    private Socket s;
    static String homeDir = System.getProperty("user.home");
    static String folder = "QuickSync";
    static String path = homeDir + "/" + folder ;
    private ListOfPeers listOfPeers;
    
    public TcpServerCloud(Socket s, ListOfPeers listOfPeers)
    {
        this.s = s;
        this.listOfPeers = listOfPeers;
    }

    @Override
    public void run()
    {
        System.out.println("TcpServer:run: Socket closed? "+s.isClosed());
        System.out.println("TcpServer:run: Server running "+s.toString());
        PeerNode self = listOfPeers.getSelf();
        while(true){
            try {
                JSONObject obj = getMessage(s);

                //Check for NULL Object
                if(obj.get("type").equals("Control"))
                {
                    String str = (String)obj.get("value");
                    System.out.println("TcpServer:run: Got an Control Message from:"+s.getInetAddress().toString() + " message: " + str);
                    //Send the file from ...
                    File file= new File(path+"/"+str);
                    JSONObject obj2 = JSONManager.getJSON(file);

                    PeerNode node = listOfPeers.getPeerNode(s.getInetAddress().getHostAddress());
                    if(node.isCloud()){
                        sendMessage(obj2, self.getSocket());
                        System.out.println("TcpServerCloud:run:Sending file " + str + " to cloud");
                    }else{
                        Thread client = new Thread(new TcpClient(s.getInetAddress().getHostAddress(), "60010", obj2));
                        client.start();
                    }
                }
                else if(obj.get("type").toString().substring(0,4).equals("File"))
                {
                    
                    System.out.println("TcpServer:run: Got an File from:"+s.getInetAddress().toString());
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
                }
                else if(obj.get("type").equals("HashMap"))
                {
                    System.out.println("TcpServer:run: Got an HashMap from:"+s.getInetAddress().toString());
                    HashMap map = (HashMap)obj.get("value");
                    self.setHashMapFilePeer(map);
                }
                else
                {
                    System.out.println("TcpServer:run: Got an Invalid Message from:"+s.getInetAddress().toString());
                }
                
                //CLOSE SOCKET HERE
                //s.close();
            }catch (Exception e) {
                try{
                    //s.close();
                    //System.out.println("TcpServer:run: closing socket "+s.toString());
                    e.printStackTrace();
                }catch(Exception ee)
                {
                }
            }
            System.out.println();        
        }
    }
    
    JSONObject getMessage(Socket s)
    {
        
        JSONObject obj = null;
        try
        {
            InputStream inFromServer = s.getInputStream();
            ObjectInputStream in = new ObjectInputStream(inFromServer);
            int length = (int)in.readObject();
            byte[] inputArray = new byte[length];
            inputArray = (byte[])in.readObject();
            String line = new String(inputArray);
            obj = (JSONObject)(JSONManager.convertStringToJSON(line));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return obj;
    }

     void sendMessage(JSONObject obj, Socket client)
    {
        if(client == null){
            return;
        }
        try
        {
            OutputStream outToServer = client.getOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(outToServer);
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
