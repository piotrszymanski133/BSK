package sample.rsa_keys;

import java.io.Serializable;

public class RsaKeyPairEncryptedByteData implements Serializable {
    private byte[] salt;
    private byte[] pub;
    private byte[] priv;
    public RsaKeyPairEncryptedByteData(byte[] salt, byte[] pub, byte[] priv)
    {
        this.salt = salt;
        this.pub = pub;
        this.priv = priv;
    }
    public byte[] getSalt() {
        return salt;
    }
    public byte[] getPublicKey() {
        return pub;
    }
    public byte[] getPrivateKey() {
        return priv;
    }
}
