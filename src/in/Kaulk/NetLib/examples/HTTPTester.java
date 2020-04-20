package in.Kaulk.NetLib.examples;

import in.Kaulk.NetLib.http.HTTPServer;

public class HTTPTester {

    public static void main(String args[]){

        HTTPServer server = new HTTPServer(5500,1024,"C:\\Users\\philo\\Documents\\GitHub\\JavaNetworkLibrary\\");
        server.startup();

    }


}
