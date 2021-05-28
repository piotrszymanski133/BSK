package sample.cipher;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;

public class ECB implements CipherFile{

    @Override
    public byte[] encrypt(Data data, byte[] bytes){
        try{
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, data.getSecretKey());
            return cipher.doFinal(bytes);

        }catch(Exception e) {
            System.out.println("Error with ECB mode encryption");
            System.out.println(e.getMessage());
            return null;
        }
    }

    @Override
    public byte[] decrypt(Data data, byte[] bytes) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, data.getSecretKey());
            return cipher.doFinal(bytes);

        }catch(Exception e){
            System.out.println("Error with ECB mode decryption");
            System.out.println(e.getMessage());
            return null;
        }
    }
}
