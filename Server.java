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

public class Server implements Runnable
{
    private ServerSocket ss;
    private Socket s;
    public Server(ServerSocket ss, Socket s)
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
        InputStream inFromServer = s.getInputStream();
        DataInputStream in =
           new DataInputStream(inFromServer);
        String line = null;
        line = in.readUTF();
        System.out.println(line);
        myFile = new File(line);
        if (myFile.createNewFile())
        {
            System.out.println("Server: File is created!");
        }
        else
        {
            System.out.println("Server: File already exists.");
        }
        byte[] mybytearray = new byte[1024];
        InputStream is = s.getInputStream();
        FileOutputStream fos = new FileOutputStream(line);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        int bytesRead = is.read(mybytearray, 0, mybytearray.length);
        bos.write(mybytearray, 0, bytesRead);
        bos.close();
        ss.close();
        System.out.println("Server: closing socket "+s.toString());
        
    } catch (Exception e) {
        e.printStackTrace();
    }

    }
}
