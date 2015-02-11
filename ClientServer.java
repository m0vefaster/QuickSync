import java.io.IOException;
import java.io.*;
import java.net.*;
import java.util.*;

public class ClientServer{
    public static void main(String[] args){

	    
       ServerSocket ss = null;
  //controller search code
  String controllerAddress = "";
  String controllerPort = "";
  //send files
  ListOfFiles files = new ListOfFiles(args[0]);
  SendList toBeSent = new SendList(controllerAddress, controllerPort);
  toBeSent.sendList(files.getList());

  //TODO:get peer files        
  HashMap< String,ArrayList< String > > map = new HashMap<String, ArrayList<String> >();

  Iterator< Map.Entry<String, ArrayList<String> > > it = map.entrySet().iterator();

	while(true)
	{
    try 
    {
      while(it.hasNext()){       
        Map.Entry<String, ArrayList<String> > pairs = (Map.Entry<String, ArrayList<String> >)it.next(); 
        ArrayList<String> fileList = pairs.getValue();
	    	ss = new ServerSocket(Integer.parseInt(pairs.getKey()));
        for(String str: fileList)
        {
          Socket s = ss.accept();
          System.out.println("Server: Accepted Connection");
		  Thread server = new Thread(new Server(ss, s));
          System.out.println("ClientServer:Created Thread for " +s. getRemoteSocketAddress());
          server.start();
          Thread client1 = new Thread(new Client(pairs.getKey(), "60010", str));
          client1.start();
        }
      }
    }
    catch (Exception e) 
    {
		e.printStackTrace();

  		try
      {
		ss.close();  
        Thread.sleep(1000);   
      }
      catch(Exception e1){ e1.printStackTrace(); }
    }
  } 

        
}
}
