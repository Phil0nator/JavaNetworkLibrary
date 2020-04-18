package com.NetLib;

import java.lang.ref.Cleaner;
import java.net.ServerSocket;


class ClientAccepter implements Runnable{
    Server S;
    boolean pause = false;
    ClientAccepter(Server s){
        S=s;
    }
    public void stop(){
        pause = true;
    }
    public void go(){
        pause=false;
    }
    public void run(){
        while (true){

            while(pause){}

            try {
                Client newClient = new Client(S, S.serverSocket.accept());
                S.pushClient(newClient);
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }

}

class PerClientActionable implements Runnable{
    Server server;
    Client client;
    ServerAction action;
    private boolean end = false;
    PerClientActionable(Server s, Client c, ServerAction a){
        server=s;
        client=c;
        action=a;
    }

    public void kill(){end=true;}

    public void run(){

        while(!end){
            try {
                action.run(client);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

}


interface ServerAction{
    public void run(Client c);
}



public class Server {

    Client[] clients;
    ServerSocket serverSocket;
    Client newestClient;
    private int clientCount = 0;
    private ClientAccepter ca = null;


    private ServerAction onNewClient;
    private ServerAction perClientAction;


    Server(int port, int maxClients){

        clients = new Client[maxClients];
        try {
            serverSocket = new ServerSocket(port);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    void pushClient(Client c){
        if(c==null){
            return;
        }
        if(clientCount+1 >= clients.length){
            return;
        }
        clients[clientCount] = c;
        clientCount++;
        newestClient = c;
        if(onNewClient != null){
            onNewClient.run(c);
        }
        if(perClientAction != null){
            c.startAction(new PerClientActionable(this, c, perClientAction));
        }
    }

    void remove(Client c){
        Client[] newClients = new Client[clients.length];
        int i = 0 ;
        for(Client A: clients){
            if(A != c){
                newClients[i] = A;
                i++;
            }
        }
        clients=newClients;
    }

    void stopAcceptingClients(){
        if(ca==null){
            throw new UnsupportedOperationException("You cannot stop accepting clients before you begin accepting clients");
        }
        ca.stop();
    }

    void startAcceptingClients(){
        if(ca==null){

            ca = new ClientAccepter(this);
            Thread t = new Thread(ca);
            t.start();

        }else{
            ca.go();
        }
    }

    void doToNewClients(ServerAction a){
        onNewClient = a;
    }

    void doToEachClient(ServerAction a){
        perClientAction = a;
        for(Client c:clients){
            c.startAction(new PerClientActionable(this, c, a));
        }
    }

}
