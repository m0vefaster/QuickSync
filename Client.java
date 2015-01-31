

import java.net.*;
import java.io.*;

public class Client
{
     public static void main(String args[])
     {

	      String serverName = args[0];
	      int port = Integer.parseInt(args[1]);

	      try
	      {
		 System.out.println("Connecting to " + serverName
				     + " on port " + port);
		 Socket client = new Socket(serverName, port);
		 System.out.println("Just connected to "
			      + client.getRemoteSocketAddress());
		 OutputStream outToServer = client.getOutputStream();
		 DataOutputStream out =
			       new DataOutputStream(outToServer);

		 out.writeUTF("Hello from "
			      + client.getLocalSocketAddress());


                 File myFile = new File("temp.c");
	         byte[] mybytearray = new byte[(int) myFile.length()];
                 BufferedInputStream bis = new BufferedInputStream(new FileInputStream(myFile));
                 bis.read(mybytearray, 0, mybytearray.length);
                 OutputStream os = client.getOutputStream();
                 os.write(mybytearray, 0, mybytearray.length);
                 os.flush(); 
		 
                 client.close();
	      }
	      
	      catch(IOException e)
	      {
		 e.printStackTrace();
	      }
    }
}
