

import java.net.*;
import java.io.*;
import java.lang.*;
public class Client implements Runnable
{

     String serverName ;
     int port;
     private Thread t;
     private String threadName = "Client";
     private String fileName;

     Client (String serverName, String port, String fileName)
     {
        this.serverName = serverName;    	      
	this.port = Integer.parseInt(port);
        this.fileName = fileName;
     }

     public void run()
     {
	      try
	      {
		 System.out.println("Client:Connecting to " + serverName + " on port " + port);
		 Socket client =null ;
		 do 
		 {
		    try
	    	    {
		  	  client = new Socket(serverName,port);
	            }
		    catch (Exception anye)
		    {
                          try 
                               {
                                  t.sleep(100); //milliseconds
       		               } 
                         catch (InterruptedException e) 
                              {

             		      } 
		    }

		  }while(client==null);

		 System.out.println("Client:Just connected to "
			      + client.getRemoteSocketAddress());
		 OutputStream outToServer = client.getOutputStream();
		 DataOutputStream out =
			       new DataOutputStream(outToServer);

		 out.writeUTF(fileName);
   
                 System.out.println("Client:Sent Filename " + fileName);
                 File myFile = new File(fileName);
	         byte[] mybytearray = new byte[(int) myFile.length()];
                 BufferedInputStream bis = new BufferedInputStream(new FileInputStream(myFile));
                 bis.read(mybytearray, 0, mybytearray.length);
                 OutputStream os = client.getOutputStream();
                 os.write(mybytearray, 0, mybytearray.length);
                 os.flush(); 
		 
		 System.out.println("Client:Sent the file " + fileName);
                 client.close();
	      }

	      catch(IOException e)
	      {
		 e.printStackTrace();
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
    
}
