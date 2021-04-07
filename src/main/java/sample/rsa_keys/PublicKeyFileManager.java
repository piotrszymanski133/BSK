package sample.rsa_keys;

import java.io.*;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public class PublicKeyFileManager {
    private String path;

    /**
     * Class for reading and writing public key to and from file.
     * @param path path to file in which public key is or will be stored
     */
    public PublicKeyFileManager(String path) {
        this.path = path;
    }

    /**
     * Writes public key to previously chosen file
     * @param key user's public key
     */
    public void write(PublicKey key) {
        try(FileOutputStream fo = new FileOutputStream(path)) {
            fo.write(key.getEncoded());
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Reads public key from previously chosen file
     * @return public key read from file
     */
    public PublicKey read() {
        try(FileInputStream fi = new FileInputStream(path)) {
            byte[] encodedKey = new byte[fi.available()];
            fi.read(encodedKey);
            X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(encodedKey);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey key = keyFactory.generatePublic(pubKeySpec);
            return key;
        } catch(IOException | InvalidKeySpecException | NoSuchAlgorithmException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
