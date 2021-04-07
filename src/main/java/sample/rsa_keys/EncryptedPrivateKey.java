package sample.rsa_keys;

import java.io.Serializable;

/**
 * Class for storing encrypted private key and salt used for its encryption
 */
public class EncryptedPrivateKey implements Serializable {
    private byte[] salt;
    private byte[] priv;

    /**
     * Creates object for storing encrypted private key data and salt
     * @param salt salt used for encryption
     * @param priv encrypted private key bytes
     */
    public EncryptedPrivateKey(byte[] salt, byte[] priv)
    {
        this.salt = salt;
        this.priv = priv;
    }
    public byte[] getSalt() {
        return salt;
    }
    public byte[] getPrivateKey() {
        return priv;
    }
}
