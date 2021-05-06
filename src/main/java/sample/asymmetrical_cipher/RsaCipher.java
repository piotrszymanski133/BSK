package sample.asymmetrical_cipher;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;

public class RsaCipher implements AsymmetricalCipher {
    private KeyPair keyPair;
    public RsaCipher(KeyPair keyPair) {
        this.keyPair = keyPair;
    }
    public byte[] encrypt(String data) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
            return cipher.doFinal(data.getBytes());
        } catch(BadPaddingException | IllegalBlockSizeException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    public String decrypt(byte[] data) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
            return new String(cipher.doFinal(data));
        } catch(BadPaddingException | IllegalBlockSizeException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
