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

public class Server {
    public static void main(String[] args) throws IOException {

        startServer();
        /*startSender();*/
    }

    public static void startSender() {
        (new Thread() {
            @Override
            public void run() {
                try {
                    Socket s = new Socket("localhost", 60010);
                    BufferedWriter out = new BufferedWriter(
                            new OutputStreamWriter(s.getOutputStream()));

                    while (true) {
                        out.write("Hello World!");
                        out.newLine();
                        out.flush();

                        Thread.sleep(200);
                    }

                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void startServer() {
        (new Thread() {
            @Override
            public void run() {
                ServerSocket ss;
		File myFile = new File("temp.c");
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
		    byte[] mybytearray = new byte[1024];
		    InputStream is = s.getInputStream();
		    FileOutputStream fos = new FileOutputStream("s");
		    BufferedOutputStream bos = new BufferedOutputStream(fos);
		    int bytesRead = is.read(mybytearray, 0, mybytearray.length);
		    bos.write(mybytearray, 0, bytesRead);
		    bos.close();
		    ss.close();
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }

		}
        }).start();
    }
}
