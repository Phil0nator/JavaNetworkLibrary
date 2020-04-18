package com.NetLib;

import com.NetLib.util.ByteCaster;

import java.io.*;

/**
 * Container class for messages, serialization and deserialization
 */
public class Message {


    private volatile Object obj;

    /**
     * @see ByteCaster
     */
    private ByteCaster c = new ByteCaster();

    /**
     * Create a message with a serializable object
     * @param o a serializable object
     * @see Serializable
     */
    Message(Serializable o){
        obj=o;
    }

    /**
     * Create a message based on a byte array
     * @param b bytes of either a String object or a serializable object
     * @see Serializable
     */
    Message(byte[] b){
        obj = c.getObjectFor(b);
    }

    /**
     * Create a message based on a String object
     * @param s string
     */
    Message(String s){

        obj = s;

    }

    /**
     * Can be null.
     * @return whatever the Object field of the message currently holds
     */
    Object getObject(){
        return obj;
    }

    /**
     * Gets the byte form of the message in order to send through sockets
     * @return byte array of serialized data
     * @see Serializable
     * @throws NotSerializableException
     */
    byte[] getSerialized() throws NotSerializableException{
        if(obj instanceof Serializable) {
            try {
                return c.writeValueAsBytes((Serializable) obj);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if (obj instanceof String) {
            return ((String) obj).getBytes();
        }else{
            throw new NotSerializableException("the object was not serializable");
        }
        return new byte[0];
    }

    /**
     * Debugging method for printing the serialized contents of the message
     * @see ByteCaster#printDataOf(Object)
     */
    private synchronized void printSerialized(){
        c.printDataOf(this);
    }
}
