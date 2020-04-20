package in.Kaulk.NetLib.examples;///////
//An example of how to use the in.Kaulk.NetLib server side
///////





import in.Kaulk.NetLib.*;
import in.Kaulk.NetLib.util.Logging.LoggingMode;

import java.io.File;

//Action to do to each client
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
//do on join for each client
class doOnJoin implements ServerAction{
    public void run(Client c){
        System.out.println("Joined");
    }
}
//do on disconnect for each client
class doOnDiscon implements ServerAction{

    public void run(Client c){
        System.out.println("Disconnected "+c);
    }

}

public class ServerTest {

    public static void main(String[] args) {
	// setup server object
    Server server = new Server(5555,1024);
    //setup callbacks
    server.doToEachClient(new doToClients());
    server.doToNewClients(new doOnJoin());
    server.doOnDisconnect(new doOnDiscon());
    //optional logger setup
    server.setupLogger(new File("testlog.txt"), LoggingMode.extra_verbose);

    //begin accepting clients
    server.startAcceptingClients();
    System.out.println("Ready to accept");
    //the server is all set up, and will run in its own threads

    }
}
