# QuickSync
An application that combines the benefits of Cloud and P2P networks to provide modes for faster sync , conserve battery life ondevices , cost reduction on mobile devices and high availability


No.	Class Name (.java files) 	Class Definition
1	QuickSync	It is the Main Class and starts the UDP Client and Server.  Starts the TCP Server. Connects to the Cloud.
2	UdpClient	Runs every 3 seconds and emits a IP-Broadcast packet to notify its identity and weight.
3	UdpServer	Listens for UDP Messages. On receiving a broadcast from another peer, add it to its peer list.
4	TcpClient	To emit TCP messages like File List and control messages.
5	TcpServer	Listens for control messages. Based on different control messages takes different actions.
6	TcpClientCloud	Similar to TCPclient but specifically for the Cloud as a Long Lived Connection.
7	TcpServerCloud	Similar to TCPServer but specifically for the Cloudm as a Long Lived Connection.
8	ListOfPeers	Maintains the list of peers and performs addition, deletion and other operations on the peers
9	PeerNode	Maintains information for the 
10	JSONManager	Creates different JSON Objects
11	ListOfFiles	Find the files that the peer posseses
12	NodeWeightCalculation	Calculation of weights of Nodes based on different parameters.


