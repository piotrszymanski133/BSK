package sample.rsa_keys;

import java.io.*;

public class PrivateKeyFileManager {
    private String path;

    /**
     * Class for reading and writing encrypted private key data (private key with salt) from and to file.
     * @param path path to file in which encrypted private key data is or will be stored
     */
    public PrivateKeyFileManager(String path) {
        this.path = path;
    }

    /**
     * Writes encrypted private key data to file
     * @param byteData encrypted private key data (with salt)
     */
    public void write(EncryptedPrivateKey byteData) {
        try(FileOutputStream fo = new FileOutputStream(path);
            ObjectOutputStream oo = new ObjectOutputStream(fo)) {
            oo.writeObject(byteData);
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Reads encrypted private key data from file
     * @return Encrypted private key data (with salt)
     */
    public EncryptedPrivateKey read() {
        try(FileInputStream fi = new FileInputStream(path);
            ObjectInputStream oi = new ObjectInputStream(fi)) {
            return (EncryptedPrivateKey) oi.readObject();
        } catch(IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
