import java.io.*;
import java.util.*;
import java.net.*;

class ListOfPeers
{
    SortedSet<PeerNode> peerList = new TreeSet<PeerNode>(new Comp());
    int offset;
    PeerNode mySelf;
    HashMap<String,String> filesInSync = new HashMap<String,String>();
    ListOfPeers(PeerNode mySelf)
    {
        //Insert to Peer List cloud domain id
        this.mySelf = mySelf;
    }
    
    PeerNode getSelf()
    {
        return mySelf;
    }
    
    class Comp implements Comparator<PeerNode>
    {
        @Override
        public int  compare(PeerNode pn1,PeerNode pn2)
        {
            if( pn2.getWeight() > pn1.getWeight())
                return -1;
            if(pn1.getWeight() == pn2.getWeight())
                return 0;
            return 1 ;
        }
    }
    
    boolean addPeerNode(PeerNode newNode)
    {
        if(present(newNode))
        {
            System.out.println("ListOfPeers:addPeerNode:Peer List is: " + peerList);
            return false;
        }
        
        peerList.add(newNode);
        System.out.println(peerList);
        return true;
    }
    

    boolean removePeerNode(PeerNode removeNode)
    {
        if(!present(removeNode))
            return false;
        
        peerList.remove(removeNode);
        return true;
    }
    
    boolean removePeerNode(String peerId)
    {
        Iterator<PeerNode> itr = peerList.iterator();
        
        while(itr.hasNext())
        {
          PeerNode node = itr.next() ;
            if(node.getId().equals(peerId))
            {
              removePeerNode(node);
            }
            return true;
        }
        return false;
    }

    PeerNode getMaster()
    {
        if(peerList.size()>0  && (peerList.first().getWeight()< getSelf().getWeight()))
          return peerList.first();
        
        return null;
    }
    
    boolean present(PeerNode node)
    {
        Iterator<PeerNode> itr = peerList.iterator();
        
        while(itr.hasNext())
        {
            if(itr.next().getId()==node.getId())
            return true;
        }
        
        return false;
    }
    
    PeerNode getPeerNode(String peerId){
        Iterator<PeerNode> itr = peerList.iterator();
        PeerNode node;
        
        while(itr.hasNext()){
            node = itr.next();
            if(node.getId().equals(peerId)){
                return node;
            }
        }

        return null;
    }
    
    
    SortedSet<PeerNode> getList()
    {
        return peerList;
    }
    
    PeerNode getPeerNodeFromIP(String ipAddress)
    {
        Iterator<PeerNode> itr = peerList.iterator();
        PeerNode node;

        while(itr.hasNext()){
            node = itr.next();
            if(node.getIPAddress().equals(ipAddress)){
                return node;
            }
        }
        
        return null;
    }


    void printPeerList()
    {
        Iterator<PeerNode> itr = peerList.iterator();
        PeerNode node;
        //System.out.print("\n************************The Peer list is:");
        while(itr.hasNext()){
            node = itr.next();
            //System.out.print(node.getId()+",");
        }
        //System.out.println();
    }

   PeerNode getPeerNodeFromSocket(Socket s)
   {
    Iterator<PeerNode> itr = peerList.iterator();
        PeerNode node;
        while(itr.hasNext()){
            node = itr.next();
        //System.out.println("Comparing roginal Socket:"+s +":with:"+node.getSocket());
            if(node.getSocket() == s ) {
        //System.out.println("Found a match for socket as PeerNode :"+ node.getId());
                return node;
            }

        }

        return null;
   }    


    void updateHashMapBeforeRemovingNode(PeerNode nodeToBeRemoved)
   {
      HashMap<String, ArrayList<String>> hmFilesPeers = mySelf.getHashMapFilePeer();
      Set mappingSet = hmFilesPeers.entrySet();
      String removeNodeId=nodeToBeRemoved.getId();  
      Iterator itr =  mappingSet.iterator();
      //System.out.println("=================Node Id is :"+removeNodeId);
      //System.out.println("==================HashMap before removal---"+hmFilesPeers);  
      while(itr.hasNext()){
            Map.Entry<String, ArrayList<String>> entry = (Map.Entry<String, ArrayList<String>>)itr.next();
            ArrayList<String> allPeers = entry.getValue();
            int i=0;
              while(i<allPeers.size())
                {
                //System.out.println("inside looping i=:" + i+  "and finding peer node with ID:"+allPeers.get(i));
                    PeerNode node = getPeerNode(allPeers.get(i));
                    if(node!=null && node.getId().equals(removeNodeId))
                        {
                //System.out.println("removed node" + node.getId());
                            allPeers.remove(node.getId());
                break;
                        }
                    else
                        i++;
                }
        }
    
        //System.out.println("=====================HashMap after removal---"+hmFilesPeers); 

   }


   ArrayList<String> addFilesInTransit(ArrayList<String> fileList,String peerId)
   {
       int i;
       //System.out.println("Sent fileList" + fileList);
       //System.out.println("Just Entered addFilesInTransit:"+syncMap("","print"));
       ArrayList<String> listOfFileToSend = new ArrayList<String>();
       for(i=0;i<fileList.size();i++)
       {
            if(!syncMap(fileList.get(i),peerId,"contains"))
            {
                listOfFileToSend.add(fileList.get(i));
                syncMap(fileList.get(i),peerId,"add");
            }
       }

       //System.out.println("Leaving addFilesInTransit:"+syncMap("","print"));
       return listOfFileToSend;

   }

   boolean removeFileInTransit(String fileName,String peerId)
   {
        //System.out.println("Just Entered removeFileInTransit:"+syncMap("","print"));

        if(!syncMap(fileName,peerId,"contains"))
            {
                return false;
            }
         syncMap(fileName,peerId,"remove");   
         return true;      
   }

   synchronized boolean syncMap(String fileName, String peerId, String type)
   {
    //System.out.println("Just Entered syncMap:"+filesInSync);
    //System.out.println("SyncMap: fileName: " + fileName + " type: " + type);

    /*HahsMap is <File,Peer> */
    switch(type)
    {
        case "add":  
                    if(filesInSync.containsKey(fileName))
                        {
                            //System.out.println("Error:File present in filesInSync");
                            return false;
                        }
                    //System.out.println("Adding File");
                    filesInSync.put(fileName,peerId);
                    break;
        case "remove":
                       if(!filesInSync.containsKey(fileName))
                       {
                            //System.out.println("Error:File not present in filesInSync");
                            return false;
                       }   
                        //System.out.println("Rmoving File");
                       filesInSync.remove(fileName);
                       break;
        case "contains":return filesInSync.containsKey(fileName);
        case "clearForPeer":
                            System.out.println("Size of filesInSync is:"+filesInSync.size());
                             Set mappingSet = filesInSync.entrySet();
                             Iterator itr =  mappingSet.iterator();
                            while(itr.hasNext()){
                            Map.Entry<String, String> entry = (Map.Entry<String, String>)itr.next();
                                if(entry.getValue().equals(peerId))
                                {
                                    filesInSync.remove(entry.getKey());
                                }
                            }
                            System.out.println("Size of filesInSync is:"+filesInSync.size());
                            break;
        case "print" :   if(filesInSync==null) return false;
                        System.out.print(filesInSync);
                        break;
        case "default": //System.out.println("Hit wrong statement");
                        return false;
    } 
     //System.out.println("Leaving syncMap:"+filesInSync);
     return true;
   }

   void setOffset(int offset)
   {
	   this.offset=offset;
   }
   int getOffset( )
   {
	   return offset;
   }








}
