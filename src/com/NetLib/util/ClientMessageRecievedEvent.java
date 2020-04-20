package com.NetLib.util;

import com.NetLib.Client;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ClientMessageRecievedEvent extends Event{

    public ClientMessageRecievedEvent(Client c, int size){
        address = c.s.getRemoteSocketAddress().toString();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        message = "Client "+ address +" sent message at "+dtf.format(now) + " of size "+ size + " bytes";
        info = size;
    }

}
