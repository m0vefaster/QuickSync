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

public class TcpServer implements Runnable
{
    private ServerSocket ss;
    private Socket s;
    ListOfPeers peerList;
    static String homeDir = System.getProperty("user.home");
    static String folder = "QuickSync";
    static String path = homeDir + "/" + folder ;
    
    public TcpServer(ServerSocket ss, Socket s, ListOfPeers peerList)
    {
        this.ss = ss;
        this.s = s;
        this.peerList = peerList;
    }

    @Override
    public void run()
    {
        int count =0;
        InputStream inFromServer = null;
        ObjectInputStream in = null;
        System.out.println("TcpServer:run: Server running "+s.toString());
        try{
            inFromServer = s.getInputStream();
            in = new ObjectInputStream(inFromServer);
        }catch(Exception e){
            e.printStackTrace();
        }
        while(true){
            try {
                JSONObject obj = getMessage(s, in);

                //Check for NULL Object
                if(obj.get("type").equals("Control"))
                {
                    System.out.println("TcpServer:run: Got an Control Message from:"+s.getInetAddress().toString());
                    String str = (String)obj.get("value");
                    //Send the file from ...
                    File file= new File(path+"/"+str);
                    JSONObject obj2 = JSONManager.getJSON(file);
                    Thread client = new Thread(new TcpClient(s.getInetAddress().getHostAddress(), "60010", obj2, peerList));
                    client.start();
                    break;
                }
                else if(obj.get("type").toString().substring(0,4).equals("File"))
                {
                    
                    String fileContent = (String)obj.get("value");
                    //Store this File...
                    String receivedPath = obj.get("type").toString().substring(4);
                    System.out.println("TcpServer:run: Got an File " + receivedPath + " from:"+s.getInetAddress().toString());
                    /* Remove the file from in-transit hashset */
                    if(!peerList.removeFileInTransit(receivedPath)){
                        System.out.println("Error!!! File not found in hash set. Something is wrong");
                    }

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
                    //java.util.Date date= new java.util.Date();
                    //Timestamp t = new Timestamp(date.getTime());
                    SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
         
                    final TimeZone utc = TimeZone.getTimeZone("UTC");
                    dateFormatter.setTimeZone(utc);
         
                    String t = dateFormatter.format(new java.util.Date());
                    System.out.println("From peer " + peerList.getSelf().getListOfFiles().getArrayListOfFiles().size() + " " + t + " ----- peer IP -- :" + s.getInetAddress().getHostAddress()+ " File -- " + receivedPath);
                    break;

                }
                else if(obj.get("type").equals("ArrayList"))
                {
                    System.out.println("TcpServer:run: Got an ArrayList from:"+s.getInetAddress().toString());
                    ArrayList list = (ArrayList)obj.get("value");
                    //Uodate the peerList peerNode list of files
                    PeerNode peerNode = peerList.getPeerNodeFromIP(s.getInetAddress().getHostAddress());
                    
                    if(peerNode ==null)
                    {
                        //System.out.println("TcpServer:run: \nCouldn't find the PeerNode");
                    }
                    else
                    {
                        ListOfFiles lof= new ListOfFiles(list);
                        peerNode.setListOfFiles(lof);
                    }
                }
                else if(obj.get("type").equals("HashMap"))
                {
                    System.out.println("TcpServer:run: Got an HashMap from:"+s.getInetAddress().toString());
                    HashMap map = (HashMap)obj.get("value");
                    peerList.getSelf().setHashMapFilePeer(map);
                    break;
                }
                else if(obj.get("type").equals("ArrayListFiles"))
                {
                    System.out.println("TcpServer:run: Got an ArrayListFile from:"+s.getInetAddress().toString());
                    ArrayList<String> fileArray = (ArrayList<String>)obj.get("value");
                    //Store this File...
                    Thread client = new Thread(new TcpClient(s.getInetAddress().getHostAddress(), "60010", fileArray, false, peerList));
                    client.start();
                    break;
                }
                else if(obj.get("type").equals("EOF")){
                    System.out.println("File EOF received from: " + s.getInetAddress().toString());
                    break;
                }else
                {
                    break;
                    //System.out.println("TcpServer:run: Got an Invalid Message from:"+s.getInetAddress().toString());
                }
                
                //CLOSE SOCKET HERE
                //s.close();
        }catch(StreamCorruptedException ee){
                ee.printStackTrace();
         }catch (Exception e) {
                try{
                    s.close();
                    break;
                    //System.out.println("TcpServer:run: closing socket "+s.toString());
                e.printStackTrace();}
                catch(Exception ee)
                {
                }
            }
            //System.out.println();        
        }
        s.close();
    }
    
    JSONObject getMessage(Socket s, ObjectInputStream in)
    {
        
        JSONObject obj = null;
        try
        {
            int length = (int)in.readObject();
            byte[] inputArray = new byte[length];
            inputArray = (byte[])in.readObject();
            String line = new String(inputArray);
            obj = (JSONObject)(JSONManager.convertStringToJSON(line));
        }
        catch(Exception e)
        {
            //e.printStackTrace();
        }
        return obj;
    }
    
    
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
}
