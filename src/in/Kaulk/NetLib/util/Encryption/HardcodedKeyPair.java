package in.Kaulk.NetLib.util.Encryption;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;

public class HardcodedKeyPair {

    BigInteger exp;
    BigInteger mod;

    KeyPair pair;

    HardcodedKeyPair(){

        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048, new SecureRandom());
            pair = generator.generateKeyPair();
            System.out.println(pair.getPublic());
        }catch (Exception e){
            e.printStackTrace();
        }

    }


}
