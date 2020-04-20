package com.NetLib.util;

public enum LoggingMode {
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

