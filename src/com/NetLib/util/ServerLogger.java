package com.NetLib.util;


import com.NetLib.Server;

import java.io.*;
import java.time.Instant;

enum LoggingMode{
    /**
     * No logging
     */
    off,
    /**
     * Only records information about fatal errors
     */
    fatal,

    /**
     *Records information about any errors,
     *And information about each client that connects and disconnects,
     */
    verbose,
    /**
     * Records information about any errors,
     * And information about each client that connects and disconnects,
     * And also records the timestamp and size of each message relayed
     * (Can create very large files)
     */
    extra_verbose,
    /**
     * Includes information of extra_verbose, with the addition of incremental information about
     * CPU and RAM usage
     */
    debugger

}





/**
 * Basic finalized class to handle server logging
 */
public final class ServerLogger {
    /**
     * parent
     */
    private volatile Server s;

    /**
     * @see LoggingMode
     */
    private volatile LoggingMode l;

    /**
     * The thread in which logging takes place
     */
    private volatile LoggerThread thread;

    /**
     * The file in which the logging will be done
     */
    volatile File f;


    /**
     * Constructor
     * @param s parent object
     * @param l Logging level
     * @see LoggingMode
     */
    public ServerLogger(Server s,File file ,LoggingMode l){
        this.s=s;
        this.l=l;
        f=file;
    }

    /**
     * Mutator method for logging mode
     * @param l new logging mode
     */
    synchronized public void setMode(LoggingMode l){
        this.l=l;
    }

    /**
     * Accessor method for logging mode
     * @return the currently active logging mode
     */
    public LoggingMode getMode(){return l;}



    synchronized public void feedNewActionType(){

    }


    /**
     * Functional Interface to create a logging thread
     */
    private class LoggerThread implements Runnable{

        private volatile ServerLogger parent;
        /**
         * Event stack
         */
        private volatile Event[] events = new Event[64];
        /**
         * Stack tracer
         */
        private int eventCount = 0;
        private BufferedWriter writer;

        /**
         * Adds new event
         * @param e a new event object
         * @see Event
         */
        synchronized void pushEvent(Event e){
            events[eventCount]=e;
            eventCount++;
        }

        /**
         * Remove event
         * No data is actually changed, but the stack counter moves down one
         * So that the next event pushed will overwrite the unused data
         */
        private void pop(){
            eventCount--;
        }

        /**
         * Constructor
         * @param p parent
         * @throws IOException when creating a writer
         */
        LoggerThread(ServerLogger p)throws IOException{
            parent = p;
            FileWriter fw = new FileWriter(parent.f);
            writer = new BufferedWriter(fw);
        }

        /**
         * Sleep method
         * @param amount milliseconds
         */
        private void delay(int amount){
            long start = Instant.now().toEpochMilli();
            while(Instant.now().toEpochMilli()-start<amount){

            }

        }

        /**
         * determines if an event is needed based on logging level
         * @param e event
         * @return if the event is valid
         */
        private boolean getIfValid(Event e){
            LoggingMode lm = parent.getMode();
            if(lm == LoggingMode.off){return false;}
            if (lm == LoggingMode.extra_verbose){
                return true;
            }
            if(lm == LoggingMode.fatal){
                if(!(e instanceof ErrorEvent)){
                    return false;
                }
                return true;
            }else if (lm == LoggingMode.verbose){
                if(!(e instanceof ClientMessageRecievedEvent)){
                    return true;
                }
                return false;
            }
            return false;

        }

        /**
         * Main loop
         */
        public void run(){
            while(true){
                delay(1000);
                while(eventCount>0){

                    if(!getIfValid(events[eventCount])){
                        pop();
                        continue;
                    }
                    String content = events[eventCount].message;

                    try{
                        writer.write(content+"\n");
                        pop();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                try {
                    writer.flush();
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }
    }


}
