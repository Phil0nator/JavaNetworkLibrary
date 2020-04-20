package in.Kaulk.NetLib.util.Events;

/**
 * Used when the event stack in a ServerLogger has exceeded its pre-set size
 * @see in.Kaulk.NetLib.util.Logging.ServerLogger
 */
public class EventstackOverflowException extends RuntimeException {
    public EventstackOverflowException(String msg){
        super(msg);
    }
}
