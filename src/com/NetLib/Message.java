package com.NetLib;

import com.NetLib.util.ByteCaster;

import java.io.*;

public class Message {
    private volatile Object obj;
    ByteCaster c = new ByteCaster();
    Message(Serializable o){
        obj=o;
    }
    Message(byte[] b){
        obj = c.getObjectFor(b);
    }
    Message(String s){

        obj = s;

    }
    Object getObject(){
        return obj;
    }
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
    void printSerialized(){
        c.printDataOf(this);
    }
}
