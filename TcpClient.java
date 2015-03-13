import java.net.*;
import java.io.*;
import java.lang.*;
import java.util.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
public class TcpClient implements Runnable
{
    
    String serverName ;
    int port;
    private Thread t;
    private String threadName = "Client";
    private JSONObject obj;
    private ArrayList<String> fileList;
    private boolean seeking;
    static String homeDir = System.getProperty("user.home");
    static String folder = "QuickSync";
    static String path = homeDir + "/" + folder ;
    private ListOfPeers listOfPeers;

    TcpClient (String serverName, String port, JSONObject obj, ListOfPeers listOfPeers)
    {
        this.serverName = serverName;
        this.port = Integer.parseInt(port);
        this.obj = obj;
        this.listOfPeers = listOfPeers;
    }
    
    TcpClient (String serverName, String port, ArrayList<String> fileList, boolean seeking, ListOfPeers listOfPeers)
    {
        this.serverName = serverName;
        this.port = Integer.parseInt(port);
        this.fileList = fileList;
        this.seeking = seeking;
        this.obj = null;
        this.listOfPeers = listOfPeers;
    }

    public void run()
    {
        Socket client=null;
        int count =0;
        
        try
        {
            System.out.println("TcpClient:run: Connecting to " + serverName + " on port " + port);
            client =null ;
            do
            {
                try
                {
                    client = new Socket(serverName, port);
                }
                catch (Exception anye)
                {
                    try
                    {
                        t.sleep(100); //milliseconds
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    //anye.printStackTrace();
                }
            }while(client==null);
            System.out.println("TcpClient:run: Client:Just connected to " + client.getRemoteSocketAddress());

            OutputStream outToServer = client.getOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(outToServer);
            if(obj == null){
                if(seeking == true){
                    System.out.println("TcpClient:run: Sending fileList");
                    /* Send a list of files to be sought. Put a randomized or a part of file list */
                    ArrayList<String> filesToAsk = fileList;
                    /* Send the array list to List of peer to add and get a filtered array list */
                    filesToAsk = listOfPeers.addFilesInTransit(filesToAsk, listOfPeers.getPeerNodeFromIP(serverName).getId());
                    if(filesToAsk != null){
                        JSONObject obj1 = JSONManager.getJSON(filesToAsk, 1);
                        sendMessage(obj1, client, out);
                    }
                }else{
                    System.out.println("TcpClient:run: Sending files to the remote****************"+ fileList.size());
                    Iterator<String> itr = fileList.iterator();
                    
                    while(itr.hasNext() &&  !client.isClosed()){
                        String str = itr.next();
                        //Send the file from ...
                        File file= new File(path+"/"+str);
                        JSONObject obj2 = JSONManager.getJSON(file);
                        sendMessage(obj2, client, out);
                    }
                    System.out.println("Sending EOF....");
                    JSONObject obj3 = JSONManager.getJSON("", "EOFFileList");
                    sendMessage(obj3, client, out);
                }
            }else{
                System.out.println("TcpClient:run: Sending single message to remote");
                sendMessage(obj, client, out);
            }
               
            out.close();
            client.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            try{
                client.close();
            }
            catch (Exception ee)
            {
            }
        }
        //System.out.println();
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

    void sendMessage(JSONObject obj, Socket client, ObjectOutputStream out)
    {
        try
        {
            Message msg = new Message(obj);
            out.writeObject(msg);
            //out.flush();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            try{
                client.close();
            }catch(Exception ee){
            }
        }
    }
}
