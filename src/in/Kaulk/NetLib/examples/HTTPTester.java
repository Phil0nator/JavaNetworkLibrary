package in.Kaulk.NetLib.examples;

import in.Kaulk.NetLib.Client;
import in.Kaulk.NetLib.ServerAction;
import in.Kaulk.NetLib.http.HTTPServer;

import java.awt.*;


class PrintJoinMessage implements ServerAction{
    public void run(Client c){
        System.out.println("CLient JoInEd");
    }
}

public class HTTPTester {

    public static void main(String args[]){

        HTTPServer server = new HTTPServer(5500,1024,"<PUT YOUR DIRECTORY HERE>");
        server.subServer.doToNewClients(new PrintJoinMessage());
        server.subServer.setLoggerMaxEventstackSize(4096);
        server.startup();

    }


}
