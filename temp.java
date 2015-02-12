
void keepChecking()
{
	ListOfFiles lof = new ListOfFiles();

	while(true)
	{
        ArrayList<ArrayList<String>> llof = peerList.getPeerListOfFiles();
        PeerFileList pfl = new PeerFileList();
		llof.add(self.getListOfFiles());
        HashMap<String,ArrayList<String>> hm = pfl.getListOFPeersWithFiles(llof);		
	    //Send HashMap 	
	}
}


//To get when Controller
HashMap<String,ArrayList<String>> getHashMapOfFile()
{
        ArrayList<ArrayList<String>> llof = peerList.getPeerListOfFiles();
        PeerFileList pfl = new PeerFileList();
        llof.add(self.getListOfFiles());
        HashMap<String,ArrayList<String>> hm = pfl.getListOFPeersWithFiles(llof); 
        return hm;
}



//For Peer Node
HashMap<String, ArrayList<String>> getFilesToRequestPerPeer(HashMap<String,ArrayList<String>> hm)
{
   ArrayList<String> lof = self.getListOfFiles();   
   HashMap<String, ArrayList<String>> filesPerPeer = new HashMap<String, ArrayList<String>>();
   
   int i;

   for(i=0;i<lof.size();i++)
   {
	   if(hm.containsKey(lof.indexOf(i)))
		   hm.remove(lof.indexOf(i));
   }

  Set set = hm.entrySet();
      // Get an iterator
  Iterator i = set.iterator();
      // Display elements
  while(i.hasNext()) 
   {
         Map.Entry me = (Map.Entry)i.next();
         if(!filesPerPeer.containsKey(me.getValue()))
         {
           ArrayList<String> files = new ArrayList<String>();
           files.add(me.getValue())
           filesPerPeer.put(me.getKey(),me.getValue());
         }
   } 

      
}



