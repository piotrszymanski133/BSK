package sample.rsa_keys;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class RsaKeyPairPasswordEncryptor {
    private SecretKey secret;
    private byte[] salt;
    public RsaKeyPairPasswordEncryptor(String password, byte[] salt) {
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
    public RsaKeyPairEncryptedByteData encryptKeys(PublicKey pub, PrivateKey priv) {
        try {
            //convert key to byte array
            byte[] publicKeyBytes = pub.getEncoded();
            byte[] privateKeyBytes = priv.getEncoded();
            //encrypt key
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secret);
            byte[] encryptedPublicKey =  cipher.doFinal(publicKeyBytes);
            byte[] encryptedPrivateKey =  cipher.doFinal(privateKeyBytes);
            return new RsaKeyPairEncryptedByteData(this.salt, encryptedPublicKey, encryptedPrivateKey);
        } catch(Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    public KeyPair decryptKeyPair(RsaKeyPairEncryptedByteData byteData) {
        try {
            //decrypt keys
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secret);
            byte[] publicKeyBytes =  cipher.doFinal(byteData.getPublicKey());
            byte[] privateKeyBytes = cipher.doFinal(byteData.getPrivateKey());
            KeyFactory kf = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = kf.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
            PublicKey publicKey = kf.generatePublic(new X509EncodedKeySpec(publicKeyBytes));
            return new KeyPair(publicKey, privateKey);
        } catch(Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
