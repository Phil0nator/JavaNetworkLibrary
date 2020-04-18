package com.NetLib;

import com.NetLib.util.ByteCaster;

import java.io.*;

public class Message {

    private Object obj;
    ByteCaster c = new ByteCaster();
    Message(Serializable o){
        obj=o;
    }
    Message(byte[] b){
        obj = c.getObjectFor(b);
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
        }else{
            throw new NotSerializableException("the object was not serializable");
        }
        return new byte[0];
    }

    void printSerialized(){
        c.printDataOf(this);
    }
}
