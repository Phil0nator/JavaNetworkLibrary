package in.Kaulk.NetLib.util.Events;

import in.Kaulk.NetLib.Client;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ClientDisconnectEvent extends Event{
    public ClientDisconnectEvent(Client c){
        address = c.s.getRemoteSocketAddress().toString();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        message = "Client "+ address +" Disconnected at "+dtf.format(now);
    }
}
