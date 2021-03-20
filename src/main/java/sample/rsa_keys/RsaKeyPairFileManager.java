package sample.rsa_keys;

import java.io.*;

public class RsaKeyPairFileManager {
    private String path;
    public RsaKeyPairFileManager(String path) {
        this.path = path;
    }
    public void write(RsaKeyPairEncryptedByteData byteData) {
        try(FileOutputStream fo = new FileOutputStream(path);
            ObjectOutputStream oo = new ObjectOutputStream(fo)) {
            oo.writeObject(byteData);
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }
    public RsaKeyPairEncryptedByteData read() {
        try(FileInputStream fi = new FileInputStream(path);
            ObjectInputStream oi = new ObjectInputStream(fi)) {
            return (RsaKeyPairEncryptedByteData) oi.readObject();
        } catch(IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
