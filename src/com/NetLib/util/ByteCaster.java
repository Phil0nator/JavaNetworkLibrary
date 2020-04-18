package com.NetLib.util;

import java.io.*;

/**
 * An abstracted way to quickly transfer between bytes and serializable objects/strings
 * @see ByteArrayOutputStream
 * @see ByteArrayInputStream
 * @see ObjectOutputStream
 * @see ObjectInputStream
 */
public class ByteCaster {
    /**
     * Empty Constructor
     */
    public ByteCaster(){}

    /**
     * Takes a serializable object, and uses a buffered stream to get the byte data for that object
     * @param obj a serializable object
     * @return byte array of data
     * @see Serializable
     */
    public byte[] writeValueAsBytes(Serializable obj) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(buffer);
            oos.writeObject(obj);
            oos.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return buffer.toByteArray();
    }

    /**
     * Get an object (Type will be unspecified) from a byte array. Byte array must either be from
     * A serializable object, or a string.
     * @param data bytes
     * @return Unspecified Object
     */
    public Object getObjectFor(byte[] data){
        Object o = new Object();
        try {
            ByteArrayInputStream formattedData = new ByteArrayInputStream(data);
            ObjectInputStream ois = new ObjectInputStream(formattedData);
            o = ois.readObject();
            ois.close();
            formattedData.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return o;
    }

    /**
     * Debugging method for printing out the serialized contents of an object
     * @param o object to be debugged
     */
    public void printDataOf(Object o){
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(buffer);
            oos.writeObject(o);
            oos.close();
            buffer.close();
        }catch (Exception e){
            //e.printStackTrace();
        }
        System.out.println(buffer.toString());


    }
}
