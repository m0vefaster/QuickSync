import java.io.IOException;
import java.io.*;
import java.net.*;

public class ClientServer{
    public static void main(String[] args){

	    
       ServerSocket ss = null;
        
	while(true)
	{
            try 
	    {
	    	ss = new ServerSocket(Integer.parseInt(args[1]));
                Socket s = ss.accept();
                Thread server = new Thread(new Server(ss, s));
                System.out.println("ClientServer:Created Thread for " +s. getRemoteSocketAddress());
		server.start();
                Thread client1 = new Thread(new Client(args[0], args[1], args[2]));
		client1.start();
            }
	    catch (Exception e) 
	    {
        	try
		{
			Thread.sleep(100);   
		}
		catch(Exception e1){}
	    }
        } 

        
    }
}
