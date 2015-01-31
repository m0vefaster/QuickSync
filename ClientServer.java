public class ClientServer{
    public static void main(String[] args){
        Thread client = new Thread(new Client(args[0], args[1]));
        Thread server = new Thread(new Server());

        client.start();
        server.start();
    }
}
