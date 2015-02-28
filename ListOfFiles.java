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
        System.out.println("===============================Construncting with LOF as empty");
    }
    
    ListOfFiles(ArrayList<String> list)
    {
        System.out.println("===============================Construncting with LOF with list");
        this.list=list;
    }
    
    ArrayList<String> getArrayListOfFiles()
    {
        return this.list;
    }

    ArrayList<String> getList ( )
    {

         System.out.println("The list is null");
         list = new ArrayList<String>();
         return removeAbsolutePath(getListHelper(list,path));
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
        System.out.println("Entering Set List function");
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

    void printFileList()
    {
        System.out.print("\nThe File list is:");
        if(list == null){
            return;
        }

        Iterator<String> itr = list.iterator();
        String file;
        while(itr.hasNext()){
            file = itr.next();
            System.out.print(file+",");
        }
        System.out.println();
    }

}
