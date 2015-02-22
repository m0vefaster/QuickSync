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
    ListOfPeers peerList;
    
    Sync(ListOfPeers peerList)
    {
        this.peerList = peerList;
    }
    
    public void run()
    {
        boolean ret = false;
        ListOfFiles lof = peerList.getSelf().getListOfFiles();
        ArrayList<String> arrayOfFiles = new ArrayList<String>();
        
        
        /* Keep checking if any changes have been made to the shared directory */
        while(true){

            /* Update list fo file for self by periodic poling of the shared directory */
            lof.setList(lof.getList());
            
            PeerNode masterNode = peerList.getMaster();
            
            //Send to Controller
            if(masterNode!=null)
            {
                JSONObject obj = JSONManager.getJSON(lof.getList());// make the object
                if(obj==null)
                {
                  System.out.println("Sync:run:Obj is null");
                }
                Thread client = new Thread(new TcpClient(masterNode.getIPAddress().toString(), "60010", obj));
                client.start();
            }
            
            /* Call seekFromPeer() on the list of files received from the controller */
            HashMap<String, ArrayList<String>> hmFilesPeers = getFilesToRequestPerPeer(peerList.getSelf().getHashMapFilePeer());
            

            Set mappingSet = hmFilesPeers.entrySet();
            Iterator itr =  mappingSet.iterator();
            
            while(itr.hasNext()){
                Map.Entry<String, ArrayList<String>> entry = (Map.Entry<String, ArrayList<String>>)itr.next();
                arrayOfFiles.add(0,
                String.valueOf(entry.getKey()));
                ret = seekFromPeer(arrayOfFiles, entry.getValue().get(0));
                if(ret == false){
                    System.out.println("Sync:run:Seeking from Peer failed");
                }
            }
            
            
            if(peerList.getMaster() == null) /*I am the master*/
            {
                if(peerList.getList().size() !=0)
                  System.out.println("Sync:run:I am the master and number of nodes in the list are" + peerList.getList().size() );
                
                else
                    System.out.println("Sync:run:Looks like I am the only one here!");
                
                peerList.getSelf().setHashMapFilePeer( getFilesToRequestPerPeerMaster(peerList));
                JSONObject obj = JSONManager.getJSON(peerList.getSelf().getHashMapFilePeer());// make the object
                Iterator<PeerNode> itr2 = peerList.getList().iterator();
                PeerNode node;
                while(itr2.hasNext()){
                    node = itr2.next();
                    Thread client = new Thread(new TcpClient(node.getIPAddress().toString(), "60010", obj));
                    client.start();
                }
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
    
    boolean seekFromPeer(ArrayList<String> fileName, String peerId){
        PeerNode peer;

        if(fileName == null || peerId == null){
            return false;
        }
        
        peer = peerList.getPeerNode(peerId);
        
        if(peer == null){
            return false;
        }

        JSONObject obj = JSONManager.getJSON(fileName.get(0));
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
    
    HashMap<String, ArrayList<String>> getFilesToRequestPerPeer(HashMap<String,ArrayList<String>> hmFilesPeers)
    {
        int i;
        PeerNode mySelf = peerList.getSelf();
        
        ArrayList<String> filesWithSelf=mySelf.getListOfFiles().getList();
        for(i=0;i<filesWithSelf.size();i++)
        {
            if(hmFilesPeers.containsKey(filesWithSelf.get(i)))
            {
                hmFilesPeers.remove(filesWithSelf.get(i));
            }
        }
        
        return hmFilesPeers;
    }
    
    void find(int x)
    {
        System.out.println("Sync:run:========Inside find" + x + "===========");
        Iterator<PeerNode> it = peerList.getList().iterator();
        while (it.hasNext())
        {
            PeerNode peerNode = it.next();
            ArrayList<String> lof = peerNode.getListOfFiles().getList();
            System.out.println("For peer node:"+peerNode.getId()+" list of files is:"+lof.toString());
        }
        System.out.println("Sync:run:========Leaving find()===========");
    }
}
