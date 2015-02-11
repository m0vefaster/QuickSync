public class Sync{
    private ConnectionTable table;
    private ListOfFiles files;
    private SendList toBeSent;
    private HashMap< String,ArrayList< String > > map;

    public void run(){
        /* Keep checking if any changes have been made to PeerFileList and HashTable*/ 
        String controllerAddress = args[7];       //Cloud IP 
        String controllerPort = args[2];          //Cloud listen port

        //send files
        ListOfFiles files = new ListOfFiles(".");
        SendList toBeSent = new SendList(controllerAddress, controllerPort);
        toBeSent.sendList(files.getList());

        //TODO:get peer files        
        HashMap< String,ArrayList< String > > map = new HashMap<String, ArrayList<String> >();

        Iterator< Map.Entry<String, ArrayList<String> > > it = map.entrySet().iterator();




        
