package com.NetLib.util;

public class ErrorEvent extends Event{

    public ErrorEvent(Exception e){
        message = e.getMessage();
    }

}
