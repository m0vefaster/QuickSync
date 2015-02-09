import java.io.*;

public class peerNode implements Serializable{
    private byte[] peerId;
    private int peerWeight;

    peerNode(byte[] peerId, int peerWeight){
        this.peerId = peerId;
        this.peerWeight = peerWeight;
    }
}


