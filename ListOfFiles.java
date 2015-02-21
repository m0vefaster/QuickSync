import java.io.*;
import java.util.*;
import java.lang.*;

class ListOfFiles implements Serializable
{
      static String homeDir = System.getProperty("user.home");
      static String folder = "QuickSync";
      static String path = homeDir + "/" + folder ;
      ArrayList<String> list;

   ListOfFiles( )
   {
   } 

   ListOfFiles(ArrayList<String> list)
   {
      this.list=list;
   }

   ArrayList<String> getArrayListOfFiles(){
       return this.list;
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
                list.add(listOfFiles[i].getName());
            }
            else if(listOfFiles[i].isDirectory())
            {
                //System.out.println("Directory is:" + listOfFiles[i].getAbsolutePath());
                //getListHelper(list,path+"/"+ listOfFiles[i].getName());
            }
        }

        return list;
    }

    void setList(ArrayList<String> list){
        this.list = list;
    }
}
