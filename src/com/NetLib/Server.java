package com.NetLib;

import java.lang.ref.Cleaner;
import java.net.ServerSocket;

/**
 * ClientAccepter implements runnable to run in its own thread, and accept new clients
 * @see Server
 */
class ClientAccepter implements Runnable{
    /**
     * Parent Object
     */
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

    /**
     * Accept each client:
     *  Run the user-defined runOnClientJoin FunctionalInterface
     *  Push the client to the server's list
     */
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

/**
 * PerClientActionable also runs in its own thread, and is where
 * the ServerAction a user has defined will run. This is what runs for each
 * Client of the server to handle incoming and outgoing information.
 * @see ServerAction
 */
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

/**
 * A similar structure to Runnable, with the addition of a Client object parameter
 */
@FunctionalInterface
interface ServerAction{
    public void run(Client c);
}


/**
 * The container and handler for multiple clients
 */
public class Server {
    /**
     * Currently connected clients
     */
    volatile Client[] clients;
    /**
     * @see ServerSocket
     */
    volatile ServerSocket serverSocket;
    /**
     * The newest client to be added
     */
    volatile Client newestClient;

    volatile private int clientCount = 0;
    /**
     * @see ClientAccepter
     */
    volatile private ClientAccepter ca = null;

    /**
     * Callbacks:
     * @see ServerAction
     * @see FunctionalInterface
     */
    volatile private ServerAction onNewClient;
    volatile private ServerAction perClientAction;

    /**
     * Constructor
     * @param port defines the port to which the server's socket will be bound
     * @param maxClients defines the maximum number of clients that can be connected to the server at one time
     */
    Server(int port, int maxClients){

        clients = new Client[maxClients];
        try {
            serverSocket = new ServerSocket(port);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * Adds a new Client
     * @param c the new Client
     */
    synchronized void pushClient(Client c){
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

    /**
     * Remove an existing client
     * The client that is will become an unsafe reference
     * @param c a reference to the client to be removed
     */
    synchronized void remove(Client c){
        Client[] newClients = new Client[clients.length];
        int i = 0 ;
        for(Client A: clients){
            if(A != c){
                newClients[i] = A;
                i++;
            }
        }
        c.killThread();
        c.disconnect();
        c = null;
        clients=newClients;
    }

    /**
     * Stops the thread that accepts new clients
     */
    public synchronized void stopAcceptingClients(){
        if(ca==null){
            throw new UnsupportedOperationException("You cannot stop accepting clients before you begin accepting clients");
        }
        ca.stop();
    }
    /**
     * Starts or re-starts the thread that accepts new clients
     */
    public synchronized void startAcceptingClients(){
        if(ca==null){

            ca = new ClientAccepter(this);
            Thread t = new Thread(ca);
            t.start();

        }else{
            ca.go();
        }
    }

    /**
     * Define a ServerAction (FunctionalInterface) to act on every client that joins
     * @param a FunctionalInterface
     * @see ServerAction
     */
    public void doToNewClients(ServerAction a){
        onNewClient = a;
    }

    /**
     * Define a ServerAction (FunctionalInterface) which will act on each client in a seperate thread
     * @param a FunctionalInterface
     * @see ServerAction
     * (warning) the action given will be run in a seperate thread for each client
     */
    void doToEachClient(ServerAction a){
        perClientAction = a;
        for(Client c:clients){
            c.startAction(new PerClientActionable(this, c, a));
        }
    }

    /**
     * Send a single message to all connected clients
     * @param m Message Object
     * @see Message
     */
    synchronized void sendToAll(Message m){
        for(Client c: clients){
            c.send(m);
        }
    }

}
