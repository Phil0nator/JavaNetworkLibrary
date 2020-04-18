package com.NetLib.util;

import java.io.*;

public class ByteCaster {

    public ByteCaster(){}

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
