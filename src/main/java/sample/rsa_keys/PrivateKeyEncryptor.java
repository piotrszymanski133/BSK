package sample.rsa_keys;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.*;

/**
 * Class for encrypting and decrypting private key
 */
public class PrivateKeyEncryptor {
    private SecretKey secret;
    private byte[] salt;

    /**
     * Class for encrypting and decrypting private key with user's password
     * @param password user's password for encryption or decryption
     * @param salt salt which will be used when creating secret for key encryption
     */
    public PrivateKeyEncryptor(String password, byte[] salt) {
        try {
            //create key from provided password
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
            SecretKey tmp = factory.generateSecret(keySpec);
            this.secret = new SecretKeySpec(tmp.getEncoded(), "AES");
            this.salt = salt;
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Encrypts private key and saves it to a special object with salt
     * @param priv private key
     * @return Object with byte data of encrypted key and salt
     */
    public EncryptedPrivateKey encryptPrivateKey(PrivateKey priv) {
        try {
            //convert key to byte array
            byte[] privateKeyBytes = priv.getEncoded();
            //encrypt key
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secret);
            byte[] encryptedPrivateKey =  cipher.doFinal(privateKeyBytes);
            return new EncryptedPrivateKey(this.salt, encryptedPrivateKey);
        } catch(Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Decrypts private key from object with encrypted private key data
     * @param byteData encrypted private key data (with salt)
     * @return decrypted private key
     */
    public PrivateKey decryptPrivateKey(EncryptedPrivateKey byteData) {
        try {
            //decrypt keys
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secret);
            byte[] privateKeyBytes;
            try {
                privateKeyBytes = cipher.doFinal(byteData.getPrivateKey());
            } catch(BadPaddingException ex) {
                privateKeyBytes = KeyPairGenerator.getInstance("RSA").generateKeyPair().getPrivate().getEncoded();
            }
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
        } catch(Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
