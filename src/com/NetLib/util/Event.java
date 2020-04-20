package com.NetLib.util;

import com.NetLib.Client;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class Event {

    protected String address;
    protected String message;
    protected int info;


    Event(){
    }

}

/**
 * Container subclasses for events:
 */
class ClientConnectEvent extends Event{

    ClientConnectEvent(Client c){
        address = c.s.getRemoteSocketAddress().toString();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        message = "Client "+ address +" Connected at "+dtf.format(now);
    }

}
class ClientDisconnectEvent extends Event{
    ClientDisconnectEvent(Client c){
        address = c.s.getRemoteSocketAddress().toString();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        message = "Client "+ address +" Disconnected at "+dtf.format(now);
    }
}
class ClientMessageRecievedEvent extends Event{

    ClientMessageRecievedEvent(Client c, int size){
        address = c.s.getRemoteSocketAddress().toString();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        message = "Client "+ address +" sent message at "+dtf.format(now) + "of size "+ size + "bytes";
        info = size;
    }

}
class ErrorEvent extends Event{

    ErrorEvent(Exception e){
        message = e.getMessage();
    }

}