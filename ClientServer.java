import java.io.IOException;
import java.io.*;
import java.net.*;

public class ClientServer{
    public static void main(String[] args){
        Thread client1 = new Thread(new Client(args[0], args[1], args[2]));
        Thread client2 = new Thread(new Client(args[3], args[1], args[4]));

        client1.start();
        client2.start();
        ServerSocket ss = null;
        try{
            ss = new ServerSocket(Integer.parseInt(args[1]));
        }catch (Exception e2){
        }

        while(true){
            try {
                Socket s = ss.accept();
                Thread server = new Thread(new Server(ss, s));
                System.out.println("Created Thread for " +s. getRemoteSocketAddress());
		server.start();
            } catch (Exception e) {
                  try 
                   {
                      Thread.sleep(10); //milliseconds
                   } 
                 catch (InterruptedException e1) 
                  {

                  } 
            }
        }
    }
}
