package in.Kaulk.NetLib;

import in.Kaulk.NetLib.util.Encryption.EncryptionHandler;
import in.Kaulk.NetLib.util.Encryption.HardcodedKeyPair;
import in.Kaulk.NetLib.util.Events.ClientConnectEvent;
import in.Kaulk.NetLib.util.Events.ClientDisconnectEvent;
import in.Kaulk.NetLib.util.Events.ErrorEvent;
import in.Kaulk.NetLib.util.Events.Event;
import in.Kaulk.NetLib.util.Logging.LoggingMode;
import in.Kaulk.NetLib.util.Logging.ServerLogger;

import java.io.File;
import java.net.ServerSocket;

/**
 * ClientAccepter implements runnable to run in its own thread, and accept new clients
 * @see Server
 * @see Runnable
 * @see Server#doToNewClients(ServerAction)
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
                S.feedLoggingEvent(new ErrorEvent(e));
            }

        }
    }

}

/**
 * PerClientActionable also runs in its own thread, and is where
 * the ServerAction a user has defined will run. This is what runs for each
 * Client of the server to handle incoming and outgoing information.
 *
 * The ServerAction used is defined in
 * @see Server#doToEachClient(ServerAction)
 *
 *
 * Also:
 *
 * @see ServerAction
 * @see Runnable
 */
class PerClientActionable implements Runnable{
    Server server;
    Client client;
    ServerAction action;
    public boolean end = false;
    PerClientActionable(Server s, Client c, ServerAction a){
        server=s;
        client=c;
        action=a;
    }

    public synchronized void kill(){

        end=true;

    }

    public void run(){

        while(!end){
            try {
                action.run(client);
            }catch (Exception e){
                e.printStackTrace();
                server.feedLoggingEvent(new ErrorEvent(e));
                client.disconnect();
                kill();
            }
        }

    }

}


enum EncryptionLevel{

    RSA_1024, RSA_2048

}



/**
 * The container and handler for multiple clients
 */
public final class Server {
    /**
     * Currently connected clients
     * Note: Will not always be full, and will be allocated with null references
     *       Up to the size specified by the user in the constructor
     * @see Server#Server(int, int)
     */
    volatile private Client[] clients;
    /**
     * ServerSocket to generate new clients
     * @see ServerSocket
     */
    volatile ServerSocket serverSocket;
    /**
     * The newest client to be added
     */
    volatile private Client newestClient;

    volatile private int clientCount = 0;
    /**
     * ClientAccepter object
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
    volatile private ServerAction onDisconnect;


    /**
     *Logger object, configurable by user
     * @see ServerLogger
     * @see LoggingMode
     */
    volatile private ServerLogger logger = new ServerLogger(this, null, LoggingMode.off);

    /**
     * Asymetric Encryption is done through an EncryptionHandler Object:
     * @see EncryptionHandler
     */
    volatile public EncryptionHandler encryptionHandler;
    volatile public boolean doesEncrypt = false;
    /**
     * Constructor
     * @param port defines the port to which the server's socket will be bound
     * @param maxClients defines the maximum number of clients that can be
     *                   connected to the server at one time
     */
    public Server(int port, int maxClients){

        clients = new Client[maxClients];
        try {
            serverSocket = new ServerSocket(port);
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    /**
     * Secondary Constructor for encryption
     * @param port port to bind to
     * @param maxClients maximum number of clients to connect at one time
     */
    public Server(int port, int maxClients, HardcodedKeyPair hardcoded){
        this(port,maxClients);

        doesEncrypt=true;
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
        logger.feedNewActionType(new ClientConnectEvent(c));
    }

    /**
     * Remove an existing client
     * The client that is will become an unsafe reference
     * @param c a reference to the client to be removed
     */
    void remove(Client c){
        Client[] newClients = new Client[clients.length];
        int i = 0 ;
        for(Client A: clients){
            if(A != c){
                newClients[i] = A;
                i++;
            }
        }
        c.killThread();
        c.close();
        onDisconnect.run(c);
        logger.feedNewActionType(new ClientDisconnectEvent(c));
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
     * Define a ServerAction (FunctionalInterface) to act on each client that disconnects
     * @param a FunctionalInterface
     * @see ServerAction
     */
    public void doOnDisconnect(ServerAction a){
        onDisconnect = a;
    }

    /**
     * Define a ServerAction (FunctionalInterface) which will act on each client in a separate thread
     * @param a FunctionalInterface
     * @see ServerAction
     * (warning) the action given will be run in a seperate thread for each client
     */
    public void doToEachClient(ServerAction a){
        perClientAction = a;
        for(Client c : clients){
            if(c!=null)
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

    /**
     * Thread safe method for getting the current list of clients
     * Note: Since clients may be added or removed after the array is returned,
     *       It is possible that some of the contents of the array may become unsafe references
     * @return the currently connected clients
     * @see Server#remove(Client c)
     */
    synchronized public Client[] getClients(){
        return clients;
    }

    /**
     * Accessor for the current number of connected clients
     * @return number of connected clients
     */
    synchronized public int getClientAmount(){
        return clientCount;
    }


    /**
     * Setup a logger for the server
     * Args are passed to ServerLogger constructor
     * @param file destination of logs
     * @param lm logging mode
     * @see LoggingMode
     * @see ServerLogger
     * @see Server#logger
     */
    public void setupLogger(File file, LoggingMode lm){
        logger = new ServerLogger(this, file, lm);
    }

    /**
     * Change the logging mode of a logger
     * Args passed to ServerLogger#setMode(LoggingMode l)
     * @param lm new LoggingMode
     * @see LoggingMode
     * @see ServerLogger
     * @see Server#logger
     */
    synchronized public void setLoggingMode(LoggingMode lm){
        logger.setMode(lm);
    }

    /**
     * Accessor for the logging mode of the server
     * @return logging mode
     * @see ServerLogger
     * @see LoggingMode
     * @see Server#logger
     */
    synchronized public LoggingMode getLoggingMode(){
        return logger.getMode();
    }

    /**
     * Used to pass events to the logger
     * To create your own event types, create a class that extends Event
     * (The message instance variable is what ends up being written into the log file)
     * @see Event
     * @see ServerLogger
     * @param e new event type
     */
    synchronized public void feedLoggingEvent(Event e){
        if(e==null)return; //prevent nullpointerexception in logger
        logger.feedNewActionType(e);
    }

    /**
     * Change the size of the eventstack on the server's logger
     * (Usually the default 64 events should be more than enough)
     * @see ServerLogger
     * @see in.Kaulk.NetLib.util.Events.EventstackOverflowException
     * @param max new max amount of event objects to be held in a stack for the logger at
     *            any given time, before they are popped
     * @see Event
     */
    public void setLoggerMaxEventstackSize(int max){
        logger.setNewStackSize(max);
    }

}
