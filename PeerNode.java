import java.io.*;
import java.util.*;
import java.net.*;

public class PeerNode implements Serializable{
    private String peerId;
    private int peerWeight;
    private ListOfFiles lof;
    private HashMap<String, ArrayList<String>> hMap;
    InetAddress ipAddress ;
    PeerNode(String peerId){
        Random ran = new Random();
        this.peerWeight = ran.nextInt(60000);
        this.peerId = peerId;
        lof = new ListOfFiles();
        hMap = new HashMap<String, ArrayList<String>>();
    }

    PeerNode(String peerId, int peerWeight){
        this.peerId = peerId;
        this.peerWeight = peerWeight;
        lof = new ListOfFiles();
        hMap = new HashMap<String, ArrayList<String>>();
    }

    public String getId()
    {
      return peerId;
    }

    public int getWeight(){
        return peerWeight;
    }

    void setListOfFiles(ListOfFiles lof)
    {
            this.lof = lof;
    }

    ListOfFiles getListOfFiles( ) 
    {
            return lof;
    }

    void setHashMapFilePeer(HashMap<String, ArrayList<String>> hashFromController)
    {
            hMap = hashFromController;
    }

    HashMap<String, ArrayList<String>> getHashMapFilePeer( ) 
    {
            return hMap;
    }

    void setIPAddress(InetAddress ipAddress )
    {
        this.ipAddress=ipAddress;
    }

    InetAddress getIPAddress()
    {
         return ipAddress;
    }
}

