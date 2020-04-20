package com.NetLib.util;

import com.NetLib.Client;
import com.NetLib.Server;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Abstract class event is used to provide info to a ServerLogger Object
 * @see ServerLogger
 * You can make your own event type by extending this class, and creating a
 * String in the message field in the constructor
 * @see Event#message
 * Example:
 * @see ErrorEvent
 * You can feed them to your server using:
 * @see Server#feedLoggingEvent(Event)
 * This passes them to:
 * @see ServerLogger#feedNewActionType(Event)
 * @see ServerLogger
 */
public abstract class Event {

    protected String address;
    /**
     * The message field is what gets written into the logger
     */
    protected String message;
    protected int info;

    /**
     * The constructor should always be overridden (excluding some scope requirements)
     */
    Event(){
    }

}

