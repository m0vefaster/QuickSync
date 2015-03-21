import java.io.*;
import java.util.*;
import java.net.*;

class ListOfPeers {
  SortedSet < PeerNode > peerList = new TreeSet < PeerNode > (new Comp());
  int offset;
  PeerNode mySelf;
  HashMap < String, String > filesInSync = new HashMap < String, String > ();
  ListOfPeers(PeerNode mySelf) {
     
    this.mySelf = mySelf;
  }

  PeerNode getSelf() {
    return mySelf;
  }

  class Comp implements Comparator < PeerNode > {@Override
    public int compare(PeerNode pn1, PeerNode pn2) {
      if (pn2.getWeight() > pn1.getWeight()) return -1;
      if (pn1.getWeight() == pn2.getWeight()) return 0;
      return 1;
    }
  }

  boolean addPeerNode(PeerNode newNode) {
    if (present(newNode)) {
      System.out.println("ListOfPeers:addPeerNode:Peer List is: " + peerList);
      return false;
    }

    peerList.add(newNode);
    System.out.println(peerList);
    return true;
  }


  boolean removePeerNode(PeerNode removeNode) {
    if (!present(removeNode)) return false;

    peerList.remove(removeNode);
    return true;
  }

  boolean removePeerNode(String peerId) {
    Iterator < PeerNode > itr = peerList.iterator();

    while (itr.hasNext()) {
      PeerNode node = itr.next();
      if (node.getId().equals(peerId)) {
        removePeerNode(node);
      }
      return true;
    }
    return false;
  }

  PeerNode getMaster() {
    if (peerList.size() > 0 && (peerList.first().getWeight() < getSelf().getWeight())) return peerList.first();

    return null;
  }

  boolean present(PeerNode node) {
    Iterator < PeerNode > itr = peerList.iterator();

    while (itr.hasNext()) {
      if (itr.next().getId() == node.getId()) return true;
    }

    return false;
  }

  PeerNode getPeerNode(String peerId) {
    Iterator < PeerNode > itr = peerList.iterator();
    PeerNode node;

    while (itr.hasNext()) {
      node = itr.next();
      if (node.getId().equals(peerId)) {
        return node;
      }
    }

    return null;
  }


  SortedSet < PeerNode > getList() {
    return peerList;
  }

  PeerNode getPeerNodeFromIP(String ipAddress) {
    Iterator < PeerNode > itr = peerList.iterator();
    PeerNode node;

    while (itr.hasNext()) {
      node = itr.next();
      if (node.getIPAddress().equals(ipAddress)) {
        return node;
      }
    }

    return null;
  }


  void printPeerList() {
    Iterator < PeerNode > itr = peerList.iterator();
    PeerNode node;
     
    while (itr.hasNext()) {
      node = itr.next();
       
    }
     
  }

  PeerNode getPeerNodeFromSocket(Socket s) {
    Iterator < PeerNode > itr = peerList.iterator();
    PeerNode node;
    while (itr.hasNext()) {
      node = itr.next();
       
      if (node.getSocket() == s) {
         
        return node;
      }

    }

    return null;
  }


  void updateHashMapBeforeRemovingNode(PeerNode nodeToBeRemoved) {
    HashMap < String, ArrayList < String >> hmFilesPeers = mySelf.getHashMapFilePeer();
    Set mappingSet = hmFilesPeers.entrySet();
    String removeNodeId = nodeToBeRemoved.getId();
    Iterator itr = mappingSet.iterator();
     
     
    while (itr.hasNext()) {
      Map.Entry < String, ArrayList < String >> entry = (Map.Entry < String, ArrayList < String >> ) itr.next();
      ArrayList < String > allPeers = entry.getValue();
      int i = 0;
      while (i < allPeers.size()) {
         
        PeerNode node = getPeerNode(allPeers.get(i));
        if (node != null && node.getId().equals(removeNodeId)) {
           
          allPeers.remove(node.getId());
          break;
        } else i++;
      }
    }

     

  }


  ArrayList < String > addFilesInTransit(ArrayList < String > fileList, String peerId) {
    int i;
     
     
    ArrayList < String > listOfFileToSend = new ArrayList < String > ();
    for (i = 0; i < fileList.size(); i++) {
      if (!syncMap(fileList.get(i), peerId, "contains")) {
        listOfFileToSend.add(fileList.get(i));
        syncMap(fileList.get(i), peerId, "add");
      }
    }

     
    return listOfFileToSend;

  }

  boolean removeFileInTransit(String fileName, String peerId) {
     

    if (!syncMap(fileName, peerId, "contains")) {
      return false;
    }
    syncMap(fileName, peerId, "remove");
    return true;
  }

  synchronized boolean syncMap(String fileName, String peerId, String type) {
     
     

     
    switch (type) {
      case "add":
        if (filesInSync.containsKey(fileName)) {
           
          return false;
        }
         
        filesInSync.put(fileName, peerId);
        break;
      case "remove":
        if (!filesInSync.containsKey(fileName)) {
           
          return false;
        }
         
        filesInSync.remove(fileName);
        break;
      case "contains":
        return filesInSync.containsKey(fileName);
      case "clearForPeer":
        System.out.println("Size of filesInSync is:" + filesInSync.size());
        Set mappingSet = filesInSync.entrySet();
        Iterator itr = mappingSet.iterator();
        ArrayList < String > removeList = new ArrayList < String > ();
        while (itr.hasNext()) {
          Map.Entry < String, String > entry = (Map.Entry < String, String > ) itr.next();

          if (entry.getValue().equals(peerId)) {
            removeList.add(entry.getKey());
          }
        }

        for (int z = 0; z < removeList.size(); z++) {
          filesInSync.remove(removeList.get(z));
        }
        System.out.println("Size of filesInSync is:" + filesInSync.size());
        break;
      case "print":
        if (filesInSync == null) return false;
        System.out.print(filesInSync);
        break;
      case "default":
         
        return false;
    }
     
    return true;
  }

  void setOffset(int offset) {
    this.offset = offset;
  }
  int getOffset() {
    return offset;
  }








}