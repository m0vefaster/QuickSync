import java.io.*;
import java.util.*;

class ListOfFiles
{
	  
//      String path ="\~/QuickSync";
   String path ="/Users/vishalparekh/QuickSync/";
	  ArrayList getList ( )
	  {
		       ArrayList<String> list = new ArrayList<String>();
			   
			   return getListHelper(list);
	  }

	  ArrayList getListHelper (ArrayList<String> list)
	  {
		      File folder = new File(path);
		      File[] listOfFiles = folder.listFiles();

			  System.out.println("list:" + listOfFiles.length);
			  for(int i=0;i< listOfFiles.length;i++)
			  {
				    if(listOfFiles[i].isFile())
					{
						 list.add(listOfFiles[i].getName());
					}
			  }

			  return list;
	  }

}
