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

public class Server implements Runnable{
    @Override
    public void run() 
    {
	ServerSocket ss;
	File myFile;
	byte[] aByte = new byte[1];
	try {
	    ss = new ServerSocket(60010);
	    Socket s = ss.accept();
	    InputStream inFromServer = s.getInputStream();
	    DataInputStream in =
	       new DataInputStream(inFromServer);
	    String line = null;
	    line = in.readUTF();
	    System.out.println(line);
		myFile = new File(line);
		if (myFile.createNewFile())
		{
	        System.out.println("File is created!");
	    }
		else
		{
	        System.out.println("File already exists.");
	    }
	    byte[] mybytearray = new byte[1024];
	    InputStream is = s.getInputStream();
	    FileOutputStream fos = new FileOutputStream(line);
	    BufferedOutputStream bos = new BufferedOutputStream(fos);
	    int bytesRead = is.read(mybytearray, 0, mybytearray.length);
	    bos.write(mybytearray, 0, bytesRead);
	    bos.close();
	    ss.close();
	    
	} catch (Exception e) {
	    e.printStackTrace();
	}

    }
}
