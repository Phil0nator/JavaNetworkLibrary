package in.Kaulk.NetLib.http;

import in.Kaulk.NetLib.Client;

/**
 * Functions the same as a ServerAction interface, but for HTTP request handling
 * @see in.Kaulk.NetLib.ServerAction
 * @see FunctionalInterface
 */
@FunctionalInterface
public interface HTTPResopnse {

    public void run(Client c, String msg);

}
