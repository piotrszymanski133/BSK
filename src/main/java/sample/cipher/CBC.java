package sample.cipher;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class CBC implements CipherFile{

    @Override
    public byte[] encrypt(Data data, byte[] bytes){
        try{
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, data.getSecretKey(), data.getIvParameterSpec());
            return cipher.doFinal(bytes);
        }catch(Exception e) {
            System.out.println("Error with CBC mode encryption");
            System.out.println(e.getMessage());
            return null;
        }
    }

    @Override
    public byte[] decrypt(Data data, byte[] bytes){
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, data.getSecretKey(), data.getIvParameterSpec());
            return cipher.doFinal(bytes);
        } catch (Exception e) {
            System.out.println("Error with CBC mode decryption");
            System.out.println(e.getMessage());
            return null;
        }
    }
}
