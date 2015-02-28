import java.io.*;

class Ping {

    public static void main(String[] args) {
      try
	  {
		PingPong p =new PingPong();
	    int val = p.getSysLatency(args[0]);
        System.out.println("Time required is:" + val);
	  }
      catch(Exception e)
	  {
	  }
	}
}
