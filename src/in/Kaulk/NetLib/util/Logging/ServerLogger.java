package in.Kaulk.NetLib.util.Logging;


import com.sun.management.OperatingSystemMXBean;
import in.Kaulk.NetLib.Server;
import in.Kaulk.NetLib.util.Events.*;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.time.Instant;






/**
 * Basic finalized class to handle server logging
 */
public final class ServerLogger {

    /**
     * Number of milliseconds between each time the logger logs system info (Only on log level extra_verbose)
     * @see LoggingMode
     */
    private static final int SYSTEM_INFO_DELAY = 10000;

    private static final int MAX_EVENTSTACK_SIZE = 64;

    /**
     * Operating system information used in logging on log level extra_verbose
     * @see OperatingSystemMXBean
     * @see LoggingMode
     */
    private OperatingSystemMXBean os = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

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
    public volatile LoggerThread thread;

    /**
     * The file in which the logging will be done
     */
    volatile File f;


    /**
     * Constructor
     * @param s parent object
     * @param l Logging level
     * @param file the destination for logging
     * @see LoggingMode
     */
    public ServerLogger(Server s,File file ,LoggingMode l){
        this.s=s;
        this.l=l;
        f=file;
        if(file == null){return;}
        try{
            thread = new LoggerThread(this);
            Thread t = new Thread(thread);
            t.start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Mutator method for logging mode
     * @param l new logging mode
     */
    synchronized public void setMode(LoggingMode l){
        this.l=l;
    }

    public void setNewStackSize(int size){
        thread.overflowStackTo(size);
    }

    /**
     * Accessor method for logging mode
     * @return the currently active logging mode
     */
    public LoggingMode getMode(){return l;}


    /**
     * Feed an Event object into the LoggerThread
     * @param e new event object
     * @see Event
     * @see LoggerThread
     *
     * Called in:
     * @see Server#feedLoggingEvent(Event)
     */
    synchronized public void feedNewActionType(Event e){
        if(e==null)return;
        if(l == LoggingMode.off)return;
        thread.pushEvent(e);
    }


    /**
     * Get System Info:
     * @return overall system cpu load %
     */
    private double getSystemLoad(){
        return os.getSystemLoadAverage();
    }

    /**
     * Ram info
     * @return the amount of memory the jvm is using
     */
    private double getRamInUse() {
        return Runtime.getRuntime().freeMemory()-Runtime.getRuntime().totalMemory();
    }


    /**
     * Functional Interface to create a logging thread
     */
    private class LoggerThread implements Runnable{

        private volatile ServerLogger parent;
        /**
         * Event stack
         */
        public volatile Event[] events = new Event[MAX_EVENTSTACK_SIZE];
        /**
         * Stack tracer
         */
        private int eventCount = 0;
        private BufferedWriter writer;

        /**
         * Adds new event
         * @param e a new event object
         * @throws EventstackOverflowException when you have exceeded the eventStack limit
         * @see Event
         */
        synchronized void pushEvent(Event e) throws EventstackOverflowException {

            eventCount++;
            if(eventCount >= events.length){
                throw new EventstackOverflowException("You have exceeded the maximum number of events in your server logger");
            }
            events[eventCount]=e;

        }

        /**
         * Remove top event
         */
        private void pop(){
            //events[eventCount] = null;
            eventCount--;
        }

        /**
         * Constructor
         * @param p parent
         * @throws IOException when creating a writer
         */
        LoggerThread(ServerLogger p)throws IOException{
            parent = p;
            FileWriter fw = new FileWriter(parent.f, true);
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
         * This is done in this thread to avoid slowing down any server threads
         * @param e event
         * @return if the event is valid
         */
        private boolean getIfValid(Event e){
            if(e == null)return false;
            LoggingMode lm = parent.getMode();
            if(lm == LoggingMode.off){return false;}
            if (lm == LoggingMode.extra_verbose){
                return true;
            }
            if(lm == LoggingMode.HTTP_Standard && e instanceof HTTPRequestEvent){
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
            return true; //true by default

        }

        /**
         * Main loop
         */
        public void run(){
            long lastSystemInfoDump = Instant.now().toEpochMilli();
            while(true){




                delay(1000); //slow down thread
                while(eventCount>0){
                    //main event handling:
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

                //write system dump
                if(parent.getMode()==LoggingMode.extra_verbose) {
                    if (Instant.now().toEpochMilli() - lastSystemInfoDump > SYSTEM_INFO_DELAY) {
                        lastSystemInfoDump = Instant.now().toEpochMilli();
                        try {
                            writer.write("System Info: \n\tApprox. Ram use: " + getRamInUse() + "b\n\tApprox. SystemLoad: " + getSystemLoad() + "%\n");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                //flush buffers:
                try {
                    writer.flush();
                }catch (Exception e){
                    e.printStackTrace();
                }

            }//end mainloop
        }

        public void overflowStackTo(int newsize){
            Event[] newevents = new Event[newsize];
            int i = 0;
            for(Event e: events){
                if(e==null)break;
                newevents[i]=e;
                i++;
            }
            events = newevents;
        }

    }


}
