
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
HashMap<String,ArrayList<String>> getHashMapOfFile(PeerNode self)
{
        ArrayList<ArrayList<String>> llof = peerList.getPeerListOfFiles();
        PeerFileList pfl = new PeerFileList();
        llof.add(self.getListOfFiles());
        HashMap<String,ArrayList<String>> hm = pfl.getListOFPeersWithFiles(llof); 
        return hm;
}



//For Peer Node



