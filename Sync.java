import java.io.*;
import java.net.*;
import java.util.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.sql.Timestamp;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class Sync implements Runnable{
    ListOfFiles files;
    ListOfPeers listOfPeers;
    int count1=0,count2=0;
    boolean firstTime=true;
    boolean arrayListSent = false;
    
    Sync(ListOfPeers listOfPeers)
    {
        this.listOfPeers = listOfPeers;
    }
    
    public void run()
    {
        boolean ret = false;
        ListOfFiles lof = listOfPeers.getSelf().getListOfFiles();
        ArrayList<String> arrayOfFiles = new ArrayList<String>();
        PeerNode self = listOfPeers.getSelf();
        
        
        /* Keep checking if any changes have been made to the shared directory */
        while(true){
            
            PeerNode masterNode = listOfPeers.getMaster();
            lof.getList();
            //Send to Controller
            if(masterNode!=null)
            {
                if(lof.getArrayListOfFiles().size() != 0){
                 if(firstTime)
                         {
                                 firstTime=false;
                                //java.util.Date date= new java.util.Date();
                                //Timestamp t = new Timestamp(date.getTime());
                                SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");

                                final TimeZone utc = TimeZone.getTimeZone("UTC");
                                dateFormatter.setTimeZone(utc);

                                String t = dateFormatter.format(new java.util.Date());
                                System.out.println("Init "+ t);
                         }
                    JSONObject obj = JSONManager.getJSON(lof.getList());// make the object
                    if(obj==null)
                    {
                        //System.out.println("Sync:run:Obj is null");
                        try
                        {
                        Thread.sleep(3000);
                        }

                        catch(Exception e)
                        {
                        }
                        continue;
                    }
                    if(masterNode.isCloud()){
                        self.sendMessage(obj);
                        //System.out.println("Sync:run:Sending arraylist to cloud");
                    }else if(arrayListSent == false){
                        Thread client = new Thread(new TcpClient(masterNode.getIPAddress(), "60010", obj, listOfPeers));
                        client.start();
                        arrayListSent = true;
                        //System.out.println("Sync:run:Starting Client thread !!!!!!!!!!!!!");
                    }
                }
            }
            count2=lof.getArrayListOfFiles().size();
            if(count2!=count1)
            {
                java.util.Date date= new java.util.Date();
                Timestamp t = new Timestamp(date.getTime());
                System.out.println("\n Number of Files Received till "+ t + " is: "+ count2);
                count1=count2;
            }
 
            /* Call seekFromPeer() on the list of files received from the controller */
            //HashMap<String, ArrayList<String>> hmFilesPeers = getFilesToRequestPerPeer(listOfPeers.getSelf().getHashMapFilePeer());
            
            /*

            Set mappingSet = getFilesToRequestPerPeer(listOfPeers.getSelf().getHashMapFilePeer(),listOfPeers.getSelf().getListOfFiles().getArrayListOfFiles()).entrySet();
   
            /*Collection.shuffle(mappingSet);
            Iterator itr =  mappingSet.iterator();
            
            while(itr.hasNext()){
                Map.Entry<String, ArrayList<String>> entry = (Map.Entry<String, ArrayList<String>>)itr.next();
                ret = seekFromPeer(String.valueOf(entry.getKey()), entry.getValue(), masterNode==null ? listOfPeers.getSelf().isCloud() : masterNode.isCloud());//Instead of Index 0 seek from peer based on Algo.
                if(ret == false){
                    //System.out.println("Sync:run:Seeking from Peer failed\n");
                    //listOfPeers.printPeerList();
                }
            }*/


             //Alogrithm 2 -> Randomized

             HashMap<String,ArrayList<String>> fileToPeersMap = getFilesToRequestPerPeer(listOfPeers.getSelf().getHashMapFilePeer(),listOfPeers.getSelf().getListOfFiles().getArrayListOfFiles());
             HashMap<String,ArrayList<String>> peerToFilesMap = new HashMap<String,ArrayList<String>>();
             Random rand = new Random(); 
     
             Set mappingSet = fileToPeersMap.entrySet();    
             Iterator itr =  mappingSet.iterator();
      		 //System.out.println("HashMap of fileToPeerMap is :"+ fileToPeersMap);
		//	 System.out.println("Temp:"+listOfPeers.getSelf().getHashMapFilePeer());
             while(itr.hasNext()){
                 Map.Entry<String, ArrayList<String>> entry = (Map.Entry<String, ArrayList<String>>)itr.next();
                 ArrayList<String> listofPeerHavingTheFile = entry.getValue();
                 String randomPeerId = listofPeerHavingTheFile.get(rand.nextInt(listofPeerHavingTheFile.size()));
                 if(peerToFilesMap.containsKey(randomPeerId))
                 {
                    ArrayList<String> listOfFileForPeer = peerToFilesMap.get(randomPeerId);
                    listOfFileForPeer.add(entry.getKey());
                 }  
                 else
                 {
                    ArrayList<String> listOfFileForPeer = new ArrayList<String>();
                    listOfFileForPeer.add(entry.getKey());
                    peerToFilesMap.put(randomPeerId,listOfFileForPeer);
                 }
             }
             
             mappingSet = peerToFilesMap.entrySet();    
             //System.out.println("HashMap of peerToFilesMap is :"+ peerToFilesMap);
			 itr =  mappingSet.iterator();
       		  
             while(itr.hasNext()){
                 Map.Entry<String, ArrayList<String>> entry = (Map.Entry<String, ArrayList<String>>)itr.next();
                 Collections.shuffle(entry.getValue());
                 ret = seekFromPeer( entry.getValue(), entry.getKey(), masterNode==null ? listOfPeers.getSelf().isCloud() : masterNode.isCloud());//Instead of Index 0 seek from peer based on Algo.
             }

/*Old.....Remove after commit
             ArrayList<String> randList = new ArrayList<String>();
             for(String k : mappingSet.keySet()) 
             {
               randList.add(k);
             }


             Collections.shuffle(randList);
             for(int j=0;j<randList.size();j++)
             {
                ArrayList<String> randPeerList = mappingSet.get(randList.get(j));
                ret = seekFromPeer(randList.get(j), randPeerList, masterNode==null ? listOfPeers.getSelf().isCloud() : masterNode.isCloud());//Instead of Index 0 seek from peer based on Algo.
                if(ret == false){
                    //System.out.println("Sync:run:Seeking from Peer failed\n");
                    //listOfPeers.printPeerList();
                }
             }


*/
             

            
            //listOfPeers.printPeerList();
            if(listOfPeers.getMaster() == null) /*I am the master*/
            {
                /* Get your own Lof */

                if(listOfPeers.getList().size() !=0){}
                  //System.out.println("Sync:run:I am the master and number of nodes in the list are" + listOfPeers.getList().size() );
                else
                    {
                        System.out.println("Sync:run:Looks like I am the only one here!");
                        try
                        {
                            Thread.sleep(3000);
                        }

                        catch(Exception e)
                        {
                        }

                        continue;
                    }
                
                listOfPeers.getSelf().setHashMapFilePeer( getFilesToRequestPerPeerMaster(listOfPeers));
                //System.out.println("Sync.java: Global Hashmap of controller");
                //print(listOfPeers.getSelf().getHashMapFilePeer());

                SortedSet<PeerNode> peerList =listOfPeers.getList();
                Iterator<PeerNode> it = peerList.iterator();

                while (it.hasNext())
                {
                    PeerNode peerNode = it.next();
                    HashMap<String, ArrayList<String>> hmFilesPeers = getFilesToRequestPerPeer(listOfPeers.getSelf().getHashMapFilePeer(),peerNode.getListOfFiles().getArrayListOfFiles());

                    //System.out.print("\nThe File list of " + peerNode.getId() + "is:");
                    //peerNode.getListOfFiles().printFileList();

                    //System.out.print("Sync.java: Hashmap from controller to " + peerNode.getId());
                    //print(hmFilesPeers);
                    if(!hmFilesPeers.isEmpty() ){
                        JSONObject obj = JSONManager.getJSON(hmFilesPeers);// make the object
                        Thread client = new Thread(new TcpClient(peerNode.getIPAddress(), "60010", obj, listOfPeers));
                        client.start();
                     
                    }
                }

                //System.out.println();
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
            System.out.print(entry.getKey() + ", ");
        }
        System.out.println();
    }
    
    boolean seekFromPeer(ArrayList<String> fileName, String peerId, boolean isCloud){
        //System.out.println("Files are:"+fileName + " and Peer Id is:"+peerId);
        if(fileName == null || peerId == null){
		//System.out.println("Seek from peer; " + fileName + " " + peerIds);
            return false;
        }

        PeerNode peer = listOfPeers.getPeerNode(peerId);
        JSONObject obj = JSONManager.getJSON(fileName, 1);
        if(peer == null){
            return false;
        }
        if(peer.isCloud() == true){
            listOfPeers.getSelf().sendMessage(obj);
            //System.out.println("Sync:run:Sending control message to cloud");
        }else{
            Thread client = new Thread(new TcpClient(peer.getIPAddress(), "60010", fileName, true, listOfPeers));
            client.start();
        }
        
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
        HashMap<String,ArrayList<String>> incrementalHashMap = new HashMap<String,ArrayList<String>>(hmFilesPeers);
        for(i=0;i<filesWithPeer.size();i++)
        {
            if(incrementalHashMap.containsKey(filesWithPeer.get(i)))
            {
                incrementalHashMap.remove(filesWithPeer.get(i));
            }
        }
        
        return incrementalHashMap;
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
/*
    ArrayList<String> randomizeHashMap(HashMap<String, ArrayList<String>> hmap)
    {
        if(hmap==null)
            return null;

        Set keyset = map.keySet().toArray();
        ArrayList<String> list = new ArrayList<String>(keySet);

        Collections.shuffle(list);
        return list;

    }
    */

}
