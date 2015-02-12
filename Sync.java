import java.io.*;
import java.util.*;

public class Sync{
    private ConnectionTable table;
    private ListOfFiles files;
    private SendList toBeSent;
    private PeerFileList fileList;

    Sync(){
    }

    public void run(){
        PeerNode controller = ClientServerGen.peerList.getMaster();
        String controllerAddress = controller.getIp();       //Obtain from sortedSet.first 
        String controllerPort = controller.getPort();          //Cloud Obtain from sortedSet.first

        /*TODO: Keep checking if any changes have been made to PeerFileList and HashTable
         * and send to the controller*/ 
        keepChecking();

        /* Call seekFromPeer() on the list of files received from the controller */
        /* TODO:
        while( all peers in hashmap){
            ret = seekFromPeer(arrayList, peerId);
        }
        */
    }

    boolean seekFromPeer(ArrayList<String> fileName, byte[] peerId){
        PeerNode peer;

        if(fileName == null || peerId == null){
            return false;
        }

        peer = ClientServerGen.peerList.getPeerNode(peerId);

        if(peer == null){
            return false;
        }

        /* Insert code to open a client thread and ask the peer for the file */
        Thread client = new Thread(new TcpClient(peer.getIp(), peer.getPort(), fileName));
        client.start();
        System.out.println("Created client TCP connection");

        return true;
    }

    void keepChecking(){
        /* TODO: */
    }
}
