package com.NetLib.util;


import com.NetLib.Message;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.*;


public class EncryptionHandler {

    private volatile Cipher cipher;
    private volatile PrivateKey privateKey;
    private volatile PublicKey publicKey;
    private volatile KeyPair keyPair;
    private volatile KeyPairGenerator keyGenerator;

    public EncryptionHandler(int keySize){
        try {
            this.cipher = Cipher.getInstance("RSA");
            keyGenerator = KeyPairGenerator.getInstance("RSA");
            keyGenerator.initialize(keySize);
            this.keyPair = keyGenerator.generateKeyPair();
            this.privateKey = keyPair.getPrivate();
            this.publicKey = keyPair.getPublic();


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    synchronized public byte[] encrypt(Message msg){
        try {
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return Base64.getEncoder().encode(cipher.doFinal(msg.getSerialized()));
        }catch (Exception e){
            e.printStackTrace();
        }
        return new byte[0];
    }


}
