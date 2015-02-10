import java.security.*;
import java.util.*;

class ConnectionTable
{
   String srcIP,destIP;
   int srcPort,destPort;
   HashMap<String,PeerNode> connections =  new HashMap<String,PeerNode>();

   ConnectionTable ()
   {
	    
   }
	   
   boolean addConnection(String srcIP, String destIP, int srcPort,int destPort,PeerNode PeerNode)
   {
       String hashVal = getHash(srcIP,destIP,srcPort,destPort);
	   if(connections.containsKey(hashVal))
	   {
		   return false;
	   }
       connections.put(hashVal,PeerNode);
	   return true;
   }

   PeerNode removeConnection(String srcIP, String destIP, int srcPort,int destPort)
   {
	    String hashVal = getHash(srcIP,destIP,srcPort,destPort);
        if(connections.containsKey(hashVal))
			return connections.get(hashVal);

		return null;
   }

   String getHash(String srcIP, String destIP, int srcPort,int destPort)
   {
	  try
	  {

	  String concat = srcIP + destIP + String.valueOf(srcPort) + String.valueOf(destPort);
	  byte[] bytesOfMessage = concat.getBytes("UTF-8");

	  MessageDigest md = MessageDigest.getInstance("MD5");
	  byte[] theDigest = md.digest(bytesOfMessage);

     StringBuffer sb = new StringBuffer();
     
     for (int i = 0; i < theDigest.length; i++) 
        {
         sb.append(Integer.toString((theDigest[i] & 0xff) + 0x100, 16).substring(1));
        }  
     
	    return sb.toString(); 
	  }

	  catch (Exception e)
	  {
		   
	  }

	  return "error";
   }   
   

}
