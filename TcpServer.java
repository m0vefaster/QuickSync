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
        System.out.println("Server running "+s.toString());
        try {

              JSONObject obj = getMessage(s); 
              //Check for NULL Object
              if(obj.get("type").equals("Control"))
              {
                System.out.println("Got an Control Message from:"+s.getInetAddress().toString()); 
                String str = (String)obj.get("value");  
                //Send the file from ...
                File file= new File(path+"/"+str);
                JSONObject obj2 = JSONManager.getJSON(file);
                Thread client = new Thread(new TcpClient(s.getInetAddress().getHostAddress(), "60010", obj2
                  ));
                client.start();
              }
              else if(obj.get("type").toString().substring(0,4).equals("File"))
              {

                 System.out.println("Got an File from:"+s.getInetAddress().toString()); 
                String fileContent = (String)obj.get("value");
                //Store this File...
                File file = new File(path+"/"+ obj.get("type").toString().substring(4));
                //...Need the File Name
                file.createNewFile();
                FileOutputStream fos = new FileOutputStream(file);
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                bos.write(fileContent.getBytes());
                bos.close();

                //File file = new File(path+"newFileNeedName");	
                //Cannnnnt proceed

              }
              else if(obj.get("type").equals("ArrayList"))
              {
                 System.out.println("Got an ArrayList from:"+s.getInetAddress().toString()); 
                ArrayList list = (ArrayList)obj.get("value");
                //Uodate the peerList peerNode list of files
                PeerNode peerNode = peerList.getPeerNodeFromIP(s.getInetAddress().getHostAddress());

                if(peerNode ==null)
                {
                   System.out.println("\nCouldn't find the PeerNode");
                }
                else
                {
                  ListOfFiles lof= new ListOfFiles(list);
                  peerNode.setListOfFiles(lof);
                }
              }
              else if(obj.get("type").equals("HashMap"))
              {
                 System.out.println("Got an HashMap from:"+s.getInetAddress().toString()); 
                HashMap map = (HashMap)obj.get("value");
                peerList.getSelf().setHashMapFilePeer(map);
              }
              else
              {
                 System.out.println("Got an Invalid Message from:"+s.getInetAddress().toString()); 
                System.out.println("Invalid type");
              }
              /*  myFile = new File(str);
                JSONObject toSend = JSONManager.getJSON(myFile);
                System.out.println("---"+toSend.toString());
                byte[] mybytearray = new byte[(int) myFile.length()];
                //BufferedInputStream bis = new BufferedInputStream(new FileInputStream(myFile));
                //bis.read(mybytearray, 0, mybytearray.length);
                OutputStream os = s.getOutputStream();
                os.write(toSend.toString().getBytes(), 0, toSend.toString().length());
                os.flush(); 
                System.out.println("Server: Sent file " + str);
            }*/


            //CLOSE SOCKET HERE 
            s.close();//Check!
       
            
        }catch (Exception e) {
          try{
              s.close();
              System.out.println("Server: closing socket "+s.toString());
              e.printStackTrace();}
            catch(Exception ee)
            {
            }
        }

    }

    JSONObject getMessage(Socket s)
    {

      JSONObject obj = null;
      try
      {
        InputStream inFromServer = s.getInputStream();
        DataInputStream in = new DataInputStream(inFromServer);
        String line,str = null;
        line = in.readUTF();
        obj = (JSONObject)(JSONManager.convertStringToJSON(line));
      }
      catch(Exception e)
      {
        e.printStackTrace();
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
