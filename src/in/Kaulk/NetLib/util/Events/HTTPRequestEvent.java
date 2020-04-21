package in.Kaulk.NetLib.util.Events;

import in.Kaulk.NetLib.Client;

import java.util.Date;

/**
 * Logging event for HTTP requests
 * @see in.Kaulk.NetLib.http.HTTPServer
 * @see Event
 */
public class HTTPRequestEvent extends Event {
    /**
     * Constructor
     * @param c Client object from which the request was received
     * @param msg Request
     * @param code response code
     */
    public HTTPRequestEvent(Client c, String msg, int code){

        message = "Got request: "+msg+" } from client: "+c.s.getRemoteSocketAddress().toString();
        message+=" at "+ new Date();
        message+=".  CODE: "+ code +" was given\n";

    }


}
