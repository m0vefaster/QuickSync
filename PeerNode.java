import java.io.*;

public class PeerNode implements Serializable{
    private byte[] peerId;
    private int peerWeight;

    PeerNode(byte[] peerId, int peerWeight){
        this.peerId = peerId;
        this.peerWeight = peerWeight;
    }
}

