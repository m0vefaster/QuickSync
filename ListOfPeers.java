import java.io.*;
import java.util.*;

class ListOfPeers
{
	SortedSet<PeerNode> peerList = new TreeSet<PeerNode>(new Comp());
    
	PeerNode mySelf;
     
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
			if( pn1.getWeight() > pn2.getWeight())
				return 1;
			return -1 ;
		}
   }	

   boolean addPeerNode(PeerNode newNode)
   {
	     if(present(newNode))
        {
          System.out.println(peerList);
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

   PeerNode getMaster()
   {
	   //return peerList.size()==0? null :peerList.first();
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
           System.out.println("\n" + node.getId() + "with  "+ peerId);
           if(node.getId().equals(peerId)){
               System.out.println("Found Match---");
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

       ipAddress=ipAddress.substring(1,ipAddress.length());

       Iterator<PeerNode> itr = peerList.iterator();

       PeerNode node;
       System.out.println("Size of peer list is :" + peerList.size());
       while(itr.hasNext()){
           node = itr.next();
           System.out.println("\nMatched:" + node.getIPAddress() + ":with:"+ ipAddress);
           if(node.getIPAddress().equals(ipAddress)){
               System.out.println("Found Match---");
               return node;
           }
       }

       return null;
   }
}
