package in.Kaulk.NetLib.util.Encryption;


import in.Kaulk.NetLib.Message;

import java.security.*;
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
            return new Message(Base64.getEncoder().encodeToString(cipher.doFinal(msg.getSerialized()))).getSerialized();
        }catch (Exception e){
            e.printStackTrace();
        }
        return new byte[0];
    }

    synchronized public Message decrypt(Message msg){
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return new Message(cipher.doFinal(Base64.getDecoder().decode(msg.getSerialized())));
        }catch (Exception e){
            e.printStackTrace();
        }
        return new Message(0);
    }


}
