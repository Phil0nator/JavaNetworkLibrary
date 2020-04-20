package in.Kaulk.NetLib.util.Events;

public class ErrorEvent extends Event{

    public ErrorEvent(Exception e){
        message = e.getMessage();
    }

}
