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
                    }else{
                        Thread client = new Thread(new TcpClient(masterNode.getIPAddress(), "60010", obj));
                        client.start();
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
            

            Set mappingSet = getFilesToRequestPerPeer(listOfPeers.getSelf().getHashMapFilePeer(),listOfPeers.getSelf().getListOfFiles().getArrayListOfFiles()).entrySet();
            //System.out.println("Sync:run:Printing mappingSet:" + mappingSet);
            //System.out.println("Sync:run:Printing Global HashMap:" );
            //print(listOfPeers.getSelf().getHashMapFilePeer());
            //System.out.println("Sync:run:Prinintg Array List:"+listOfPeers.getSelf().getListOfFiles().getArrayListOfFiles());
            Iterator itr =  mappingSet.iterator();
            
            while(itr.hasNext()){
                Map.Entry<String, ArrayList<String>> entry = (Map.Entry<String, ArrayList<String>>)itr.next();
                ret = seekFromPeer(String.valueOf(entry.getKey()), entry.getValue(), masterNode==null ? listOfPeers.getSelf().isCloud() : masterNode.isCloud());//Instead of Index 0 seek from peer based on Algo.
                if(ret == false){
                    //System.out.println("Sync:run:Seeking from Peer failed\n");
                    //listOfPeers.printPeerList();
                }
            }
            
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
                    print(hmFilesPeers);
                    if(!hmFilesPeers.isEmpty()){
                        JSONObject obj = JSONManager.getJSON(hmFilesPeers);// make the object
                        Thread client = new Thread(new TcpClient(peerNode.getIPAddress(), "60010", obj));
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
            //System.out.print(entry.getKey() + ", ");
        }
        //System.out.println();
    }
    
    boolean seekFromPeer(String fileName, ArrayList<String> peerIds, boolean isCloud){
        PeerNode peer = null;
        //System.out.println("FileName is:"+fileName + " and Peer Id is:"+peerIds);
        if(fileName == null || peerIds == null){
		//System.out.println("Seek from peer; " + fileName + " " + peerIds);
            return false;
        }
        for(String peerId: peerIds)
        {
            peer = listOfPeers.getPeerNode(peerId);
            if(peer != null){
                break;
            }
            
        }
        if(peer==null){
		//System.out.println("Seek from peer; Peer not found for peerId " + peerIds);
            return false;
	}

        JSONObject obj = JSONManager.getJSON(fileName);
        if(peer.isCloud() == true){
            listOfPeers.getSelf().sendMessage(obj);
            //System.out.println("Sync:run:Sending control message to cloud");
        }else{
            Thread client = new Thread(new TcpClient(peer.getIPAddress(), "60010", obj));
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
    void sendMessage(JSONObject obj, Socket client)
    {
        if(client == null){
            return;
        }
        try
        {
            OutputStream outToServer = client.getOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(outToServer);
            byte[] outputArray = obj.toString().getBytes();
            int len = obj.toString().length();
            out.writeObject(len);
            out.writeObject(outputArray);
            //client.shutdownOutput();
            //out.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    */
}
