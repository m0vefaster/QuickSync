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
        PeerNode controller = peerList.getMaster();
    	ListOfFiles lof = peerList.getSelf().getListOfFiles();


        /* Keep checking if any changes have been made to the shared directory */
        while(true){
            /* Update list fo file for self by periodic poling of the shared directory */
            peerList.setList( lof.getList()); 
            //Send to Controller
                
            /* Call seekFromPeer() on the list of files received from the controller */
                
				HashMap<String, ArrayList<String>> hmFilesPeers = getFilesToRequestPerPeer(peerList.getSelf().getHashMapFilePeer());

                Set mappingSet = hashMap.entrySet();

				Iterator itr =  mappingSet.iterator();

				while(itr.hasNext()){
					Map.Entry entry = (Map.Entry)itr.next();
					ret = seekFromPeer(new ArrayList<String>(entry.getKey()), entry.getValue().indexOf(0));
					if(ret == false){
						System.out.println("Not seeking from peer. Error!");
					}
				}

                
			if(peerList.getMaster() == null) /*I am the master*/
			{
           		peerList.getSelf().setHashMapFilePeer( getFilesToRequestPerPeerMaster(peerList));
                                
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
        Thread client = new Thread(new TcpClient(peer.getId(), peer.getPort(), fileName));
        client.start();
        System.out.println("Created client TCP connection");

        return true;
    }


    HashMap<String, ArrayList<String>> getFilesToRequestPerPeerMaster(ListOfPeers peers){
        /* Condense hashmap from controller to a dense hashmap of actual files to get*/
		SortedSet<PeerNode> peerList = peers.getPeerList();
        PeerNode mySelf = peers.getSelf();
        int i;

        ArrayList<String> filesWithSelf=myself.getListOfFiles();
        HashMap<String, ArrayList<String>> hmFilesPeers	= new HashMap<String, ArrayList<String>>();
	    
	    Iterator<PeerNode> it = set.iterator();
        while (it.hasNext()) 
        {
          PeerNode peerNode = it.next();
          ArrayList<String> lof = peerNode.getListOfFiles();
          
          for(i=0; i < lof.size();i++)
          {
              if ( hmFilesPeers.containsKey(lof.indexOf(i)))
               {
                   hmFilesPeers.get(lof.indexOf(i)).add(peerNode.getId());  
			   }
              else
               {
                   Array<String> newListOfPeers = new Array<String>();
                   newListOfPeers.add(peerNode.getId());
  				   hmFilesPeers.put(lof.indexOf(i),newListOfPeers);
               }
          }
        }
        

        return hmFilesPeers;       
        	
    }

    HashMap<String, ArrayList<String>> getFilesToRequestPerPeer(HashMap<String,ArrayList<String>> hmFilesPeers){
        int i;
         ArrayList<String> filesWithSelf=myself.getListOfFiles();
          for(i=0;i<filesWithSelf.size();i++)
            {
               if(hmFilesPeers.containsKey(filesWithSelf(i)))
                 hmFilesPeers.remove(filesWithSelf.indexOf(i));
            }

        return hmFilesPeers;
     }
 }
