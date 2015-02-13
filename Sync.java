import java.io.*;
import java.util.*;

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
        while(true){
            /* Update list fo file for self by periodic poling of the shared directory */
            lof.setList(lof.getList()); 
            int count = 0;
            while(count < lof.getList().size()){
              System.out.println("Sync.java: lof updated to " + lof.getList().get(count));
              count++;
            }
            //Send to Controller
                
            /* Call seekFromPeer() on the list of files received from the controller */
                
            HashMap<String, ArrayList<String>> hmFilesPeers = getFilesToRequestPerPeer(peerList.getSelf().getHashMapFilePeer());
            count = 0;
            while(count < hmFilesPeers.size()){
              System.out.println("Sync.java: hmFilesPeers updated to " + hmFilesPeers.get(count));
              count++;
            }

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


            if(peerList.getMaster() == null) /*I am the master*/
            {
                System.out.println("I am the master");
                peerList.getSelf().setHashMapFilePeer( getFilesToRequestPerPeerMaster(peerList));
            }

            try{
                Thread.sleep(30000);
            }catch(Exception e){
            }
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

        /* Insert code to open a client thread and ask the peer for the file */
        /* TODO: Change hard-coded port*/
        Thread client = new Thread(new TcpClient(peer.getId(), "60010", fileName));
        client.start();
        System.out.println("Created client TCP connection " + fileName.get(0));

        return true;
    }


    HashMap<String, ArrayList<String>> getFilesToRequestPerPeerMaster(ListOfPeers peers){
        /* Condense hashmap from controller to a dense hashmap of actual files to get*/
        SortedSet<PeerNode> peerList = peers.getList();
        PeerNode mySelf = peers.getSelf();
        int i;

        ArrayList<String> filesWithSelf=mySelf.getListOfFiles().getList();
        HashMap<String, ArrayList<String>> hmFilesPeers	= new HashMap<String, ArrayList<String>>();
	    
        Iterator<PeerNode> it = peers.peerList.iterator();
        while (it.hasNext()) 
        {
          PeerNode peerNode = it.next();
          ArrayList<String> lof = peerNode.getListOfFiles().getList();
          
          for(i=0; i < lof.size();i++)
          {
              if ( hmFilesPeers.containsKey(lof.indexOf(i)))
               {
                   hmFilesPeers.get(lof.indexOf(i)).add(peerNode.getId());  
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

         ArrayList<String> filesWithSelf=mySelf.getListOfFiles().getList();
          for(i=0;i<filesWithSelf.size();i++)
            {
               if(hmFilesPeers.containsKey(filesWithSelf.indexOf(i)))
                 hmFilesPeers.remove(filesWithSelf.indexOf(i));
            }

        return hmFilesPeers;
     }
 }
