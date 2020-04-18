package com.NetLib;

import java.net.Socket;

public class Client {

    public Socket s;
    public Server parent;


    private PerClientActionable thread;
    Client(Server p, Socket s){
        this.s=s;
        parent = p;
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




}
