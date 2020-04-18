package com.NetLib;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.time.Instant;

public class Client {

    public volatile Socket s;
    public volatile Server parent;

    /**
     * In and Out fields are streams of data between server and client or client and client
     */
    private volatile OutputStream out;
    private volatile InputStream in;

    /**
     * The threaded action being done to the client
     * @see PerClientActionable
     * @see ServerAction
     */
    volatile private PerClientActionable thread;
    Client(Server p, Socket s){
        this.s=s;
        parent = p;
        try {
            out = s.getOutputStream();
            in = s.getInputStream();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void disconnect(){
        thread.kill();
        try{
            s.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        parent.remove(this);
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
        Thread t = new Thread(perClientAction);
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
            out.write(data);
        }catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Blocking method to get the next valid message coming through the socket
     * @param timeout the maximum time to wait in milliseconds
     * @return a new Message object
     */
    public Message getNextMessage(long timeout){
        long start = Instant.now().toEpochMilli();
        while((Instant.now().toEpochMilli()-start < timeout)){
            try{
                Message msg = new Message(in.readAllBytes());
                Object test = msg.getObject();
                return msg;
            }catch (Exception e){

            }
        }
        return null;
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
