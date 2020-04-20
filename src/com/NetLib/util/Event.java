package com.NetLib.util;

import com.NetLib.Client;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class Event {

    protected String address;
    protected String message;
    protected int info;


    Event(){
    }

}

