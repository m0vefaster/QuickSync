public class ClientServer{
    public static void main(String[] args){
        Thread client = new Thread(new Client(args[0], args[1], args[2]));
        Thread server = new Thread(new Server());

        //if(Integer.parseInt(args[2]) == 1){
            client.start();
        //}else if(Integer.parseInt(args[2]) == 0){
            server.start();
        //}
    }
}
