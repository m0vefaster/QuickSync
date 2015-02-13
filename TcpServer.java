import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class TcpServer implements Runnable
{
    private ServerSocket ss;
    private Socket s;
    public TcpServer(ServerSocket ss, Socket s)
    {
        this.ss = ss;
        this.s = s; 
    }
    @Override
    public void run() 
    {
        File myFile;
        byte[] aByte = new byte[1];
        System.out.println("Server running "+s.toString());
        try {
            while(true){
                InputStream inFromServer = s.getInputStream();
                DataInputStream in = new DataInputStream(inFromServer);
                String line,str = null;
                line = in.readUTF();
                JSONObject obj = (JSONObject)(JSONManager.convertStringToJSON(line));
                if(obj.get("type").equals("Control"))
                {
                  str = (String)obj.get("value");  
                  if(str.compareTo("***EOF***") == 0){
                      System.out.println("Server: EOF received");
                      break;
                  }else{
                      System.out.println("Server: File request received for " + line);
                  }
                }
                /* Send the corresponding file */
                
                myFile = new File(str);
                JSONObject toSend = JSONManager.getJSON(myFile);
                System.out.println("---"+toSend.toString());
                byte[] mybytearray = new byte[(int) myFile.length()];
                //BufferedInputStream bis = new BufferedInputStream(new FileInputStream(myFile));
                //bis.read(mybytearray, 0, mybytearray.length);
                OutputStream os = s.getOutputStream();
                os.write(toSend.toString().getBytes(), 0, toSend.toString().length());
                os.flush(); 
                System.out.println("Server: Sent file " + str);
            }
            s.close();
            System.out.println("Server: closing socket "+s.toString());
            
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
