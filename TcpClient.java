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
     //private ArrayList<String> fileName;
     private JSONObject obj;
     TcpClient (String serverName, String port, JSONObject obj)
     {
        this.serverName = serverName;    	      
        this.port = Integer.parseInt(port);
        this.obj = obj;
        //this.fileName = fileName;
     }

    public void run()
    {
        File myFile;
        String file;
        Socket client=null;
        
        try
        {
            System.out.println("Client:Connecting to " + serverName + " on port " + port);
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
                    anye.printStackTrace();
                }
            }while(client==null);
            System.out.println("Client:Just connected to " + client.getRemoteSocketAddress());
            sendMessage(obj, client);
            /*Iterator itr = fileName.iterator();
            while(itr.hasNext()){
              file = (String)itr.next();

              OutputStream outToServer = client.getOutputStream();
              DataOutputStream out = new DataOutputStream(outToServer);
              out.writeUTF(JSONManager.getJSON(file).toString());
              System.out.println("Client:File request sent for " + file);
              System.out.println(JSONManager.getJSON(file).toString());
              // Accept and store the file obtained from the peer 
              myFile = new File(file);
              if (myFile.createNewFile())
              {
                  System.out.println("Client: File is created!");
              }

              byte[] mybytearray = new byte[1024];
              InputStream is = client.getInputStream();
              FileOutputStream fos = new FileOutputStream(file);
              BufferedOutputStream bos = new BufferedOutputStream(fos);
              int bytesRead = is.read(mybytearray, 0, mybytearray.length);
              String str = mybytearray.toString();
              JSONObject obj = (JSONObject)(JSONManager.convertStringToJSON(str));
              if(obj.get("type").equals("File"))
              {
                String fileContent = (String)obj.get("value");
                bos.write(fileContent.getBytes());

              }
              bos.close();
          }

          // Send EOF to the server 
          OutputStream outToServer = client.getOutputStream();
          DataOutputStream out = new DataOutputStream(outToServer);
          out.writeUTF(((JSONObject)(JSONManager.getJSON("***EOF***"))).toString());
          client.close();
          */
            /* Close the socket after current batch of files is received */

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
    }

 
   void start ()
   {
      System.out.println("Starting " +  threadName );
      if (t == null)
      {
         t = new Thread (this, threadName);
         t.start ();
      }
   }
   void sendMessage(JSONObject obj, Socket client)
   {
      try
      {
        OutputStream outToServer = client.getOutputStream();
        DataOutputStream out = new DataOutputStream(outToServer);
        out.writeUTF(obj.toString());
        out.close();
        client.close();
      }
      catch(Exception e)
      {
        e.printStackTrace();
      }
   }
    
}
