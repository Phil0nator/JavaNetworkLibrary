package com.NetLib.util;

import com.NetLib.Client;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Container subclasses for events:
 */
public class ClientConnectEvent extends Event{

    public ClientConnectEvent(Client c){
        address = c.s.getRemoteSocketAddress().toString();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        message = "Client "+ address +" Connected at "+dtf.format(now);
    }

}
