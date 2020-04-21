package in.Kaulk.NetLib.http;

import in.Kaulk.NetLib.Client;

/**
 * Functions the same as a ServerAction interface, but for HTTP request handling
 * @see in.Kaulk.NetLib.ServerAction
 * @see FunctionalInterface
 */
@FunctionalInterface
public interface HTTPResopnse {
    /**
     * Put your main content here
     *
     * Example implementation for GET and HEAD requests:
     * @see HTTPServer.HandleNormalRequests#run(Client)
     *
     * @param c Client object to send and receive data
     * @param msg the direct request from the client
     *
     */
    public void run(Client c, String msg);

}
