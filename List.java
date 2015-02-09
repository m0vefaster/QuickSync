import java.io.File;
import java.util.*;

class List
{
	  static String homeDir = System.getProperty("user.home");
	  static String folder = "QuickSync";
	  static String path1 = homeDir + "/" + folder ;
      static String path2 = homeDir + "/" + "Documents/" +folder ; 
	   public static void main(String args[])
	   {
		   ListOfFiles lof1 = new ListOfFiles(path1);

		   ListOfFiles lof2 = new ListOfFiles(path2);

            ArrayList<ArrayList<String>> llof  =  new ArrayList<ArrayList<String>>();
		   llof.add(lof1.getList());
		   llof.add(lof2.getList());
		  
		   PeerFileList pfl = new PeerFileList();
		   HashMap<String,ArrayList<String>> lpfl = pfl.getListOfPeersWithFiles(llof);   
 
		   Iterator itr =  lpfl.entrySet().iterator();

		   while  ( itr.hasNext())
		   {
			    Map.Entry me = (Map.Entry)itr.next();
			    System.out.print ("\n\nFile Name is:" + me.getKey() + "\t List of Peers is :" );
				ArrayList<String> al = (ArrayList<String>) me.getValue();
				for(int i=0;i<al.size();i++)
				{
					 System.out.print(al.get(i) + ",");
				}
				
		   } 

		  String currentUsersHomeDir = System.getProperty("user.home");
		  System.out.println("\n\n\n" + currentUsersHomeDir); 
	   }
}
