import java.io.*;
import java.util.*;

public class Sync implements Runnable{
    private ConnectionTable table;
    private ListOfFiles files;
    private SendList toBeSent;
    private PeerFileList fileList;

    Sync(){
    }

    public void run(){
        PeerNode controller = QuickSync.peerList.getMaster();
        String controllerAddress = controller.getIp();       //Obtain from sortedSet.first 
        String controllerPort = controller.getPort();          //Cloud Obtain from sortedSet.first

        /*TODO: Keep checking if any changes have been made to PeerFileList and HashTable
         * and send to the controller*/ 
        keepChecking();

        /* Call seekFromPeer() on the list of files received from the controller */
        if(QuickSync.controller == false){
            hashMap = getFilesToRequestPerPeer(hmFromController);
        }

        Set mappingSet = hashMap.entrySet();

        Iterator itr =  mappingSet.iterator();

        while(itr.hasNext()){
            Map.Entry entry = (Map.Entry)itr.next();
            ret = seekFromPeer(entry.getValue(), entry.getKey());
            if(ret == false){
                System.out.println("Not seeking from peer. Error!");
            }
        }
    }

    boolean seekFromPeer(ArrayList<String> fileName, String peerId){
        PeerNode peer;

        if(fileName == null || peerId == null){
            return false;
        }

        peer = QuickSync.peerList.getPeerNode(peerId);

        if(peer == null){
            return false;
        }

        /* Insert code to open a client thread and ask the peer for the file */
        Thread client = new Thread(new TcpClient(peer.getIp(), peer.getPort(), fileName));
        client.start();
        System.out.println("Created client TCP connection");

        return true;
    }


    HashMap<String, ArrayList<String>> getFilesToRequestPerPeer(ListOfPeers peers){
        /* Condense hashmap from controller to a dense hashmap of actual files to get*/
    }


    void keepChecking(){
        /* Keep checking the shared directory */

        /* If changes found, send a list of files to controller */
        /* Make a hashmap of self.peerId and list of files */
    }
}
