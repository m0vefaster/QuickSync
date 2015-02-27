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
    
    ArrayList<String> getArrayListOfFiles()
    {
        return this.list;
    }
    
    ArrayList<String> getList ( )
    {
        if(list==null)
        {
         list = new ArrayList<String>();
         return removeAbsolutePath(getListHelper(list,path));
        }
        else
        {
            ArrayList<String> list2;
            list2 = new ArrayList<String>();
            list2 = removeAbsolutePath(getListHelper(list2,path));

            if(list2==null)
            {
                list=null;
                return list;
            }

            int i;
            for(i=0;i<list.size();i++)
            {
                list2.remove(list.get(i));
            }

            list=list2;
            return list;
        }
    }
    
    ArrayList<String> getListHelper (ArrayList<String> list, String path)
    {
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();
        
        for(int i=0;i< listOfFiles.length;i++)
        {
            if(listOfFiles[i].isFile()  && listOfFiles[i].getName().charAt(0) != '.')
            {
                list.add(path+"/"+ listOfFiles[i].getName());
            }
            else if(listOfFiles[i].isDirectory() && listOfFiles[i].getName().charAt(0) != '.')
            {
                getListHelper(list,path+"/"+ listOfFiles[i].getName());
            }
        }
        
        return list;
    }
    
    void setList(ArrayList<String> list){
        this.list = list;
    }

    ArrayList<String> removeAbsolutePath(ArrayList<String> list)
    { 
      if(list==null)
        return null;

      for(int i=0;i<list.size();i++)
      {
        String fileName = list.get(i);
        fileName=fileName.substring(fileName.indexOf("QuickSync")+10);
        list.set(i,fileName);
      }
      
      return list;
    }

}
