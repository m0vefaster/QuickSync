import java.io.*;

public class PeerNode implements Serializable{
    private byte[] peerId;
    private int peerWeight;
    private lof ;

    PeerNode(byte[] peerId, int peerWeight){
        this.peerId = peerId;
        this.peerWeight = peerWeight;
    }

    public byte[] getId()
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
}

