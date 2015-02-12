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
                String line = null;
                line = in.readUTF();
                if(line.compareTo("***EOF***") == 0){
                    System.out.println("Server: EOF received");
                    break;
                }else{
                    System.out.println("Server: File request received for " + line);
                }

                /* Send the corresponding file */
                myFile = new File(line);
                byte[] mybytearray = new byte[(int) myFile.length()];
                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(myFile));
                bis.read(mybytearray, 0, mybytearray.length);
                OutputStream os = s.getOutputStream();
                os.write(mybytearray, 0, mybytearray.length);
                os.flush(); 
                System.out.println("Server: Sent file " + line);
            }
            s.close();
            System.out.println("Server: closing socket "+s.toString());
            
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
