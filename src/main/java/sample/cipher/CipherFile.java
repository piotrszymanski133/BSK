package sample.cipher;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class CipherFile {

    public byte[] encrypt(Data data, byte[] bytes, String cipherMode){
        try{
            Cipher cipher = Cipher.getInstance("AES/" + cipherMode +"/NoPadding");
            if(cipherMode.equals("ECB")){
                cipher.init(Cipher.ENCRYPT_MODE, data.getSecretKey());
            }
            else{
                cipher.init(Cipher.ENCRYPT_MODE, data.getSecretKey(), data.getIvParameterSpec());
            }
            return cipher.doFinal(bytes);

        }catch(Exception e) {
            System.out.println("Error with " + cipherMode + " mode encryption");
            System.out.println(e.getMessage());
            return null;
        }
    }

    public byte[] decrypt(Data data, byte[] bytes, String cipherMode) {
        try {
            Cipher cipher = Cipher.getInstance("AES/" + cipherMode +"/NoPadding");
            if(cipherMode.equals("ECB")) {
                cipher.init(Cipher.DECRYPT_MODE, data.getSecretKey());
            }
            else{
                cipher.init(Cipher.DECRYPT_MODE, data.getSecretKey(), data.getIvParameterSpec());
            }
            return cipher.doFinal(bytes);

        }catch(Exception e){
            System.out.println("Error with " + cipherMode + " mode decryption");
            System.out.println(e.getMessage());
            return null;
        }
    }
}
