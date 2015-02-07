import java.io.File;
import java.util.*;

class List
{
	  static String path1 ="/Users/vishalparekh/QuickSync/";
      static String path2 ="/Users/vishalparekh/Documents/QuickSync/"; 
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
	   }
}
