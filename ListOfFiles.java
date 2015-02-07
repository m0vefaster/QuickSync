import java.io.*;
import java.util.*;

class ListOfFiles
{
	  
   String path ="/Users/vishalparekh/QuickSync/";

   ArrayList getList ( )
	  {
		       ArrayList<String> list = new ArrayList<String>();
			   
			   return getListHelper(list,path);
	  }

	  ArrayList getListHelper (ArrayList<String> list, String path)
	  {
		      File folder = new File(path);
		      File[] listOfFiles = folder.listFiles();

			  for(int i=0;i< listOfFiles.length;i++)
			  {
				    if(listOfFiles[i].isFile())
					{
						  System.out.println("File is:" + listOfFiles[i].getAbsolutePath());
						  list.add(path +"/" + listOfFiles[i].getName());
					}
					else if(listOfFiles[i].isDirectory())
					{
						  System.out.println("Directory is:" + listOfFiles[i].getAbsolutePath());
						  getListHelper(list,path+"/"+ listOfFiles[i].getName());
					}
			  }

			  return list;
	  }

}
