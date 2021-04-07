package sample.rsa_keys;

import java.io.File;
import java.io.IOException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

public class RsaKeyPairFileManager {
    private File privateKeyFile;
    private File publicKeyFile;

    /**
     * Class for reading and writing RSA key pair to and from files
     * @param dir user's directory for RSA key pair
     */
    public RsaKeyPairFileManager(String dir)
    {
        File userDir = new File(dir);
        if(!userDir.exists())
        {
            userDir.mkdirs();
        }
        try {
            privateKeyFile = new File(userDir, "key.pem");
            if (!privateKeyFile.exists()) {
                privateKeyFile.createNewFile();
            }
            publicKeyFile = new File(userDir, "key.pub");
            if(!publicKeyFile.exists()) {
                publicKeyFile.createNewFile();
            }
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Writes user's key pair to files. Private key is encrypted before saving.
     * @param keyPair user's keyPair
     * @param password user's password used for encrypting private key
     */
    public void write(KeyPair keyPair, String password)
    {
        PrivateKeyFileEncryptor fileEncryptor = new PrivateKeyFileEncryptor();
        fileEncryptor.encryptAndSaveKey(password, privateKeyFile.getAbsolutePath(), keyPair.getPrivate());
        PublicKeyFileManager publicKeyFileManager = new PublicKeyFileManager(publicKeyFile.getAbsolutePath());
        publicKeyFileManager.write(keyPair.getPublic());
    }

    /**
     * Reads user's key pair from files. Private key is decrypted after reading.
     * @param password user's password used for private key decryption
     * @return User's key pair
     */
    public KeyPair read(String password)
    {
        PrivateKeyFileEncryptor fileEncryptor = new PrivateKeyFileEncryptor();
        PrivateKey privateKey = fileEncryptor.decryptAndReadKeys(password, privateKeyFile.getAbsolutePath());
        PublicKeyFileManager publicKeyFileManager = new PublicKeyFileManager(publicKeyFile.getAbsolutePath());
        PublicKey publicKey = publicKeyFileManager.read();
        return new KeyPair(publicKey, privateKey);
    }
}
