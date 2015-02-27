import java.io.*;
import java.util.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Sync implements Runnable{
    ListOfFiles files;
    SendList toBeSent;
    PeerFileList fileList;
    ListOfPeers listOfPeers;
    
    Sync(ListOfPeers listOfPeers)
    {
        this.listOfPeers = listOfPeers;
    }
    
    public void run()
    {
        boolean ret = false;
        ListOfFiles lof = listOfPeers.getSelf().getListOfFiles();
        ArrayList<String> arrayOfFiles = new ArrayList<String>();
        
        
        /* Keep checking if any changes have been made to the shared directory */
        while(true){
            
            PeerNode masterNode = listOfPeers.getMaster();
            
            //Send to Controller
            if(masterNode!=null)
            {
                JSONObject obj = JSONManager.getJSON(lof.getList());// make the object
                if(obj==null)
                {
                  System.out.println("Sync:run:Obj is null");
                  continue;
                }
                Thread client = new Thread(new TcpClient(masterNode.getIPAddress().toString(), "60010", obj));
                client.start();
            }
            
            /* Call seekFromPeer() on the list of files received from the controller */
            //HashMap<String, ArrayList<String>> hmFilesPeers = getFilesToRequestPerPeer(listOfPeers.getSelf().getHashMapFilePeer());
            

            Set mappingSet = listOfPeers.getSelf().getHashMapFilePeer().entrySet();
            Iterator itr =  mappingSet.iterator();
            
            while(itr.hasNext()){
                Map.Entry<String, ArrayList<String>> entry = (Map.Entry<String, ArrayList<String>>)itr.next();
                ret = seekFromPeer(String.valueOf(entry.getKey()), entry.getValue().get(0));//Instead of Index 0 seek from peer based on Algo.
                if(ret == false){
                    System.out.println("Sync:run:Seeking from Peer failed\n");
                    listOfPeers.printPeerList();
                }
            }
            
            
            if(listOfPeers.getMaster() == null) /*I am the master*/
            {
                if(listOfPeers.getList().size() !=0)
                  System.out.println("Sync:run:I am the master and number of nodes in the list are" + listOfPeers.getList().size() );
                
                else
                    {
                        System.out.println("Sync:run:Looks like I am the only one here!");
                        continue;
                    }
                
                listOfPeers.getSelf().setHashMapFilePeer( getFilesToRequestPerPeerMaster(listOfPeers));

                SortedSet<PeerNode> peerList =listOfPeers.getList();
                Iterator<PeerNode> it = peerList.iterator();

                while (it.hasNext())
                {
                    PeerNode peerNode = it.next();
                    HashMap<String, ArrayList<String>> hmFilesPeers = getFilesToRequestPerPeer(listOfPeers.getSelf().getHashMapFilePeer(),peerNode.getListOfFiles().getArrayListOfFiles());
                    JSONObject obj = JSONManager.getJSON(listOfPeers.getSelf().getHashMapFilePeer());// make the object
                    Thread client = new Thread(new TcpClient(peerNode.getIPAddress().toString(), "60010", obj));
                    client.start();
                }

                System.out.println();
            }
            
            try
            {
                Thread.sleep(3000);
            }

            catch(Exception e)
            {
            }
        }
    }
    
    void print(HashMap<String, ArrayList<String>> hmap){
        Set mappingSet = hmap.entrySet();
        
        Iterator itr =  mappingSet.iterator();
        
        while(itr.hasNext()){
            Map.Entry<String, ArrayList<String>> entry = (Map.Entry<String, ArrayList<String>>)itr.next();
        }
    }
    
    boolean seekFromPeer(String fileName, String peerId){
        PeerNode peer;

        if(fileName == null || peerId == null){
            return false;
        }
        
        peer = listOfPeers.getPeerNode(peerId);
        
        if(peer == null){
            return false;
        }

        JSONObject obj = JSONManager.getJSON(fileName);
        Thread client = new Thread(new TcpClient(peer.getId(), "60010", obj));
        client.start();
        
        return true;
    }
    
    
    HashMap<String, ArrayList<String>> getFilesToRequestPerPeerMaster(ListOfPeers peers){
        /* Condense hashmap from controller to a dense hashmap of actual files to get*/
        SortedSet<PeerNode> peerList = peers.getList();
        PeerNode mySelf = peers.getSelf();
        HashMap<String, ArrayList<String>> hmFilesPeers = new HashMap<String, ArrayList<String>>();
        Iterator<PeerNode> it = peers.peerList.iterator();

        while (it.hasNext())
        {
            PeerNode peerNode = it.next();
            addToHashMap(hmFilesPeers,peerNode);
        }
        
        addToHashMap(hmFilesPeers,mySelf);

        return hmFilesPeers;   
    }
    
    
    void addToHashMap(HashMap<String, ArrayList<String>> hmFilesPeers,PeerNode peerNode)
    {
        ArrayList<String> lof = peerNode.getListOfFiles().getArrayListOfFiles();
        
        if(lof==null)
        return;
        int i;
        for(i=0; i < lof.size();i++)
        {
            if ( hmFilesPeers.containsKey(lof.get(i)))
            {
                hmFilesPeers.get(lof.get(i)).add(peerNode.getId());
            }
            else
            {
                ArrayList<String> newListOfPeers = new ArrayList<String>();
                newListOfPeers.add(peerNode.getId());
                hmFilesPeers.put(lof.get(i), newListOfPeers);
            }
        }
    }
    
    HashMap<String, ArrayList<String>> getFilesToRequestPerPeer(HashMap<String,ArrayList<String>> hmFilesPeers,ArrayList<String> filesWithPeer)
    {
        if(filesWithPeer==null)
        {
            return hmFilesPeers;
        }
        
        int i;
        for(i=0;i<filesWithPeer.size();i++)
        {
            if(hmFilesPeers.containsKey(filesWithPeer.get(i)))
            {
                hmFilesPeers.remove(filesWithPeer.get(i));
            }
        }
        
        return hmFilesPeers;
    }
    
    void find(int x)
    {
        System.out.println("Sync:run:========Inside find" + x + "===========");
        Iterator<PeerNode> it = listOfPeers.getList().iterator();
        while (it.hasNext())
        {
            PeerNode peerNode = it.next();
            ArrayList<String> lof = peerNode.getListOfFiles().getList();
            System.out.println("For peer node:"+peerNode.getId()+" list of files is:"+lof.toString());
        }
        System.out.println("Sync:run:========Leaving find()===========");
    }
}
