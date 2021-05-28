package sample.cipher;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class OFB implements  CipherFile{

    @Override
    public byte[] encrypt(Data data, byte[] bytes){
        try{
            Cipher cipher = Cipher.getInstance("AES/OFB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, data.getSecretKey(), data.getIvParameterSpec());
            return cipher.doFinal(bytes);
        }catch(Exception e) {
            System.out.println("Error with OFB mode encryption");
        }
        return null;
    }

    @Override
    public byte[] decrypt(Data data, byte[] bytes) {
        try {
            Cipher cipher = Cipher.getInstance("AES/OFB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, data.getSecretKey(), data.getIvParameterSpec());
            return cipher.doFinal(bytes);
        } catch (Exception e) {
            System.out.println("Error with OFB mode decryption");
            System.out.println(e.getMessage());
            return null;
        }
    }
}
