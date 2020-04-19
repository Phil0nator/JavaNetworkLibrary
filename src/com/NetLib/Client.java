package com.NetLib;

import java.io.*;
import java.net.Socket;
import java.security.PublicKey;
import java.time.Instant;

public class Client {

    public volatile Socket s;
    public volatile Server parent;


    public volatile PublicKey publicKey;

    /**
     * In and Out fields are streams of data between server and client or client and client
     */
    private volatile DataOutputStream out;
    private volatile DataInputStream in;

    /**
     * The threaded action being done to the client
     * @see PerClientActionable
     * @see ServerAction
     */
    volatile private PerClientActionable thread;
    volatile private Thread t;

    /**
     * Create Client object for servers
     * @param p parent
     * @param s Socket
     */
    Client(Server p, Socket s){
        this.s=s;
        parent = p;
        try {
            out = new DataOutputStream(s.getOutputStream());
            in = new DataInputStream(s.getInputStream());
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public Client(String ip, int port){
        try {
            s = new Socket(ip, port);
            out = new DataOutputStream(s.getOutputStream());
            in = new DataInputStream(s.getInputStream());
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Used for clients attached to a server object
     */
    void disconnect(){
        parent.remove(this);
    }

    /**
     * Used for clients not attached to a server object
     */
    public void close(){
        try {
            s.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Kill the ServerAction running on this client
     * @see PerClientActionable
     * @see ServerAction
     * @see Client#thread;
     */
    public void killThread(){
        thread.kill();


    }

    /**
     * Start the ServerAction that will run on this client
     * @see PerClientActionable
     * @see ServerAction
     * @see Client#thread;
     * @param perClientAction
     */
    public void startAction(PerClientActionable perClientAction) {
        t = new Thread(perClientAction);
        thread = perClientAction;
        t.start();
    }

    /**
     * Send a Message object through the socket
     * @param m message object
     * @return false if successful
     */
    public boolean send(Message m){

        try {

            byte[] data = m.getSerialized();
            //if(parent.doesEncrypt){
            //    data = parent.encryptionHandler.encrypt(m);
            //}else{
            //    data = m.getSerialized();
            //}

            out.writeInt(data.length);
            out.write(data);
            out.flush();
        }catch (Exception e){
            if(parent!=null) {
                disconnect();
            }else{
                close();
            }
        }

        return false;
    }

    /**
     * Blocking method to get the next valid message coming through the socket
     * @return a new Message object
     */
    public Message getNextMessage(){


        try{
            int size = in.readInt();
            if(size<=0)return null;
            Message msg = new Message(in.readNBytes(size));
            return msg;
        }catch (Exception e){

            if(parent!=null) {
                disconnect();
            }else{
                close();
                return null;
            }
        }

        return null;
    }

    /**
     * Used to ensure that a message is not null, and will block until a valid message is recieved
     * @return a not-null Message object
     */
    public Message blockUntilNextMessage(){

        return getNextMessage();

    }


    /**
     * Read whatever bytes are currently in the input stream buffer
     * @return Unformatted bytes
     */
    public byte[] readCurrentBytes(){
        try {
            return in.readAllBytes();
        }catch (Exception e){

        }
        return new byte[0];
    }




}
