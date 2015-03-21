# QuickSync
An application that combines the benefits of Cloud and P2P networks to provide modes for faster sync , conserve battery life ondevices , cost reduction on mobile devices and high availability


## Steps to Run

- Create a folder 'QuickSync' in your directory
- Add the path to jar files to CLASSPATH

``export CLASSPATH=~/QuickSync/json-simple-1.1.1.jar:~/QuickSync/java-json.jar:.``
- Compile the code

``javac *.java``

- Run the Code(QuickSync) with the hostname 

##Class Definition
####QuickSync
It is the Main Class and starts the UDP Client and Server.  Starts the TCP Server. Connects to the Cloud.
####	UdpClient	
Runs every 3 seconds and emits a IP-Broadcast packet to notify its identity and weight.
####UdpServer
Listens for UDP Messages. On receiving a broadcast from another peer, add it to its peer list.
####TcpClient
To emit TCP messages like File List and control messages.
####TcpServer
Listens for control messages. Based on different control messages takes different actions.
####TcpClientCloud
Similar to TCPclient but specifically for the Cloud as a Long Lived Connection.
####TcpServerCloud
Similar to TCPServer but specifically for the Cloudm as a Long Lived Connection.
####ListOfPeers
Maintains the list of peers and performs addition, deletion and other operations on the peers
####PeerNode
Maintains information for the 
####JSONManager
Creates different JSON Objects
####ListOfFiles	
Find the files that the peer posseses
####NodeWeightCalculation	
Calculation of weights of Nodes based on different parameters.


