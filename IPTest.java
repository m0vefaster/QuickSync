import java.net.*;
import java.io.*;
import java.lang.*;
import java.security.*;
import java.net.UnknownHostException;

public class IPTest {
  
  
    public static void main(String args[]) throws UnknownHostException {
    
        InetAddress addr = InetAddress.getLocalHost();
        String ipAddress = addr.getHostAddress();
      
        System.out.println("IP address of localhost from Java Program: " + ipAddress);
      
        //Hostname
        String hostname = addr.getHostName();
        System.out.println("Name of hostname : " + hostname);
      
    }
  
}


