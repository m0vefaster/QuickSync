import java.io.*;

public class PeerNode implements Serializable{
    private byte[] peerId;
    private int peerWeight;

    PeerNode(byte[] peerId, int peerWeight){
        this.peerId = peerId;
        this.peerWeight = peerWeight;
    }

    int getId()
    {   
      return peerId;
    }

    int getWeight(){
        Random rand = new Random();
        peerWeight = rand.nextInt(60000);
        return peerWeight;
    }
}

