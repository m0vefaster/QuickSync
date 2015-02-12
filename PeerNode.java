import java.io.*;

public class PeerNode implements Serializable{
    private String peerId;
    private int peerWeight;
    private ListOfFiles lof;
    private HashMap<ArrayList<String>, String> hMap;

    PeerNode(String peerId, int peerWeight){
        this.peerId = peerId;
        this.peerWeight = peerWeight;
        lof = null;
        hMap = null;
    }

    public String getId()
    {
      return peerId;
    }

    public int getWeight(){
        Random ran = new Random();
        peerWeight = ran.nextInt(60000);
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

    void setHashMapFilePeer(HashMap<ArrayList<String>, String> hashFromController)
    {
            hMap = hashFromController;
    }

    HashMap<ArrayList<String>, String> getHashMap( ) 
    {
            return hMap;
    }
}

