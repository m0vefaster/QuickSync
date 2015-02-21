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

    public void run(){
        boolean ret = false;
        //PeerNode controller = peerList.getMaster();
        ListOfFiles lof = peerList.getSelf().getListOfFiles();
        ArrayList<String> arrayOfFiles = new ArrayList<String>();


        /* Keep checking if any changes have been made to the shared directory */
        boolean sentHM=false;
        while(true){
            /* Update list fo file for self by periodic poling of the shared directory */
            lof.setList(lof.getList()); 
            int count = 0;

            /*while(count < lof.getList().size()){
              System.out.println("Sync.java: lof updated to " + lof.getList().get(count));
              count++;
            }*/

            //Send to Controller

            PeerNode masterNode = peerList.getMaster();

            if(masterNode!=null)
            {
                  JSONObject obj = JSONManager.getJSON(lof.getList());// make the object
                  Thread client = new Thread(new TcpClient(masterNode.getIPAddress().toString(), "60010", obj));
                  client.start();
            }
                
            /* Call seekFromPeer() on the list of files received from the controller */
                
            HashMap<String, ArrayList<String>> hmFilesPeers = getFilesToRequestPerPeer(peerList.getSelf().getHashMapFilePeer());
            
            /*count = 0;
            while(count < hmFilesPeers.size()){
              System.out.println("Sync.java: hmFilesPeers updated to " + hmFilesPeers.get(count));
              count++;
            }*/
            
            /*print(hmFilesPeers);*/

            Set mappingSet = hmFilesPeers.entrySet();

            Iterator itr =  mappingSet.iterator();

            while(itr.hasNext()){
                Map.Entry<String, ArrayList<String>> entry = (Map.Entry<String, ArrayList<String>>)itr.next();
                arrayOfFiles.add(String.valueOf(entry.getKey()));
                ret = seekFromPeer(arrayOfFiles, entry.getValue().get(0));
                if(ret == false){
                    System.out.println("Not seeking from peer. Error!");
                }
            }


            if(peerList.getMaster() == null && !sentHM) /*I am the master*/
            {
                if(peerList.getList().size() !=0)
                 System.out.println("I am the master and number of nodes in the list are" + peerList.getList().size() );

                else
                 System.out.println("Looks like I am the only one here!");

                peerList.getSelf().setHashMapFilePeer( getFilesToRequestPerPeerMaster(peerList));

                JSONObject obj = JSONManager.getJSON(peerList.getSelf().getHashMapFilePeer());// make the object

                Iterator<PeerNode> itr2 = peerList.getList().iterator();
                PeerNode node;

               while(itr2.hasNext()){
                  node = itr2.next();
                  Thread client = new Thread(new TcpClient(node.getIPAddress().toString(), "60010", obj));
                  client.start();
                 }

                 sentHM=true;
            }
            

            try{
                Thread.sleep(3000);
            }catch(Exception e){
            }
         }
    }

    void print(HashMap<String, ArrayList<String>> hmap){
        Set mappingSet = hmap.entrySet();

        Iterator itr =  mappingSet.iterator();

        while(itr.hasNext()){
            Map.Entry<String, ArrayList<String>> entry = (Map.Entry<String, ArrayList<String>>)itr.next();
            System.out.println("Hash from controller " + entry.getKey() + entry.getValue());
        }
    }

    boolean seekFromPeer(ArrayList<String> fileName, String peerId){
        PeerNode peer;

        System.out.println(String.valueOf(fileName==null) + "-------- " +  peerId);
        if(fileName == null || peerId == null){
            return false;
        }

        peer = peerList.getPeerNode(peerId);

        if(peer == null){
            return false;
        }

        /* Insert code to open a client thread and ask the peer for the file */
        /* TODO: Change hard-coded port*/
        //Thread client = new Thread(new TcpClient(peer.getId(), "60010", fileName));
        JSONObject obj = null;
        Thread client = new Thread(new TcpClient(peer.getId(), "60010", obj));
        client.start();
        System.out.println("Created client TCP connection to get:" + fileName.get(0));

        return true;
    }


    HashMap<String, ArrayList<String>> getFilesToRequestPerPeerMaster(ListOfPeers peers){
        /* Condense hashmap from controller to a dense hashmap of actual files to get*/
        SortedSet<PeerNode> peerList = peers.getList();
        PeerNode mySelf = peers.getSelf();
        int i;

        
        HashMap<String, ArrayList<String>> hmFilesPeers	= new HashMap<String, ArrayList<String>>();
	    
        Iterator<PeerNode> it = peers.peerList.iterator();
        while (it.hasNext()) 
        {
          PeerNode peerNode = it.next();
          ArrayList<String> lof = peerNode.getListOfFiles().getArrayListOfFiles();
          if(lof==null)
            continue;

         //System.out.println("***********lof:"+lof.toString());
          for(i=0; i < lof.size();i++)
          {
             //System.out.println("***********hmFile:"+hmFilesPeers.toString());
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
        

        return hmFilesPeers;       
        	
    }

    HashMap<String, ArrayList<String>> getFilesToRequestPerPeer(HashMap<String,ArrayList<String>> hmFilesPeers){
        int i;
        PeerNode mySelf = peerList.getSelf();

        System.out.println("\n\nInside getFileToRequestPerPeer");

        print(hmFilesPeers);
        ArrayList<String> filesWithSelf=mySelf.getListOfFiles().getList();
        for(i=0;i<filesWithSelf.size();i++)
        {
            System.out.println("\nMatching" + filesWithSelf.get(i) );	
            if(hmFilesPeers.containsKey(filesWithSelf.get(i)))
            {
                System.out.println("\nMatched" + filesWithSelf.indexOf(i) );
                hmFilesPeers.remove(filesWithSelf.get(i));
            }
        }

        print(hmFilesPeers);  
        return hmFilesPeers;
    }

    void find(int x)
    {
        System.out.println("========Inside find" + x + "==========="); 
        Iterator<PeerNode> it = peerList.getList().iterator();
        while (it.hasNext())
        {
            PeerNode peerNode = it.next();
            ArrayList<String> lof = peerNode.getListOfFiles().getList();
            System.out.println("For peer node:"+peerNode.getId()+" list of files is:"+lof.toString());
        }
        System.out.println("========Leaving find()===========");
    } 
}
