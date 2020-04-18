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
    private volatile OutputStream out;
    private volatile InputStream in;

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

    public void killThread(){
        thread.kill();
    }

    public void startAction(PerClientActionable perClientAction) {
        Thread t = new Thread(perClientAction);
        thread = perClientAction;
        t.start();
    }

    public boolean send(Message m){

        try {
            byte[] data = m.getSerialized();
            out.write(data);
        }catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

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

    public byte[] readCurrentBytes(){
        try {
            return in.readAllBytes();
        }catch (Exception e){

        }
        return new byte[0];
    }


}
