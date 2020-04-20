///////
//An example of how to use the in.Kaulk.NetLib server side
///////





import in.Kaulk.NetLib.*;
import in.Kaulk.NetLib.util.Logging.LoggingMode;

import java.io.File;

class doToClients implements ServerAction {

    public void run(Client c){

        Message msg = c.getNextMessage();
        if(msg!=null) {
            String s = (String) msg.getObject();
            System.out.println("Recieved: "+s);
            s+=" THE SERVER ADDED THIS";
            c.send(new Message(s));
        }

    }

}
class doOnJoin implements ServerAction{
    public void run(Client c){
        System.out.println("Joined");
    }
}

class doOnDiscon implements ServerAction{

    public void run(Client c){
        System.out.println("Disconnected "+c);
    }

}

public class ServerTest {

    public static void main(String[] args) {
	// write your code here
    Server server = new Server(5555,1024);
    server.doToEachClient(new doToClients());
    server.doToNewClients(new doOnJoin());
    server.doOnDisconnect(new doOnDiscon());
    server.setupLogger(new File("testlog.txt"), LoggingMode.extra_verbose);
    server.startAcceptingClients();
    System.out.println("Ready to accept");


    }
}
