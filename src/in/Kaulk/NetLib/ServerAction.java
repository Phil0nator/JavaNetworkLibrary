package in.Kaulk.NetLib;

/**
 * A similar structure to Runnable, with the addition of a Client object parameter
 *
 * Used to define actions taken by the server
 * @see Server#doToNewClients(ServerAction)
 * @see Server#doToEachClient(ServerAction)
 * @see Server#doOnDisconnect(ServerAction)
 */
@FunctionalInterface
public interface ServerAction{
    /**
     * Task to be taken out by the server. It is never necessary to call run yourself.
     * (When run through the server, it will be in a different thread)
     * @param c relevant Client object
     * @see Client
     */
    public void run(Client c);
}
