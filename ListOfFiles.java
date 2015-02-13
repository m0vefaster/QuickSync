import java.io.*;
import java.util.*;
import java.lang.*;

class ListOfFiles
{
      static String homeDir = System.getProperty("user.home");
      static String folder = "Documents/courses/cse221/proj1/files";
      static String path = homeDir + "/" + folder ;
      ArrayList<String> list;

   ListOfFiles( )
   {
   } 
   ArrayList<String> getList ( )
    {
        list = new ArrayList<String>();
        return getListHelper(list,path);
    }

	  ArrayList<String> getListHelper (ArrayList<String> list, String path)
	  {
		      File folder = new File(path);
		      File[] listOfFiles = folder.listFiles();

			  for(int i=0;i< listOfFiles.length;i++)
			  {
				    if(listOfFiles[i].isFile())
					{
						  //System.out.println("File is:" + listOfFiles[i].getAbsolutePath());
						  list.add(path +"/" + listOfFiles[i].getName());
					}
					else if(listOfFiles[i].isDirectory())
					{
						  //System.out.println("Directory is:" + listOfFiles[i].getAbsolutePath());
						  getListHelper(list,path+"/"+ listOfFiles[i].getName());
					}
			  }

			  return list;
	  }

          void setList(ArrayList<String> list){
              this.list = list;
          }
}
