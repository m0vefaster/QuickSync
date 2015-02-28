import java.io.*;
class PingPong
{

public static int getSysLatency(String ip) throws IOException{
     long t3 = System.currentTimeMillis();

        Process p2 = Runtime.getRuntime().exec("ping -n 1 " + ip );

        try {
            p2.waitFor();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        t3 = System.currentTimeMillis()-t3;

        return (int) t3;
}
}
